package com.cdc.linkage.service;

import com.cdc.linkage.entities.Patient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PatientService {

   Flux<Patient> getPatientsByKeyAndValue(String key, String value);
}
