package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.LabOrderEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface LabOrderRepository extends JpaRepository<LabOrderEntity,String> { List<LabOrderEntity> findByPatientId(String patientId); }
