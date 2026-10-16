package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.LabResultEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface LabResultRepository extends JpaRepository<LabResultEntity,Long> { List<LabResultEntity> findByOrder_Id(String orderId); }
