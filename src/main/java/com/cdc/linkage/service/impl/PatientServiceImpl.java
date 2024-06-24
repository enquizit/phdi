package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.Patient;
import com.cdc.linkage.repository.PatientRepository;
import com.cdc.linkage.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

  private final PatientRepository patientRepository;

  @Override
  public Flux<Patient> getPatientsByKeyAndValue(String key, String value) {
    if (value.contains("%")) {
      return patientRepository.findByKeyAndValueLike(key, value);
    } else {
      return patientRepository.findByKeyAndValueEqual(key, value);
    }
  }
}
