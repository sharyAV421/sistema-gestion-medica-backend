package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.ConsultationEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface ConsultationRepository extends JpaRepository<ConsultationEntity,Long> { List<ConsultationEntity> findByPatientId(String patientId); }
