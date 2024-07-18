
package com.cdc.linkage.service.impl;

import com.cdc.linkage.algorithms.BasicMatchAlgorithm;
import com.cdc.linkage.algorithms.EnhancedMatchAlgorithm;
import com.cdc.linkage.algorithms.MatchAlgorithm;
import com.cdc.linkage.entities.*;
import com.cdc.linkage.enums.AlgorithmTypes;
import com.cdc.linkage.model.*;
import com.cdc.linkage.repository.PatientRepository;
import com.cdc.linkage.repository.PersonRepository;
import com.cdc.linkage.service.*;
import com.cdc.linkage.utils.LoggerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.UUID;

import static com.cdc.linkage.utils.StringUtil.isBlankOrNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkageServiceImpl implements LinkageService {

  private final PersonRepository personRepository;
  private final PatientService patientService;
  private final AlgorithmService algorithmService;
  private final LinkageRequestAlgorithmService linkageRequestService;
  private RecordLinkageRequest request;
  private LinkageRequest linkageRequest;
  private final PatientRepository patientRepository;
  private MatchAlgorithm matchAlgorithm;
  Map<UUID, Double> linkageScores;
  private final LoggerUtil loggerUtil;
  private final EnhancedMatchAlgorithm enhancedMatchAlgorithm;
  private final BasicMatchAlgorithm basicMatchAlgorithm;



  @Override
  public Mono<InsertionResponse> recordLinkage(RecordLinkageRequest request) throws Exception {
    boolean isValid = validateRecordLinkageRequest(request);
    if (!isValid) {
      throw new RuntimeException("Bad Request");
    }

    boolean isInvalidDate = request.patientRecord().keySet().stream()
        .anyMatch(key -> key.toLowerCase().contains("date") && !isDateValid(request.patientRecord().get(key)));

    if (isInvalidDate) {
      return Mono.just(new InsertionResponse(null, "The provided 'birthdate' " +
          "value is incorrect or not obeying the 'yyyy-MM-dd' format."));
    }

    this.request = request;
    linkageScores = new HashMap<>();

    return algorithmService.findAlgorithmsByIds(request.algorithmId())
        .collectList()
        .flatMap(algorithmsList -> {
          Flux<Algorithm> algorithmsFlux = Flux.fromIterable(algorithmsList);
          return linkageRequestService.saveLinkageRequest(algorithmsFlux)
              .flatMap(savedLinkageRequest -> {
                this.linkageRequest = savedLinkageRequest;
                loggerUtil.requestData(linkageRequest.getId(), algorithmsList.get(0).getType(), request);
                return Flux.fromIterable(algorithmsList) // Process each algorithm in the list
                    .flatMap(this::processAlgorithm)
                    .then(Mono.defer(() -> manageInsertion(linkageScores)));
              });
        });
  }

  private boolean isDateValid(String dateStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    boolean isValid = true;
    try {
      LocalDate date = LocalDate.parse(dateStr, formatter);
      String[] parts = dateStr.split("-");

      int day = Integer.parseInt(parts[2]);
      if (date.getDayOfMonth() != day) {
        isValid = false;// if the date day exceed the month.days, the 'LocalDate.parse' will modify the date day
        // to the max day of the month , here we check if the date day is changed (so it is not valid)
      }
    } catch (DateTimeParseException | NumberFormatException e) {
      isValid = false; // Invalid date format or value
    }
    return isValid;
  }



  private Mono<Void> processAlgorithm(Algorithm algorithm) {
    loggerUtil.processAlgorithm(linkageRequest.getId(), algorithm.getId());
    return algorithmService.getAlgorithmDataById(algorithm.getId())
        .flatMap(algorithmData -> {
          return getQueryFields(algorithmData.getBlocks(), request.patientRecord())
              .flatMapMany(queryFields -> {
                return Flux.fromIterable(queryFields.entrySet());
              })
              .flatMap(entry -> {
                // Fetch patients for each entry
                return patientService.getPatientsByKeyAndValue(entry.getKey(), entry.getValue())
                    .collectList()
                    .map(patients -> {
                      loggerUtil.fieldRecordsCount(linkageRequest.getId(), entry.getKey(),
                          algorithmData.getBlocks().get(entry.getKey()), entry.getValue(),
                          !patients.isEmpty());
                      // Map patients to their personId
                      Set<UUID> personIds = patients.stream()
                          .map(Patient::getPersonId)
                          .collect(Collectors.toSet());

                      return new AbstractMap.SimpleEntry<>(entry, personIds); // Return the entry and its personIds set
                    });
              })
              .collectList()
              .flatMap(entriesWithPersonIds -> {
                // Calculate the intersection of personIds across all entries
                Set<UUID> intersectedPersonIds = entriesWithPersonIds.stream()
                    .map(Map.Entry::getValue)
                    .reduce((set1, set2) -> {
                      set1.retainAll(set2);
                      return set1;
                    }).orElse(new HashSet<>());
                if (intersectedPersonIds.isEmpty()) {
                  loggerUtil.recordsPerAlgorithm(linkageRequest.getId(), algorithmData.getId(), 0);
                }
                matchAlgorithm = getMatchAlgorithmByType(algorithm.getType());
                return match(intersectedPersonIds, matchAlgorithm,
                    algorithmData); // Call match with intersectedPersonIds
              });
        })
        .doOnError(e -> log.error("Error in processAlgorithm for algorithmID = " + algorithm.getId(), e))
        .then();
  }


  private Mono<Void> match(Set<UUID> uniquePersons, MatchAlgorithm matchAlgorithm, AlgorithmData algorithmData) {
    List<PersonTicket> personsTickets = new ArrayList<>();

    return Flux.fromIterable(uniquePersons)
        .flatMap(personId -> {
          AtomicInteger numMatchedInCluster = new AtomicInteger();

          // Define a holder for patient records to use it outside the flatMapMany scope
          AtomicReference<Map<UUID, Map<String, String>>> patientRecordsHolder = new AtomicReference<>();

          return getPersonRecords(personId)
              .flatMapMany(patientRecords -> {
                loggerUtil.recordsPerAlgorithm(linkageRequest.getId(), algorithmData.getId(), patientRecords.size());

                patientRecordsHolder.set(patientRecords); // Store patient records in the holder
                return Flux.fromIterable(patientRecords.entrySet())
                    .filter(
                        entry -> matchAlgorithm.matchRecord(entry.getKey(), entry.getValue(), algorithmData, request,
                            linkageRequest.getId(), personId))
                    .doOnNext(entry -> {
                      numMatchedInCluster.incrementAndGet();

                    });

              })
              .then(Mono.fromRunnable(() -> {
                Map<UUID, Map<String, String>> patientRecords = patientRecordsHolder.get();
                if (patientRecords != null) { // Ensure patientRecords are available
                  personsTickets.add(new PersonTicket(personId, numMatchedInCluster.get(), patientRecords.size()));
                  loggerUtil.clusterTotal(linkageRequest.getId(), personId, numMatchedInCluster.get());
                }
              }));
        })
        .then(Mono.fromRunnable(() -> {
          calculateLinkageScores(personsTickets, algorithmData);
        }));
  }


  private Mono<InsertionResponse> manageInsertion(Map<UUID, Double> linkageScores) {
    loggerUtil.linkageScores(linkageRequest.getId(), linkageScores);
    if (linkageScores.isEmpty()) {
      loggerUtil.newPerson(linkageRequest.getId());
      return insertNewPerson(request)
          .map(newPersonId -> new InsertionResponse(newPersonId, "No match found"));
    } else {
      UUID personWithMaxScore = null;
      if (linkageScores.size() == 1) {
        loggerUtil.noOtherCandidates(linkageRequest.getId(), linkageScores);
      } else {
        personWithMaxScore = getMaxMatchedPerson(linkageScores);
        loggerUtil.strongestMatch(linkageRequest.getId(), personWithMaxScore);
      }
      personWithMaxScore = getMaxMatchedPerson(linkageScores);
      loggerUtil.currentPerson(linkageRequest.getId());
      return insertMatchedPatient(personWithMaxScore, request)
          .thenReturn(new InsertionResponse(personWithMaxScore, "Match found"));
    }
  }


  private Mono<UUID> insertNewPerson(RecordLinkageRequest request) {
    LocalDateTime dateTime = LocalDateTime.now();
    Person newPerson = new Person();
    UUID patientUuid = UUID.randomUUID();
    return personRepository.save(newPerson)
        .flatMap(savedPerson -> {
          UUID personId = savedPerson.getId();
          List<Patient> patientRecords = request.patientRecord().entrySet().stream()
              .map(entry -> new Patient(entry, dateTime, personId, patientUuid))
              .toList();

          return Flux.fromIterable(patientRecords)
              .flatMap(patientRepository::save)
              .then(Mono.just(personId));
        });
  }


  private Mono<UUID> insertMatchedPatient(UUID currentPersonId, RecordLinkageRequest request) {
    LocalDateTime dateTime = LocalDateTime.now();
    UUID patientUuid = UUID.randomUUID();
    List<Patient> patientRecords = request.patientRecord().entrySet().stream()
        .map(entry -> new Patient(entry, dateTime, currentPersonId, patientUuid))
        .toList();

    return Flux.fromIterable(patientRecords)
        .flatMap(patientRepository::save)
        .then(Mono.just(currentPersonId));
  }


  private Map<UUID, Double> calculateLinkageScores(List<PersonTicket> personTickets, AlgorithmData algorithmData) {
    for (PersonTicket personTicket : personTickets) {
      UUID personId = personTicket.getPersonId();
      double belongingRatio = (double) personTicket.getNumMatchedInCluster() / personTicket.getRecordsCount();
      loggerUtil.belongingRatioMessage(linkageRequest.getId(), personTicket.getPersonId(), belongingRatio);
      double clusterRatio = Double.parseDouble(algorithmData.getParams().getOrDefault("cluster_ratio", "0.0"));
      if (belongingRatio >= clusterRatio) {
        loggerUtil.clusterRatio(linkageRequest.getId(), belongingRatio, clusterRatio);
        linkageScores.merge(personId, belongingRatio, Math::max);
      }
    }
    return linkageScores;
  }

  private UUID getMaxMatchedPerson(Map<UUID, Double> linkageScores) {
    return linkageScores.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);
  }

  private Mono<Map<UUID, Map<String, String>>> getPersonRecords(UUID personId) {
    return patientRepository.findByPersonId(personId)
        .collectList()
        .flatMapMany(Flux::fromIterable)
        .collect(Collectors.groupingBy(
            Patient::getPatientId,
            Collectors.toMap(Patient::getKey, Patient::getValue)
        ))
        .flatMap(map -> {
          return Flux.fromIterable(map.entrySet())
              .flatMap(entry -> {
                loggerUtil.matchedRecords(linkageRequest.getId(), personId, entry.getValue());
                return Mono.just(entry);
              })
              .collectMap(Map.Entry::getKey, Map.Entry::getValue);
        });
  }

  private Mono<Map<String, String>> getQueryFields(Map<String, String> blocks, Map<String, String> patientRecord) {
    return Flux.fromIterable(patientRecord.entrySet())
        .filter(entry -> blocks.containsKey(entry.getKey())) // Filter out entries that don't have a corresponding block
        .collectMap(
            Map.Entry::getKey,
            entry -> applyTransformation(entry.getValue(), blocks.get(entry.getKey()))
            // Apply transformation to the value
        );
  }


  private String applyTransformation(String value, String transformationType) {
    if (transformationType.equalsIgnoreCase("first4"))
      return (value.length() < 4 ? value : value.substring(0, 4)) + "%";
    if (transformationType.equalsIgnoreCase("last4"))
      return "%" + (value.length() < 4 ? value : value.substring(value.length() - 4));
    if (transformationType.equalsIgnoreCase("soundex"))
      return "SOUNDEX-"+value;
    return value;
  }

  private MatchAlgorithm getMatchAlgorithmByType(String algorithmType) {
    return algorithmType.equals(AlgorithmTypes.DIBBS_ENHANCED.toString()) ?
        enhancedMatchAlgorithm : basicMatchAlgorithm;
  }


  private boolean validateRecordLinkageRequest(RecordLinkageRequest request) {
    if (request.algorithmId() == null || request.algorithmId().isEmpty())
      return false;

    return !(request.patientRecord().keySet().stream()
        .anyMatch(field -> isBlankOrNull(field)));
  }

}
