package com.gestionmedica;

import com.gestionmedica.dto.ApiDtos.*;
import com.gestionmedica.exception.ApiException;
import com.gestionmedica.facade.ClinicaFacade;
import com.gestionmedica.model.Models.*;
import com.gestionmedica.service.*;
import com.gestionmedica.config.DemoDataInitializer;
import com.gestionmedica.persistence.repository.*;
import org.springframework.boot.DefaultApplicationArguments;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"CORS_ALLOWED_ORIGINS=https://frontend.example", "PORT=9090", "spring.datasource.url=jdbc:h2:mem:clinica;DB_CLOSE_DELAY=-1", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=update"}) @AutoConfigureMockMvc
class ClinicaApplicationTests {
    @Autowired PatientService patients; @Autowired AgendaService agenda; @Autowired ClinicaFacade facade; @Autowired PrescriptionService prescriptions; @Autowired LaboratoryService laboratory; @Autowired AuthService auth; @Autowired MockMvc mvc; @Autowired Environment environment;
    @Autowired DemoDataInitializer demoData; @Autowired PatientRepository patientRepository; @Autowired DoctorRepository doctorRepository; @Autowired AppointmentRepository appointmentRepository; @Autowired ConsultationRepository consultationRepository; @Autowired PrescriptionRepository prescriptionRepository; @Autowired LabOrderRepository labOrderRepository; @Autowired LabResultRepository labResultRepository;
    @PersistenceContext EntityManager entityManager;
    private Patient patient(String suffix, List<String> allergies) { return patients.register("Test " + suffix,"DOC-"+suffix,"test"+suffix+"@example.test",30,allergies); }
    @Test void registersPatientAndRejectsDuplicateDocumentAndEmail() { Patient p=patient("one",List.of()); assertNotNull(patients.get(p.id())); assertThrows(ApiException.class,()->patients.register("Other","DOC-one","other@example.test",25,List.of())); assertThrows(ApiException.class,()->patients.register("Other","DOC-other","testone@example.test",25,List.of())); }
    @Test void filtersDoctorsBySpecialtyAndSeedsThreeSpecialtiesAndFiveDoctors() { assertTrue(agenda.doctors("Pediatría").size()>=2); assertEquals(0,agenda.doctors("Neurología").size()); assertTrue(agenda.doctors(null).size()>=5); assertTrue(agenda.doctors(null).stream().map(Doctor::specialty).collect(java.util.stream.Collectors.toSet()).containsAll(Set.of("Cardiología","Pediatría","Dermatología"))); }
    @Test void demoDataInitializationIsIdempotent() throws Exception { long[] before={patientRepository.count(),doctorRepository.count(),appointmentRepository.count(),consultationRepository.count(),prescriptionRepository.count(),labOrderRepository.count(),labResultRepository.count()}; demoData.run(new DefaultApplicationArguments(new String[0])); assertArrayEquals(before,new long[]{patientRepository.count(),doctorRepository.count(),appointmentRepository.count(),consultationRepository.count(),prescriptionRepository.count(),labOrderRepository.count(),labResultRepository.count()}); }
    @Test void booksAppointmentAndPreventsDuplicateSlot() { Patient p=patient("book",List.of()); LocalDateTime slot=agenda.available("d1").get(0); facade.agendarCita(p.id(),"Cardiología",slot); assertThrows(ApiException.class,()->facade.agendarCita(p.id(),"Cardiología",slot)); }
    @Test void consolidatesCompleteHistorySections() { HistoryResponse h=facade.verHistoriaCompleta("p-demo"); assertEquals("p-demo",h.patient().id()); assertFalse(h.consultations().isEmpty()); assertTrue(h.pastAppointments().stream().anyMatch(a -> a.status().equals("COMPLETED"))); assertFalse(h.prescriptions().isEmpty()); assertFalse(h.laboratoryResults().isEmpty()); }
    @Test void rejectsPrescriptionAgainstRegisteredAllergyBeforePersisting() { Patient p=patient("allergy",List.of("penicilina")); assertThrows(ApiException.class,()->facade.generarPrescripcion(p.id(),List.of(new Medication("Penicilina","500 mg",5)))); assertTrue(prescriptions.forPatient(p.id()).isEmpty()); }
    @Test void generatesLabResultsWithValuesAndReferenceRanges() { Patient p=patient("lab",List.of()); com.gestionmedica.dto.ApiDtos.LabOrderResponse result=facade.solicitarExamenes(p.id(),List.of("hemograma","glicemia","perfil lipídico")); assertEquals(3,result.results().size()); assertTrue(result.results().stream().allMatch(x -> x.value()>=0 && x.referenceMin()<=x.referenceMax() && x.unit()!=null && x.withinRange()==(x.value()>=x.referenceMin() && x.value()<=x.referenceMax()))); }
    @Test void authenticationRegistersLogsInRejectsWrongPasswordAndDuplicateAccounts() { Patient p=patient("auth",List.of()); String token=auth.register(p.id(),p.email(),"secure-pass-123"); assertEquals(p.id(),auth.patientForToken(token)); assertEquals(p.id(),auth.patientForToken(auth.login(p.email(),"secure-pass-123"))); assertThrows(ApiException.class,()->auth.login(p.email(),"wrong-password")); assertThrows(ApiException.class,()->auth.register(p.id(),p.email(),"another-pass-123")); }
    @Test @Transactional void patientAllergiesPrescriptionsLabsAndCredentialsSurvivePersistenceContextClear() { Patient p=patient("persist",List.of()); auth.register(p.id(),p.email(),"persist-pass-123"); patients.addAllergy(p.id(),"latex"); prescriptions.create(p.id(),List.of(new Medication("Paracetamol","500 mg",3)),patients.allergies(p.id())); laboratory.request(p.id(),List.of("glicemia")); entityManager.flush(); entityManager.clear(); assertTrue(patients.get(p.id()).allergies().contains("latex")); assertEquals(1,prescriptions.forPatient(p.id()).size()); assertEquals(1,laboratory.forPatient(p.id()).get(0).results().size()); assertEquals(p.id(),auth.patientForToken(auth.login(p.email(),"persist-pass-123"))); }
    @Test void httpPatientAndFacadeRoutesEnforceOwnership() throws Exception {
        String suffix=java.util.UUID.randomUUID().toString();
        String patientJson="{\"name\":\"HTTP Patient\",\"document\":\"HTTP-"+suffix+"\",\"email\":\"http-"+suffix+"@example.test\",\"age\":29,\"allergies\":[]}";
        String patientId=mvc.perform(post("/api/clinica/paciente").contentType(MediaType.APPLICATION_JSON).content(patientJson)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        com.fasterxml.jackson.databind.JsonNode created=new com.fasterxml.jackson.databind.ObjectMapper().readTree(patientId);
        String id=created.get("id").asText(); String email=created.get("email").asText();
        String register="{\"patientId\":\""+id+"\",\"email\":\""+email+"\",\"password\":\"secure-http-123\"}";
        String tokenResponse=mvc.perform(post("/api/clinica/auth/register").contentType(MediaType.APPLICATION_JSON).content(register)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String token=new com.fasterxml.jackson.databind.ObjectMapper().readTree(tokenResponse).get("token").asText();
        mvc.perform(get("/api/clinica/historia/"+id).header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.patient.id").value(id));
        mvc.perform(get("/api/clinica/historia/p-demo").header("Authorization","Bearer "+token)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/clinica/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\""+email+"\",\"password\":\"secure-http-123\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.patientId").value(id));
    }
    @Test void malformedJsonReturnsBadRequestAndDoctorsUseResponseDto() throws Exception {
        mvc.perform(post("/api/clinica/paciente").contentType(MediaType.APPLICATION_JSON).content("{" )).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(get("/api/clinica/medicos").param("especialidad","Pediatría")).andExpect(status().isOk()).andExpect(jsonPath("$[0].specialty").value("Pediatría"));
    }
    @Test void corsPortAndRailwayStartConfigurationArePresent() throws Exception {
        mvc.perform(options("/api/clinica/historia/p-demo").header("Origin","https://frontend.example").header("Access-Control-Request-Method","GET"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","https://frontend.example"));
        assertEquals("9090",environment.getProperty("server.port"));
        assertTrue(java.nio.file.Files.readString(java.nio.file.Path.of("Procfile")).contains("java -jar target/sistema-gestion-medica-1.0.0.jar"));
    }
    @Test void authenticatedHttpWorkflowBooksPrescribesOrdersAndCancels() throws Exception {
        Patient p=patient("workflow",List.of()); String token=auth.register(p.id(),p.email(),"secure-flow-123");
        LocalDateTime slot=agenda.available("d1").get(0); String authorization="Bearer "+token;
        mvc.perform(get("/api/clinica/medicos/d1/horarios").header("Authorization",authorization)).andExpect(status().isOk()).andExpect(jsonPath("$[0]").exists());
        String appointmentJson="{\"patientId\":\""+p.id()+"\",\"specialty\":\"Cardiología\",\"date\":\""+slot+"\"}";
        String appointmentBody=mvc.perform(post("/api/clinica/cita").header("Authorization",authorization).contentType(MediaType.APPLICATION_JSON).content(appointmentJson)).andExpect(status().isCreated()).andExpect(jsonPath("$.doctor.specialty").value("Cardiología")).andReturn().getResponse().getContentAsString();
        String appointmentId=new com.fasterxml.jackson.databind.ObjectMapper().readTree(appointmentBody).get("appointment").get("id").asText();
        mvc.perform(post("/api/clinica/prescripcion").header("Authorization",authorization).contentType(MediaType.APPLICATION_JSON).content("{\"patientId\":\""+p.id()+"\",\"medications\":[{\"name\":\"Paracetamol\",\"dose\":\"500 mg\",\"durationDays\":3}]}" )).andExpect(status().isCreated()).andExpect(jsonPath("$.medications[0].name").value("Paracetamol"));
        mvc.perform(post("/api/clinica/laboratorio").header("Authorization",authorization).contentType(MediaType.APPLICATION_JSON).content("{\"patientId\":\""+p.id()+"\",\"exams\":[\"glicemia\"]}" )).andExpect(status().isCreated()).andExpect(jsonPath("$.results[0].referenceMin").value(70.0));
        mvc.perform(delete("/api/clinica/citas/"+appointmentId).header("Authorization",authorization)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED"));
    }
    @Test void httpValidationAndPrivateRouteAreEnforced() throws Exception {
        mvc.perform(post("/api/clinica/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"bad\",\"password\":\"x\"}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/clinica/historia/p-demo")).andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }
}
