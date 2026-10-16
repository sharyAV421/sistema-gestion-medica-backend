package com.gestionmedica.persistence.entity;
import jakarta.persistence.*;
@Entity @Table(name="lab_results")
public class LabResultEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="order_id",nullable=false) private LabOrderEntity order;
    @Column(nullable=false) private String name; @Column(name="result_value",nullable=false) private double value; @Column(nullable=false) private String unit;
    @Column(name="reference_min",nullable=false) private double min; @Column(name="reference_max",nullable=false) private double max; @Column(nullable=false) private boolean withinRange;
    protected LabResultEntity() {}
    public LabResultEntity(String name,double value,String unit,double min,double max,boolean withinRange){this.name=name;this.value=value;this.unit=unit;this.min=min;this.max=max;this.withinRange=withinRange;}
    public void setOrder(LabOrderEntity order){this.order=order;} public String getName(){return name;} public double getValue(){return value;} public String getUnit(){return unit;} public double getMin(){return min;} public double getMax(){return max;} public boolean isWithinRange(){return withinRange;}
}
