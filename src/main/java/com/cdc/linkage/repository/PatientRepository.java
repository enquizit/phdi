package com.cdc.linkage.repository;


import com.cdc.linkage.entities.Patient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PatientRepository extends ReactiveCrudRepository<Patient, UUID> {

  Flux<Patient> findByPersonId(UUID personId);

  @Query("SELECT * FROM patient p WHERE p.key = :key AND p.value LIKE :value")
  Flux<Patient> findByKeyAndValueLike(String key, String value);

  @Query("SELECT * FROM Patient p WHERE p.key = :key AND p.value = :value")
  Flux<Patient> findByKeyAndValueEqual( String key,  String value);

  @Query("SELECT * FROM Patient p WHERE p.key = :key AND SOUNDEX(p.value) = SOUNDEX(:value)")
  Flux<Patient> findByKeyAndValueSoundex(String key, String value);
}
