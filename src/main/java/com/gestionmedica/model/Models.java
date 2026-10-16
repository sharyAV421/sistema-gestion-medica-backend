package com.gestionmedica.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public final class Models {
    private Models() {}
    public record Patient(String id, String name, String document, String email, int age, List<String> allergies) {}
    public record Doctor(String id, String name, String specialty, List<LocalDateTime> availableSlots) {}
    public record Appointment(String id, String patientId, String doctorId, String specialty, LocalDateTime date, String status) {}
    public record Consultation(String date, String diagnosis, String notes) {}
    public record Prescription(String id, String patientId, List<Medication> medications, String createdAt) {}
    public record Medication(@NotBlank String name, @NotBlank String dose, @Positive int durationDays) {}
    public record LabResult(String name, double value, String unit, double min, double max, boolean withinRange) {}
    public record LabOrder(String id, String patientId, List<LabResult> results, String createdAt) {}
}
