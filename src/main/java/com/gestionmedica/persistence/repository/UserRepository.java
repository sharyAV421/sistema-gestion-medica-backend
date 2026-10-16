package com.gestionmedica.persistence.repository;
import com.gestionmedica.persistence.entity.UserEntity; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<UserEntity,String> {}
