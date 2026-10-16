package com.gestionmedica.persistence.entity;
import jakarta.persistence.*;
@Entity @Table(name="consultations")
public class ConsultationEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false) private String patientId;
    @Column(nullable=false) private String date; @Column(nullable=false) private String diagnosis; @Column(nullable=false,length=4000) private String notes;
    protected ConsultationEntity() {}
    public ConsultationEntity(String patientId,String date,String diagnosis,String notes){this.patientId=patientId;this.date=date;this.diagnosis=diagnosis;this.notes=notes;}
    public Long getId(){return id;} public String getPatientId(){return patientId;} public String getDate(){return date;} public String getDiagnosis(){return diagnosis;} public String getNotes(){return notes;}
}
