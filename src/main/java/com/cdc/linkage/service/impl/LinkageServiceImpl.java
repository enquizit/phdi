
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
  private final LoggingService loggingService;



  @Override
  public Mono<InsertionResponse> recordLinkage(RecordLinkageRequest request) {

    boolean isInvalidDate = request.patientRecord().keySet().stream()
        .anyMatch(key -> key.toLowerCase().contains("date") && !isDateValid(request.patientRecord().get(key)));

    if (isInvalidDate) {
      return Mono.just(new InsertionResponse(null, "The provided 'birthdate' " +
          "value is incorrect or not obeying the 'yyyy-MM-dd' format."));
    }

    this.request = request;
    linkageScores = new HashMap<>();

    return algorithmService.findAlgorithmsByIds(request.algorithmId())
        .collectList()  // Collect the list of algorithms into a Mono<List<Algorithm>>
        .flatMap(algorithmsList -> {
          Flux<Algorithm> algorithmsFlux = Flux.fromIterable(algorithmsList);
          return linkageRequestService.saveLinkageRequest(algorithmsFlux) // Convert the list to Flux
              .flatMap(savedLinkageRequest -> {
                this.linkageRequest = savedLinkageRequest;
                loggerUtil.requestData(linkageRequest.getId(), algorithmsList.get(0).getType(), request);
                return Flux.fromIterable(algorithmsList) // Process each algorithm in the list
                    .flatMap(this::processAlgorithm)
                    .then(Mono.defer(() -> manageInsertion(linkageScores))); // Use Mono.defer to call manageInsertion
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
    String algorithmStartLog =
        "Request ID: " + linkageRequest.getId() + " - Processing Algorithm: " + algorithm.getId();

    log.info(algorithmStartLog);
    loggingService.saveLog(new LogItem(linkageRequest.getId(), algorithmStartLog)).subscribe();

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
                          algorithmData.getBlocks().get(entry.getKey()), entry.getValue().replace("%", ""),
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
          AtomicReference<Map<LocalDateTime, Map<String, String>>> patientRecordsHolder = new AtomicReference<>();

          return getPersonRecords(personId)
              .flatMapMany(patientRecords -> {
                loggerUtil.recordsPerAlgorithm(linkageRequest.getId(), algorithmData.getId(), patientRecords.size());

                patientRecordsHolder.set(patientRecords); // Store patient records in the holder
                return Flux.fromIterable(patientRecords.entrySet())
                    .filter(entry -> matchAlgorithm.matchRecord(entry.getValue(), algorithmData, request))
                    .doOnNext(entry -> {
                      numMatchedInCluster.incrementAndGet();

                    });

              })
              .then(Mono.fromRunnable(() -> {
                Map<LocalDateTime, Map<String, String>> patientRecords = patientRecordsHolder.get();
                if (patientRecords != null) { // Ensure patientRecords are available
                  personsTickets.add(new PersonTicket(personId, numMatchedInCluster.get(), patientRecords.size()));
                  String clusterTotalLog = String.format(
                      "Request ID: %s - PersonId: %s - The cluster total is increased by 1 (Cluster total = %d).",
                      linkageRequest.getId(), personId, numMatchedInCluster.get()
                  );
                  log.info(clusterTotalLog);
                  loggingService.saveLog(new LogItem(linkageRequest.getId(), clusterTotalLog)).subscribe();
                } else {
                  log.warn("No patient records found for personId = " + personId);
                }
              }));
        })
        .then(Mono.fromRunnable(() -> {
          calculateLinkageScores(personsTickets, algorithmData);
        }));
  }



  private Mono<InsertionResponse> manageInsertion(Map<UUID, Double> linkageScores) {
    logRecordLinkage(linkageScores);
    if (linkageScores.isEmpty()) {
      String newPersonLog = "Request ID : " + linkageRequest.getId() + " - "
          + "No results found. New person and patient records will be created.";
      log.info(newPersonLog);
      loggingService.saveLog(new LogItem(linkageRequest.getId(), newPersonLog)).subscribe();
      return insertNewPerson(request)
          .map(newPersonId -> new InsertionResponse(newPersonId, "No match found"));
    } else {
      UUID personWithMaxScore = null;
      if (linkageScores.size() == 1) {
        Map.Entry<UUID, Double> firstEntry = linkageScores.entrySet().iterator().next();
        String message =
            "Request ID : " + linkageRequest.getId() + " - " + "Because there are no other candidates," +
                " this is the strongest match : " + firstEntry.getKey();
        log.info(message);
        loggingService.saveLog(new LogItem(linkageRequest.getId(), message)).subscribe();
      } else {
        String strongestMatchMessage =
            "Request ID : " + linkageRequest.getId() + " - " + "the strongest match is : " + personWithMaxScore;
        log.info(strongestMatchMessage);
        loggingService.saveLog(new LogItem(linkageRequest.getId(), strongestMatchMessage)).subscribe();
      }
      personWithMaxScore = getMaxMatchedPerson(linkageScores);
      String currentPersonLog =
          "Request ID : " + linkageRequest.getId() + " - " + "Algorithm Result : A new patient record will be created and linked to the existing matched person.";
      log.info(currentPersonLog);
      loggingService.saveLog(new LogItem(linkageRequest.getId(), currentPersonLog)).subscribe();
      return insertMatchedPatient(personWithMaxScore, request)
          .thenReturn(new InsertionResponse(personWithMaxScore, "Match found"));
    }
  }

  private void logRecordLinkage(Map<UUID, Double> linkageScores) {
    StringBuilder msgBuilder = new StringBuilder(" Linkage scores = {");
    for (Map.Entry<UUID, Double> entry : linkageScores.entrySet()) {
      msgBuilder.append("{").append(entry.getKey()).append(" : ").append(entry.getValue()).append("}");
    }
    msgBuilder.append("}");
    String linkageScoresMessage = "Request ID : " + linkageRequest.getId() + " - " + msgBuilder.toString();
    log.info(linkageScoresMessage);
    loggingService.saveLog(new LogItem(linkageRequest.getId(), linkageScoresMessage)).subscribe();
  }

  private Mono<UUID> insertNewPerson(RecordLinkageRequest request) {
    LocalDateTime dateTime = LocalDateTime.now();

    // Create the new Person without patient data yet
    Person newPerson = new Person();  // Assuming Person has a default constructor or similar

    // Save the new Person and get the generated ID
    return personRepository.save(newPerson)
        .flatMap(savedPerson -> {
          UUID personId = savedPerson.getId();
          List<Patient> patientRecords = request.patientRecord().entrySet().stream()
              .map(entry -> new Patient(entry, dateTime, personId))  // Create Patient with the saved personId
              .toList();

          // Save all Patient records associated with the new Person
          return Flux.fromIterable(patientRecords)
              .flatMap(patientRepository::save) // Assuming patientRepository.save returns Mono<Patient>
              .then(Mono.just(personId));  // Return the saved personId after saving all patients
        });
  }


  private Mono<UUID> insertMatchedPatient(UUID currentPersonId, RecordLinkageRequest request) {
    LocalDateTime dateTime = LocalDateTime.now();

    // Create the Patient records associated with the existing person (currentPersonId)
    List<Patient> patientRecords = request.patientRecord().entrySet().stream()
        .map(entry -> new Patient(entry, dateTime, currentPersonId))  // Create Patient with the currentPersonId
        .toList();

    // Save all Patient records associated with the current Person
    return Flux.fromIterable(patientRecords)
        .flatMap(patientRepository::save) // Assuming patientRepository.save returns Mono<Patient>
        .then(Mono.just(currentPersonId));  // Return the currentPersonId after saving all patients
  }


  private Map<UUID, Double> calculateLinkageScores(List<PersonTicket> personTickets, AlgorithmData algorithmData) {
    for (PersonTicket personTicket : personTickets) {
      UUID personId = personTicket.getPersonId();
      double belongingRatio = (double) personTicket.getNumMatchedInCluster() / personTicket.getRecordsCount();
      String belongingRatioMessage =
          "Request ID : " + linkageRequest.getId() + " - " + "Person ID : " + personTicket.getPersonId() +
              " - The cluster ratio is calculated as follows:  Cluster ratio = Cluster total / Cluster records count. =  " + belongingRatio;
      log.info(belongingRatioMessage);
      loggingService.saveLog(new LogItem(linkageRequest.getId(), belongingRatioMessage)).subscribe();
      double clusterRatio = Double.parseDouble(algorithmData.getParams().getOrDefault("cluster_ratio", "0.0"));

      if (belongingRatio >= clusterRatio) {
        String clusterRatioMessage =
            "Request ID : " + linkageRequest.getId() + " - " + "This cluster ratio (" + belongingRatio + ") is greater than " +
                "the “Cluster Ratio” specified in the algorithm configuration (" + clusterRatio + ")." +
                " Then the person UUID is added to the candidate linkage scores. ";
        log.info(clusterRatioMessage);
        loggingService.saveLog(new LogItem(linkageRequest.getId(), clusterRatioMessage)).subscribe();
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

  private Mono<Map<LocalDateTime, Map<String, String>>> getPersonRecords(UUID personId) {
    return patientRepository.findByPersonId(personId)
        .collectList()
        .flatMapMany(Flux::fromIterable)
        .collect(Collectors.groupingBy(
            Patient::getCreatedAt,
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
    return Flux.fromIterable(patientRecord.entrySet()) // Create a Flux from the patientRecord entries
        .filter(entry -> blocks.containsKey(entry.getKey())) // Filter out entries that don't have a corresponding block
        .collectMap( // Collect the filtered entries into a Map
            Map.Entry::getKey, // Key remains the same
            entry -> applyTransformation(entry.getValue(), blocks.get(entry.getKey()))
            // Apply transformation to the value
        );
  }


  private String applyTransformation(String value, String transformationType) {
    if (transformationType.equalsIgnoreCase("first4"))
      return (value.length() < 4 ? value : value.substring(0, 4)) + "%";
    if (transformationType.equalsIgnoreCase("last4"))
      return "%" + (value.length() < 4 ? value : value.substring(value.length() - 4));
    return value;
  }

  private MatchAlgorithm getMatchAlgorithmByType(String algorithmType) {
    return algorithmType.equals(AlgorithmTypes.DIBBS_ENHANCED.toString()) ?
        new EnhancedMatchAlgorithm() : new BasicMatchAlgorithm();
  }
}



