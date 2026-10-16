package com.gestionmedica.persistence.entity;
import jakarta.persistence.*;
@Entity @Table(name="users")
public class UserEntity {
    @Id private String email; @Column(nullable=false,unique=true) private String patientId; @Column(nullable=false,length=512) private String passwordHash;
    protected UserEntity() {}
    public UserEntity(String email,String patientId,String passwordHash){this.email=email;this.patientId=patientId;this.passwordHash=passwordHash;}
    public String getEmail(){return email;} public String getPatientId(){return patientId;} public String getPasswordHash(){return passwordHash;}
}
