package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.Patient;
import com.cdc.linkage.repository.PatientRepository;
import com.cdc.linkage.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

  private final PatientRepository patientRepository;

  @Override
  public List<Patient> getPatientsByKeyAndValueLike(String key, String value) {
    return patientRepository.findByKeyAndValueLike(key, value);
  }
}
