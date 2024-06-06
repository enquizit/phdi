
package com.cdc.linkage.service.impl;

import com.cdc.linkage.algorithms.BasicMatchAlgorithm;
import com.cdc.linkage.algorithms.EnhancedMatchAlgorithm;
import com.cdc.linkage.algorithms.MatchAlgorithm;
import com.cdc.linkage.entities.*;
import com.cdc.linkage.enums.AlgorithmTypes;
import com.cdc.linkage.model.AlgorithmData;
import com.cdc.linkage.model.InsertionResponse;
import com.cdc.linkage.model.PersonTicket;
import com.cdc.linkage.model.RecordLinkageRequest;
import com.cdc.linkage.repository.PersonRepository;
import com.cdc.linkage.service.AlgorithmService;
import com.cdc.linkage.service.LinkageRequestAlgorithmService;
import com.cdc.linkage.service.LinkageService;
import com.cdc.linkage.service.PatientService;
import com.cdc.linkage.utils.LoggerUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkageServiceImpl implements LinkageService {

  private final PersonRepository personRepository;
  private final PatientService patientService;
  private final AlgorithmService algorithmService;
  private final LinkageRequestAlgorithmService linkageRequestService;
  private AlgorithmData algorithmData;
  private RecordLinkageRequest request;
  static ObjectMapper mapper = new ObjectMapper();

  private MatchAlgorithm matchAlgorithm;
  Map<Person, Double> linkageScores;
  UUID requestId;

  @Override
  public InsertionResponse recordLinkage(RecordLinkageRequest request) throws JsonProcessingException {
    this.request = request;
    linkageScores = new HashMap<>();
    List<Algorithm> algorithmsList = algorithmService.findAlgorithmsByIds(request.algorithmId());
    requestId = linkageRequestService.saveLinkageRequest(algorithmsList);
    log.info("\n \n");
    LoggerUtil.requestData(requestId, algorithmsList.get(0).getType(), mapper.writeValueAsString(request));
    for (Algorithm algorithm : algorithmsList) {
      log.info("Request ID : " + requestId +" - "+"Algorithm ID => " + algorithm.getId());
      algorithmData = new AlgorithmData(algorithm);
      Map<String, String> queryFields = getQueryFields(algorithmData.getBlocks(), request.patientRecord());
      List<Patient> patients = new ArrayList<>();
      boolean isFirstIteration = true;
      for (Map.Entry<String, String> entry : queryFields.entrySet()) {
        List<Patient> subList = patientService.getPatientsByKeyAndValueLike(entry.getKey(), entry.getValue());
        //for-log
        String fieldValue = entry.getValue().replace("%", "");
        LoggerUtil.fieldRecordsCount(requestId,entry.getKey(), algorithmData.getBlocks().get(entry.getKey()), fieldValue,
            !subList.isEmpty());
        //for-log
        if (isFirstIteration) {
          patients.addAll(subList);
          isFirstIteration = false;
        } else {
          patients.retainAll(subList);
        }
      }
      LoggerUtil.recordsPerAlgorithm(requestId,patients.size());
      Set<Person> uniquePersons = getUniquePersonsFromPatientRecords(patients);//may changed [useless]
      //log - print the all the matched records
      for (Person p : uniquePersons) {
        Map<LocalDateTime, Map<String, String>> personRecords = getPersonRecords(p);
        for (Map.Entry<LocalDateTime, Map<String, String>> patientRecord : personRecords.entrySet()) {
          LoggerUtil.matchedRecords(requestId,p.getId() ,patientRecord.getValue());
        }
      }
      //log - print the all the matched records
      matchAlgorithm = getMatchAlgorithmByType(algorithm.getType());
      match(uniquePersons, matchAlgorithm);
      log.info("-------------------------------------------------------------------------");
    }
    return manageInsertion(linkageScores);
  }

  private void match(Set<Person> uniquePersons, MatchAlgorithm matchAlgorithm) {
    List<PersonTicket> personsTickets = new ArrayList<>();
    for (Person person : uniquePersons) {
      int numMatchedInCluster = 0;
      Map<LocalDateTime, Map<String, String>> patientRecords = getPersonRecords(person);
      for (Map.Entry<LocalDateTime, Map<String, String>> patientRecord : patientRecords.entrySet()) {
        if (matchAlgorithm.matchRecord(patientRecord.getValue(), algorithmData, request)) {
          numMatchedInCluster++;
          log.info("Request ID : " + requestId +" - "+"The cluster total is increased by 1 (Cluster total = " + numMatchedInCluster + ").");
        }
      }
      personsTickets.add(new PersonTicket(person, numMatchedInCluster, patientRecords.size()));
    }
    calculateLinkageScores(personsTickets);
  }

  private InsertionResponse manageInsertion(Map<Person, Double> linkageScores) {
    if (linkageScores.isEmpty()) {
      log.info("Request ID : " + requestId +" - "+"No results found. New person and patient records will be created.");
      UUID newPersonId = insertNewPerson(request);
      return new InsertionResponse(newPersonId, true);
    } else {
      if (linkageScores.size() == 1) {
        log.info("Request ID : " + requestId +" - "+"Because there are no other candidates, this is the strongest match.");
      }
      Person personWithMaxScore = getMaxMatchedPerson(linkageScores);
      log.info("Request ID : " + requestId +" - "+"the strongest match is : " + personWithMaxScore.getId());
      insertMatchedPatient(personWithMaxScore, request);
      log.info("Request ID : " + requestId +" - "+"Algorithm Result : A new patient record will be created and linked to the existing matched person.");
      return new InsertionResponse(personWithMaxScore.getId(), false);
    }
  }

  private UUID insertNewPerson(RecordLinkageRequest request) {
    LocalDateTime dateTime = LocalDateTime.now();
    List<Patient> patientRecord = request.patientRecord().entrySet().stream()
        .map(entry -> new Patient(entry, dateTime)).toList();
    Person newPerson = new Person(patientRecord);
    return personRepository.save(newPerson).getId();
  }

  private void insertMatchedPatient(Person currentPerson, RecordLinkageRequest request) {
    LocalDateTime dateTime = LocalDateTime.now();
    List<Patient> patientRecord = request.patientRecord().entrySet().stream()
        .map(entry -> new Patient(entry, currentPerson, dateTime)).toList();
    currentPerson.getPatients().addAll(patientRecord);
    personRepository.save(currentPerson);
  }

  private Map<Person, Double> calculateLinkageScores(List<PersonTicket> personTickets) {
    for (PersonTicket personTicket : personTickets) {
      Person person = personTicket.getPerson();
      double belongingRatio = (double) personTicket.getNumMatchedInCluster() / personTicket.getRecordsCount();
      log.info("Request ID : " + requestId +" - "+
          "The cluster ratio is calculated as follows:  Cluster ratio = Cluster total / Cluster records count. =  " + belongingRatio);
      double clusterRatio = Double.parseDouble(algorithmData.getParams().getOrDefault("cluster_ratio", "0.0"));
      if (belongingRatio >= clusterRatio) {
        log.info("Request ID : " + requestId +" - "+"This cluster ratio (" + belongingRatio + ") is greater than " +
            "the “Cluster Ratio” specified in the algorithm configuration (" + clusterRatio + ")." +
            " Then the person UUID is added to the candidate linkage scores. ");
        if (linkageScores.containsKey(person))
          linkageScores.put(person, Math.max(belongingRatio, linkageScores.get(person)));
        else
          linkageScores.put(person, belongingRatio);
      }
      String msg = " Linkage scores = {";
      for (Map.Entry<Person, Double> entry : linkageScores.entrySet()) {
        msg += "{" + entry.getKey().getId() + " : " + entry.getValue() + "}";
      }
      msg += "}";
      log.info("Request ID : " + requestId +" - "+msg);
    }

    return linkageScores;
  }

  private Person getMaxMatchedPerson(Map<Person, Double> linkageScores) {
    double maxNumMatchedInCluster = 0;
    Person personWithMaxMatches = null;
    for (Map.Entry<Person, Double> entry : linkageScores.entrySet()) {
      double numMatchedInCluster = entry.getValue();
      if (numMatchedInCluster > maxNumMatchedInCluster) {
        maxNumMatchedInCluster = numMatchedInCluster;
        personWithMaxMatches = entry.getKey();
      }
    }
    return personWithMaxMatches;
  }

  private Map<LocalDateTime, Map<String, String>> getPersonRecords(Person person) {
    return person.getPatients().stream()
        .collect(Collectors.groupingBy(
            Patient::getCreatedAt,
            Collectors.toMap(Patient::getKey, Patient::getValue)
        ));
  }

  private Map<String, String> getQueryFields(Map<String, String> blocks, Map<String, String> patientRecord) {
    Map<String, String> queryFields = new HashMap<>();
    for (Map.Entry<String, String> fieldEntry : patientRecord.entrySet()) {
      if (blocks.containsKey(fieldEntry.getKey())) {
        String transformationType = blocks.get(fieldEntry.getKey());
        queryFields.put(fieldEntry.getKey(), applyTransformation(fieldEntry.getValue(), transformationType));
      }
    }
    return queryFields;
  }

  private String applyTransformation(String value, String transformationType) {
    if (transformationType.equalsIgnoreCase("first4"))
      return (value.length() < 4 ? value : value.substring(0, 4)) + "%";
    if (transformationType.equalsIgnoreCase("last4"))
      return "%" + value.substring(value.length() - 4);
    return value;
  }

  private Set<Person> getUniquePersonsFromPatientRecords(List<Patient> patients) {
    return patients.stream().map(Patient::getPerson).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private MatchAlgorithm getMatchAlgorithmByType(String algorithmType) {
    return algorithmType.equals(AlgorithmTypes.DIBBS_ENHANCED.toString()) ?
        new EnhancedMatchAlgorithm() : new BasicMatchAlgorithm();
  }

}
