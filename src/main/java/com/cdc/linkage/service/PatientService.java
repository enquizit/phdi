package com.cdc.linkage.service;

import com.cdc.linkage.entities.Patient;

import java.util.List;

public interface PatientService {

  public List<Patient> getPatientsByKeyAndValueLike(String key, String value);
}
