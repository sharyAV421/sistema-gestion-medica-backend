package com.gestionmedica.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class PatientEntity {
    @Id private String id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String document;
    @Column(nullable = false, unique = true) private String email;
    private int age;
    @Column(nullable = false, columnDefinition = "TEXT") private String allergiesJson;
    protected PatientEntity() {}
    public PatientEntity(String id, String name, String document, String email, int age, String allergiesJson) {
        this.id=id; this.name=name; this.document=document; this.email=email; this.age=age; this.allergiesJson=allergiesJson;
    }
    public String getId(){return id;} public String getName(){return name;} public String getDocument(){return document;}
    public String getEmail(){return email;} public int getAge(){return age;} public String getAllergiesJson(){return allergiesJson;}
    public void setAllergiesJson(String value){allergiesJson=value;}
}
