package com.gestionmedica.persistence.entity;
import jakarta.persistence.*;
@Entity @Table(name="prescriptions")
public class PrescriptionEntity {
    @Id private String id; @Column(nullable=false) private String patientId; @Column(nullable=false) private String createdAt;
    @Column(nullable=false,columnDefinition="TEXT") private String medicationsJson;
    protected PrescriptionEntity() {}
    public PrescriptionEntity(String id,String patientId,String createdAt,String medicationsJson){this.id=id;this.patientId=patientId;this.createdAt=createdAt;this.medicationsJson=medicationsJson;}
    public String getId(){return id;} public String getPatientId(){return patientId;} public String getCreatedAt(){return createdAt;} public String getMedicationsJson(){return medicationsJson;}
}
