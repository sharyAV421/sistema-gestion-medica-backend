package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.DoctorEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface DoctorRepository extends JpaRepository<DoctorEntity,String> { List<DoctorEntity> findBySpecialtyIgnoreCase(String specialty); }
