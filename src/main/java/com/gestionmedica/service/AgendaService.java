package com.gestionmedica.service;

import com.fasterxml.jackson.core.type.TypeReference; import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmedica.exception.ApiException; import com.gestionmedica.model.Models.*;
import com.gestionmedica.persistence.entity.*; import com.gestionmedica.persistence.repository.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime; import java.util.*;
@Service
public class AgendaService {
    private final DoctorRepository doctors; private final AppointmentRepository appointments; private final ObjectMapper json;
    public AgendaService(DoctorRepository doctors,AppointmentRepository appointments,ObjectMapper json){this.doctors=doctors;this.appointments=appointments;this.json=json;}
    @Transactional(readOnly=true) public List<Doctor> doctors(String specialty){return (specialty==null?doctors.findAll():doctors.findBySpecialtyIgnoreCase(specialty)).stream().map(this::toModel).toList();}
    @Transactional(readOnly=true) public Doctor doctor(String id){return doctors.findById(id).map(this::toModel).orElseThrow(()->ApiException.notFound("Médico no encontrado"));}
    @Transactional(readOnly=true) public List<LocalDateTime> available(String doctorId){return doctor(doctorId).availableSlots().stream().filter(s->!appointments.existsByDoctorIdAndDateAndStatus(doctorId,s,"SCHEDULED")).toList();}
    @Transactional public synchronized Appointment book(String patientId,String specialty,LocalDateTime date){Doctor doctor=doctors(specialty).stream().filter(d->available(d.id()).contains(date)).findFirst().orElseThrow(()->ApiException.conflict("No hay disponibilidad para la fecha solicitada"));if(appointments.existsByPatientIdAndDateAndStatus(patientId,date,"SCHEDULED"))throw ApiException.conflict("El paciente ya tiene una cita en ese horario");return toModel(appointments.save(new AppointmentEntity(UUID.randomUUID().toString(),patientId,doctor.id(),doctor.specialty(),date,"SCHEDULED")));}
    @Transactional public synchronized Appointment cancel(String id){AppointmentEntity a=appointments.findById(id).orElseThrow(()->ApiException.notFound("Cita no encontrada"));a.setStatus("CANCELLED");return toModel(a);}
    @Transactional(readOnly=true) public List<Appointment> appointments(String patientId){return appointments.findByPatientId(patientId).stream().map(this::toModel).toList();}
    private Appointment toModel(AppointmentEntity a){return new Appointment(a.getId(),a.getPatientId(),a.getDoctorId(),a.getSpecialty(),a.getDate(),a.getStatus());}
    private Doctor toModel(DoctorEntity d){try{return new Doctor(d.getId(),d.getName(),d.getSpecialty(),json.readValue(d.getAvailableSlotsJson(),new TypeReference<>(){}));}catch(Exception e){throw new IllegalStateException("No se pudieron leer horarios médicos",e);}}
}
