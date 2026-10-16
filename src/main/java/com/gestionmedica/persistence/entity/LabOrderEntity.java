package com.gestionmedica.persistence.entity;
import jakarta.persistence.*; import java.util.ArrayList; import java.util.List;
@Entity @Table(name="lab_orders")
public class LabOrderEntity {
    @Id private String id; @Column(nullable=false) private String patientId; @Column(nullable=false) private String createdAt;
    protected LabOrderEntity() {}
    @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) private List<LabResultEntity> results=new ArrayList<>();
    public LabOrderEntity(String id,String patientId,String createdAt){this.id=id;this.patientId=patientId;this.createdAt=createdAt;}
    public void addResult(LabResultEntity result){results.add(result);result.setOrder(this);}
    public String getId(){return id;} public String getPatientId(){return patientId;} public String getCreatedAt(){return createdAt;} public List<LabResultEntity> getResults(){return results;}
}
