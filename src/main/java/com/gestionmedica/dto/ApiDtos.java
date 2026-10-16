package com.gestionmedica.dto;

import com.gestionmedica.model.Models.*;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}
    public record PatientRequest(@NotBlank String name, @NotBlank String document, @Email @NotBlank String email, @Min(0) @Max(120) int age, List<@NotBlank String> allergies) {}
    public record PatientResponse(String id, String name, String document, String email, int age, List<String> allergies) {}
    public record AppointmentRequest(@NotBlank String patientId, @NotBlank String specialty, @NotNull @Future LocalDateTime date) {}
    public record DoctorResponse(String id, String name, String specialty) {}
    public record AppointmentItemResponse(String id, String patientId, String doctorId, String specialty, LocalDateTime date, String status) {}
    public record AppointmentResponse(AppointmentItemResponse appointment, DoctorResponse doctor, String reminder) {}
    public record PrescriptionRequest(@NotBlank String patientId, @NotEmpty List<@Valid Medication> medications) {}
    public record LaboratoryRequest(@NotBlank String patientId, @NotEmpty List<@NotBlank String> exams) {}
    public record ConsultationResponse(String date, String diagnosis, String notes) {}
    public record MedicationResponse(String name, String dose, int durationDays) {}
    public record PrescriptionResponse(String id, String patientId, List<MedicationResponse> medications, String createdAt) {}
    public record LabResultResponse(String name, double value, String unit, double referenceMin, double referenceMax, boolean withinRange) {}
    public record LabOrderResponse(String id, String patientId, List<LabResultResponse> results, String createdAt) {}
    public record HistoryResponse(PatientResponse patient, List<ConsultationResponse> consultations, List<AppointmentItemResponse> pastAppointments, List<PrescriptionResponse> prescriptions, List<LabOrderResponse> laboratoryResults) {}
    public record RegisterRequest(@NotBlank String patientId, @Email @NotBlank String email, @NotBlank @Size(min=8) String password) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String patientId, String message) {}
    public record ErrorResponse(String message, int status) {}
    public record CancelRequest(@NotBlank String patientId) {}
}
