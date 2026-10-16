package com.gestionmedica.service;
import com.gestionmedica.exception.ApiException; import com.gestionmedica.model.Models.Consultation; import com.gestionmedica.persistence.entity.ConsultationEntity; import com.gestionmedica.persistence.repository.ConsultationRepository; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.List;
@Service public class MedicalHistoryService {
 private final ConsultationRepository consultations; private final PatientService patients;
 public MedicalHistoryService(ConsultationRepository consultations,PatientService patients){this.consultations=consultations;this.patients=patients;}
 public List<String> allergies(String patientId){try{return patients.get(patientId).allergies();}catch(ApiException e){return List.of();}}
 @Transactional(readOnly=true) public List<Consultation> consultations(String id){return consultations.findByPatientId(id).stream().map(c->new Consultation(c.getDate(),c.getDiagnosis(),c.getNotes())).toList();}
 @Transactional public void add(String id,Consultation c){consultations.save(new ConsultationEntity(id,c.date(),c.diagnosis(),c.notes()));}
 public void addAllergy(String patientId,String allergy){patients.addAllergy(patientId,allergy);}
}
