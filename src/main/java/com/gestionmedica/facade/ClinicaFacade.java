package com.gestionmedica.facade;

import com.gestionmedica.dto.ApiDtos.*;
import com.gestionmedica.model.Models.*;
import com.gestionmedica.service.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import com.gestionmedica.exception.ApiException;

@Component
public class ClinicaFacade {
    private final PatientService patients; private final AgendaService agenda; private final MedicalHistoryService history; private final PrescriptionService prescriptions; private final LaboratoryService laboratory; private final AuthService auth;
    public ClinicaFacade(PatientService patients, AgendaService agenda, MedicalHistoryService history, PrescriptionService prescriptions, LaboratoryService laboratory, AuthService auth) { this.patients=patients; this.agenda=agenda; this.history=history; this.prescriptions=prescriptions; this.laboratory=laboratory; this.auth=auth; }
    public PatientResponse register(PatientRequest r) { Patient p=patients.register(r.name(),r.document(),r.email(),r.age(),r.allergies()); return response(p); }
    public AppointmentResponse agendarCita(String patientId, String specialty, LocalDateTime date) { Patient p=patients.get(patientId); Appointment a=agenda.book(p.id(),specialty,date); return new AppointmentResponse(appointmentResponse(a),doctorResponse(agenda.doctor(a.doctorId())),"Recordatorio: cita el " + date); }
    public HistoryResponse verHistoriaCompleta(String patientId) {
        Patient p=patients.get(patientId);
        List<AppointmentItemResponse> past=agenda.appointments(patientId).stream().filter(a -> a.date().isBefore(LocalDateTime.now()) && !a.status().equals("CANCELLED")).map(this::appointmentResponse).toList();
        List<ConsultationResponse> consultations=history.consultations(patientId).stream().map(c -> new ConsultationResponse(c.date(),c.diagnosis(),c.notes())).toList();
        List<PrescriptionResponse> rx=prescriptions.forPatient(patientId).stream().map(this::prescriptionResponse).toList();
        List<LabOrderResponse> labs=laboratory.forPatient(patientId).stream().map(this::labResponse).toList();
        return new HistoryResponse(response(p),consultations,past,rx,labs);
    }
    public PrescriptionResponse generarPrescripcion(String patientId, List<Medication> meds) { patients.get(patientId); List<String> allergies=history.allergies(patientId); return prescriptionResponse(prescriptions.create(patientId,meds,allergies)); }
    public LabOrderResponse solicitarExamenes(String patientId, List<String> exams) { patients.get(patientId); return labResponse(laboratory.request(patientId,exams)); }
    public List<DoctorResponse> doctors(String specialty) { return agenda.doctors(specialty).stream().map(this::doctorResponse).toList(); }
    public List<LocalDateTime> availableSlots(String doctorId) { return agenda.available(doctorId); }
    public AppointmentItemResponse cancelAppointment(String patientId, String appointmentId) {
        patients.get(patientId);
        Appointment a=agenda.appointments(patientId).stream().filter(x -> x.id().equals(appointmentId)).findFirst().orElseThrow(() -> ApiException.notFound("Cita no encontrada"));
        return appointmentResponse(agenda.cancel(a.id()));
    }
    public AuthResponse registerAccount(RegisterRequest request) { String token=auth.register(request.patientId(),request.email(),request.password()); return new AuthResponse(token,request.patientId(),"Cuenta creada"); }
    public AuthResponse login(LoginRequest request) { String token=auth.login(request.email(),request.password()); return new AuthResponse(token,auth.patientForToken(token),"Inicio de sesión correcto"); }
    private PatientResponse response(Patient p) { return new PatientResponse(p.id(),p.name(),p.document(),p.email(),p.age(),p.allergies()); }
    private DoctorResponse doctorResponse(Doctor d) { return new DoctorResponse(d.id(),d.name(),d.specialty()); }
    private AppointmentItemResponse appointmentResponse(Appointment a) { return new AppointmentItemResponse(a.id(),a.patientId(),a.doctorId(),a.specialty(),a.date(),a.status()); }
    private PrescriptionResponse prescriptionResponse(Prescription p) { return new PrescriptionResponse(p.id(),p.patientId(),p.medications().stream().map(m -> new MedicationResponse(m.name(),m.dose(),m.durationDays())).toList(),p.createdAt()); }
    private LabOrderResponse labResponse(LabOrder lab) { return new LabOrderResponse(lab.id(),lab.patientId(),lab.results().stream().map(r -> new LabResultResponse(r.name(),r.value(),r.unit(),r.min(),r.max(),r.withinRange())).toList(),lab.createdAt()); }
}
