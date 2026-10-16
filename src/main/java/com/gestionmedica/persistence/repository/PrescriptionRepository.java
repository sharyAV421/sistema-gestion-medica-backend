package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.PrescriptionEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface PrescriptionRepository extends JpaRepository<PrescriptionEntity,String> { List<PrescriptionEntity> findByPatientId(String patientId); }
