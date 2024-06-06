package com.cdc.linkage.repository;


import com.cdc.linkage.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatientRepository  extends JpaRepository<Patient, UUID> {

  @Query("SELECT p FROM Patient p WHERE p.key = :key AND p.value LIKE :value")
  List<Patient> findByKeyAndValueLike( String key,  String value);
}
