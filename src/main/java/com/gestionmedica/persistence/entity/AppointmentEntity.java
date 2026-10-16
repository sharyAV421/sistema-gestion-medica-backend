package com.gestionmedica.persistence.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="appointments")
public class AppointmentEntity {
    @Id private String id; @Column(nullable=false) private String patientId; @Column(nullable=false) private String doctorId;
    @Column(nullable=false) private String specialty; @Column(nullable=false) private LocalDateTime date; @Column(nullable=false) private String status;
    protected AppointmentEntity() {}
    public AppointmentEntity(String id,String patientId,String doctorId,String specialty,LocalDateTime date,String status){this.id=id;this.patientId=patientId;this.doctorId=doctorId;this.specialty=specialty;this.date=date;this.status=status;}
    public String getId(){return id;} public String getPatientId(){return patientId;} public String getDoctorId(){return doctorId;} public String getSpecialty(){return specialty;} public LocalDateTime getDate(){return date;} public String getStatus(){return status;} public void setStatus(String status){this.status=status;}
}
