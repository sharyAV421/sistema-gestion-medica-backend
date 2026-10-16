package com.gestionmedica.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionmedica.exception.ApiException;
import com.gestionmedica.model.Models.Patient;
import com.gestionmedica.persistence.entity.PatientEntity;
import com.gestionmedica.persistence.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class PatientService {
    private final PatientRepository patients; private final ObjectMapper json;
    public PatientService(PatientRepository patients,ObjectMapper json){this.patients=patients;this.json=json;}
    @Transactional
    public synchronized Patient register(String name,String document,String email,int age,List<String> allergies){
        String normalizedDocument=document.trim(),normalizedEmail=email.trim();
        if(patients.existsByDocumentIgnoreCase(normalizedDocument))throw ApiException.conflict("El documento ya está registrado");
        if(patients.existsByEmailIgnoreCase(normalizedEmail))throw ApiException.conflict("El correo ya está registrado");
        PatientEntity saved=patients.save(new PatientEntity(UUID.randomUUID().toString(),name.trim(),normalizedDocument,normalizedEmail,age,write(allergies==null?List.of():List.copyOf(allergies))));return toModel(saved);
    }
    @Transactional(readOnly=true) public Patient get(String id){return patients.findById(id).map(this::toModel).orElseThrow(()->ApiException.notFound("Paciente no encontrado"));}
    public List<String> allergies(String id){return get(id).allergies();}
    @Transactional(readOnly=true) public List<Patient> all(){return patients.findAll().stream().map(this::toModel).toList();}
    @Transactional public void addAllergy(String id,String allergy){PatientEntity p=patients.findById(id).orElseThrow(()->ApiException.notFound("Paciente no encontrado"));List<String> items=new ArrayList<>(read(p.getAllergiesJson(),new TypeReference<>(){}));if(items.stream().noneMatch(a->a.equalsIgnoreCase(allergy)))items.add(allergy);p.setAllergiesJson(write(items));}
    private Patient toModel(PatientEntity p){return new Patient(p.getId(),p.getName(),p.getDocument(),p.getEmail(),p.getAge(),read(p.getAllergiesJson(),new TypeReference<>(){}));}
    private String write(Object value){try{return json.writeValueAsString(value);}catch(Exception e){throw new IllegalStateException("No se pudo serializar datos del paciente",e);}}
    private <T>T read(String value,TypeReference<T> type){try{return json.readValue(value,type);}catch(Exception e){throw new IllegalStateException("No se pudieron leer datos del paciente",e);}}
}
