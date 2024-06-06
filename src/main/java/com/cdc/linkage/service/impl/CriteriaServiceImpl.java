package com.cdc.linkage.service.impl;

import com.cdc.linkage.entities.*;
import com.cdc.linkage.exceptions.AlgorithmNotFoundException;
import com.cdc.linkage.model.Block;
import com.cdc.linkage.model.CreateCriteriaRequest;
import com.cdc.linkage.model.CriteriaFunction;
import com.cdc.linkage.repository.*;
import com.cdc.linkage.service.CriteriaService;
import com.cdc.linkage.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CriteriaServiceImpl implements CriteriaService {

  private final CriteriaRepository criteriaRepository;
  private final AlgorithmRepository algorithmRepository;
  private final FieldService fieldService;

  @Transactional
  public void createCriteria(CreateCriteriaRequest request) {
    Algorithm algorithm = algorithmRepository.findById(request.algorithmId())
        .orElseThrow(() -> new AlgorithmNotFoundException("No algorithm with id =" + request.algorithmId()));

    List<Criteria> criteriaListToSave = new ArrayList<>();
    if (request.CriteriaFunctions() != null)
      for (CriteriaFunction criteriaFunction : request.CriteriaFunctions()) {
        // Check if the field already exists, otherwise create a new one
        Field field = fieldService.findOrCreateField(criteriaFunction.fieldName());
        criteriaListToSave.add(new Criteria(algorithm, field, criteriaFunction.functionName()));
      }
    criteriaRepository.saveAll(criteriaListToSave);
  }


  @Override
  public void updateCriteria(CreateCriteriaRequest request) {
    Algorithm algorithm = algorithmRepository.findById(request.algorithmId())
        .orElseThrow(() -> new AlgorithmNotFoundException("No algorithm with id =" + request.algorithmId()));

    List<Criteria> criteriaListToSave = new ArrayList<>();
    if (request.CriteriaFunctions() != null)
      for (CriteriaFunction criteriaFunction : request.CriteriaFunctions()) {
        Criteria criteria = algorithmHaveTheCriteria(criteriaFunction, algorithm);
        if (criteria != null) {
          criteria.setFunctionName(criteriaFunction.functionName());
          criteriaRepository.save(criteria);
        } else {
          // Check if the field already exists, otherwise create a new one
          Field field = fieldService.findOrCreateField(criteriaFunction.fieldName());
          criteriaListToSave.add(new Criteria(algorithm, field, criteriaFunction.functionName()));
        }
      }
    if (!criteriaListToSave.isEmpty()) {
      criteriaRepository.saveAll(criteriaListToSave);
    }
  }

  private Criteria algorithmHaveTheCriteria(CriteriaFunction criteriaFunction, Algorithm algorithm) {
    for (Criteria criteria : algorithm.getCriteriaList()) {
      if (criteria.getField().getName().equals(criteriaFunction.fieldName()))
        return criteria;
    }
    return null;
  }


}
