package com.gestionmedica.persistence.entity;
import jakarta.persistence.*;
@Entity @Table(name="doctors")
public class DoctorEntity {
    @Id private String id; @Column(nullable=false) private String name; @Column(nullable=false) private String specialty;
    @Column(nullable=false,columnDefinition="TEXT") private String availableSlotsJson;
    protected DoctorEntity() {}
    public DoctorEntity(String id,String name,String specialty,String slots){this.id=id;this.name=name;this.specialty=specialty;this.availableSlotsJson=slots;}
    public String getId(){return id;} public String getName(){return name;} public String getSpecialty(){return specialty;} public String getAvailableSlotsJson(){return availableSlotsJson;} public void setAvailableSlotsJson(String value){availableSlotsJson=value;}
}
