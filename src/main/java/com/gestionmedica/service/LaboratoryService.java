package com.gestionmedica.service;
import com.gestionmedica.exception.ApiException; import com.gestionmedica.model.Models.*; import com.gestionmedica.persistence.entity.LabOrderEntity; import com.gestionmedica.persistence.entity.LabResultEntity; import com.gestionmedica.persistence.repository.LabOrderRepository; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.LocalDate; import java.util.*;
@Service public class LaboratoryService {
 private final LabOrderRepository orders; private final Random random=new Random(17);
 private record Range(double min,double max,String unit){}
 private final Map<String,Range> ranges=Map.of("hemograma",new Range(12,17,"g/dL"),"glicemia",new Range(70,100,"mg/dL"),"perfil lipídico",new Range(0,200,"mg/dL"));
 public LaboratoryService(LabOrderRepository orders){this.orders=orders;}
 @Transactional public LabOrder request(String patientId,List<String> exams){List<LabResult> results=exams.stream().map(name->{Range r=ranges.get(name.toLowerCase(Locale.ROOT));if(r==null)throw ApiException.badRequest("Examen no soportado: "+name);double value=Math.round((r.min+random.nextDouble()*(r.max-r.min)*1.3)*10.0)/10.0;return new LabResult(name,value,r.unit,r.min,r.max,value>=r.min&&value<=r.max);}).toList();LabOrder order=new LabOrder(UUID.randomUUID().toString(),patientId,results,LocalDate.now().toString());LabOrderEntity entity=new LabOrderEntity(order.id(),patientId,order.createdAt());results.forEach(r->entity.addResult(new LabResultEntity(r.name(),r.value(),r.unit(),r.min(),r.max(),r.withinRange())));orders.save(entity);return order;}
 @Transactional(readOnly=true) public List<LabOrder> forPatient(String id){return orders.findByPatientId(id).stream().map(o->new LabOrder(o.getId(),o.getPatientId(),o.getResults().stream().map(r->new LabResult(r.getName(),r.getValue(),r.getUnit(),r.getMin(),r.getMax(),r.isWithinRange())).toList(),o.getCreatedAt())).toList();}
}
