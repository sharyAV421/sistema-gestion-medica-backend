package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.AppointmentEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.time.LocalDateTime; import java.util.List;
public interface AppointmentRepository extends JpaRepository<AppointmentEntity,String> { List<AppointmentEntity> findByPatientId(String patientId); boolean existsByDoctorIdAndDateAndStatus(String doctorId, LocalDateTime date, String status); boolean existsByPatientIdAndDateAndStatus(String patientId, LocalDateTime date, String status); }
