package com.gestionmedica.controller;

import com.gestionmedica.dto.ApiDtos.*;
import com.gestionmedica.exception.ApiException;
import com.gestionmedica.facade.ClinicaFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/clinica")
public class ClinicaController {
    private final ClinicaFacade facade;
    public ClinicaController(ClinicaFacade facade) { this.facade=facade; }
    @PostMapping("/paciente") @ResponseStatus(HttpStatus.CREATED) public PatientResponse patient(@Valid @RequestBody PatientRequest request) { return facade.register(request); }
    @PostMapping("/cita") @ResponseStatus(HttpStatus.CREATED) public AppointmentResponse appointment(@Valid @RequestBody AppointmentRequest r, HttpServletRequest request) { own(request,r.patientId()); return facade.agendarCita(r.patientId(),r.specialty(),r.date()); }
    @GetMapping("/historia/{pacienteId}") public HistoryResponse history(@PathVariable String pacienteId,HttpServletRequest request) { own(request,pacienteId); return facade.verHistoriaCompleta(pacienteId); }
    @PostMapping("/prescripcion") @ResponseStatus(HttpStatus.CREATED) public PrescriptionResponse prescription(@Valid @RequestBody PrescriptionRequest r,HttpServletRequest request) { own(request,r.patientId()); return facade.generarPrescripcion(r.patientId(),r.medications()); }
    @PostMapping("/laboratorio") @ResponseStatus(HttpStatus.CREATED) public LabOrderResponse laboratory(@Valid @RequestBody LaboratoryRequest r,HttpServletRequest request) { own(request,r.patientId()); return facade.solicitarExamenes(r.patientId(),r.exams()); }
    @GetMapping("/medicos") public List<DoctorResponse> doctors(@RequestParam(required=false) String especialidad) { return facade.doctors(especialidad); }
    @GetMapping("/medicos/{doctorId}/horarios") public List<LocalDateTime> availability(@PathVariable String doctorId) { return facade.availableSlots(doctorId); }
    @DeleteMapping("/citas/{id}") public AppointmentItemResponse cancel(@PathVariable String id,HttpServletRequest request) { return facade.cancelAppointment((String)request.getAttribute("patientId"),id); }
    @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED) public AuthResponse register(@Valid @RequestBody RegisterRequest r) { return facade.registerAccount(r); }
    @PostMapping("/auth/login") public AuthResponse login(@Valid @RequestBody LoginRequest r) { return facade.login(r); }
    private void own(HttpServletRequest request,String id) { if (!id.equals(request.getAttribute("patientId"))) throw ApiException.unauthorized("No puedes acceder a los datos de otro paciente"); }
}
