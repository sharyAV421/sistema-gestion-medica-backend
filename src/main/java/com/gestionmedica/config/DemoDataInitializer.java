package com.gestionmedica.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gestionmedica.model.Models.*;
import com.gestionmedica.persistence.entity.*;
import com.gestionmedica.persistence.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;

/** Carga los datos demo una sola vez por clave y no sustituye datos existentes. */
@Component
public class DemoDataInitializer implements ApplicationRunner {
    private final PatientRepository patients; private final DoctorRepository doctors; private final AppointmentRepository appointments;
    private final ConsultationRepository consultations; private final PrescriptionRepository prescriptions; private final LabOrderRepository labs; private final ObjectMapper json;
    public DemoDataInitializer(PatientRepository patients,DoctorRepository doctors,AppointmentRepository appointments,ConsultationRepository consultations,PrescriptionRepository prescriptions,LabOrderRepository labs,ObjectMapper json){this.patients=patients;this.doctors=doctors;this.appointments=appointments;this.consultations=consultations;this.prescriptions=prescriptions;this.labs=labs;this.json=json;}
    @Override @Transactional public void run(ApplicationArguments args) throws Exception {
        LocalDateTime base=LocalDate.now().plusDays(1).atTime(9,0);
        seedDoctor("d1","Ana Torres","Cardiología",base);seedDoctor("d2","Luis Pérez","Cardiología",base.plusHours(2));seedDoctor("d3","Marta Ruiz","Pediatría",base.plusDays(1));seedDoctor("d4","Carlos Gómez","Pediatría",base.plusDays(1).plusHours(2));seedDoctor("d5","Elena Díaz","Dermatología",base.plusDays(2));
        seedPatient(new Patient("p-demo","Paciente Demo","DEMO-001","demo@clinica.test",35,List.of("penicilina")));
        seedPatient(new Patient("p-demo-2","Lucía Muestra","DEMO-002","lucia@clinica.test",28,List.of()));
        seedPatient(new Patient("p-demo-3","Mateo Ejemplo","DEMO-003","mateo@clinica.test",11,List.of("ibuprofeno")));
        seedConsultation("p-demo",LocalDate.now().minusDays(30).toString(),"Control general","Paciente estable");
        seedConsultation("p-demo-2",LocalDate.now().minusDays(12).toString(),"Dermatitis leve","Control ambulatorio");
        seedConsultation("p-demo-3",LocalDate.now().minusDays(8).toString(),"Control pediátrico","Sin hallazgos relevantes");
        seedAppointment("a-demo","p-demo","d1","Cardiología",LocalDate.now().minusDays(10).atTime(9,0));
        seedAppointment("a-demo-2","p-demo-2","d5","Dermatología",LocalDate.now().minusDays(6).atTime(10,0));
        seedAppointment("a-demo-3","p-demo-3","d3","Pediatría",LocalDate.now().minusDays(3).atTime(11,0));
        if(!prescriptions.existsById("rx-demo"))prescriptions.save(new PrescriptionEntity("rx-demo","p-demo",LocalDate.now().minusDays(30).toString(),json.writeValueAsString(List.of(new Medication("Vitamina D","1000 UI",30)))));
        if(!labs.existsById("lab-demo"))seedLab("lab-demo","p-demo",LocalDate.now().minusDays(20).toString(),List.of(new LabResult("Hemograma",14.2,"g/dL",12,17,true)));
        if(!labs.existsById("lab-demo-2"))seedLab("lab-demo-2","p-demo-2",LocalDate.now().minusDays(15).toString(),List.of(new LabResult("Glicemia",91,"mg/dL",70,100,true)));
    }
    private void seedDoctor(String id,String name,String specialty,LocalDateTime first)throws Exception{
        List<LocalDateTime> slots=List.of(first,first.plusDays(7),first.plusDays(14));
        DoctorEntity doctor=doctors.findById(id).orElse(null);
        if(doctor==null)doctors.save(new DoctorEntity(id,name,specialty,json.writeValueAsString(slots)));
        else if(json.<List<LocalDateTime>>readValue(doctor.getAvailableSlotsJson(),new TypeReference<>(){}).stream().noneMatch(slot->slot.isAfter(LocalDateTime.now()))){doctor.setAvailableSlotsJson(json.writeValueAsString(slots));}
    }
    private void seedPatient(Patient p)throws Exception{if(!patients.existsById(p.id()))patients.save(new PatientEntity(p.id(),p.name(),p.document(),p.email(),p.age(),json.writeValueAsString(p.allergies())));}
    private void seedConsultation(String patient,String date,String diagnosis,String notes){if(consultations.findByPatientId(patient).isEmpty())consultations.save(new ConsultationEntity(patient,date,diagnosis,notes));}
    private void seedAppointment(String id,String patient,String doctor,String specialty,LocalDateTime date){if(!appointments.existsById(id))appointments.save(new AppointmentEntity(id,patient,doctor,specialty,date,"COMPLETED"));}
    private void seedLab(String id,String patient,String createdAt,List<LabResult> results){LabOrderEntity order=new LabOrderEntity(id,patient,createdAt);results.forEach(r->order.addResult(new LabResultEntity(r.name(),r.value(),r.unit(),r.min(),r.max(),r.withinRange())));labs.save(order);}
}
