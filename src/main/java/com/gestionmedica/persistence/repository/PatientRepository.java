package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.PatientEntity; import org.springframework.data.jpa.repository.JpaRepository;
public interface PatientRepository extends JpaRepository<PatientEntity,String> { boolean existsByDocumentIgnoreCase(String document); boolean existsByEmailIgnoreCase(String email); }
