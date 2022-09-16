package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.*;
import gr.aegean.palaemon.conductor.model.pojo.BraceletPojo;
import gr.aegean.palaemon.conductor.model.pojo.ConstraintSolverIncident;
import gr.aegean.palaemon.conductor.model.pojo.LegacySystemTO;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.DistanceCalculatorService;
import gr.aegean.palaemon.conductor.service.ElasticService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.CryptoUtils;
import gr.aegean.palaemon.conductor.utils.SRAPUtils;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService {

    static final String NOTIFICATION_TOPIC = "pameas-notification";
    static final String LOCATION_TOPIC = "pameas-location";

    private final KafkaProducer<String, PameasNotificationTO> notificationProducer;

    private final KafkaProducer<String, BraceletPojo> braceletProducer;


    @Autowired
    private DBProxyService dbProxyService;

    @Autowired
    private DistanceCalculatorService distanceCalculatorService;


    @Autowired
    ElasticService elasticService;

    @Autowired
    EvacuationStatusTO evacuationStatusTO;

    @Autowired
    CryptoUtils cryptoUtils;

    private final KafkaProducer<String, KafkaHeartBeatResponse> heartBeatProducer;
    private final KafkaProducer<String, EvacuationCoordinatorEventTO> evacuationCoordinatorProducer;

    private final KafkaProducer<String, SmartSafetySystemEventTO> smartSafetyProducer;

    private final KafkaProducer<String,SrapTO> srapProducer;

    private final KafkaProducer<String, LegacySystemTO> legacyProducer;

    @Autowired
    public KafkaServiceImpl(KafkaProducer<String, PameasNotificationTO> notificationProducer,
                            KafkaProducer<String, BraceletPojo> braceletProducer,
                            KafkaProducer<String, KafkaHeartBeatResponse> heartBeatProducer,
                            KafkaProducer<String, EvacuationCoordinatorEventTO> evacuationCoordinatorProducer,
                            KafkaProducer<String, SmartSafetySystemEventTO> smartSafetyProducer,
                            KafkaProducer<String,SrapTO> srapProducer,
                            KafkaProducer<String, LegacySystemTO> legacyProducer) {
        this.notificationProducer = notificationProducer;
        this.braceletProducer = braceletProducer;
        this.heartBeatProducer = heartBeatProducer;
        this.evacuationCoordinatorProducer = evacuationCoordinatorProducer;
        this.smartSafetyProducer = smartSafetyProducer;
        this.srapProducer = srapProducer;
        this.legacyProducer= legacyProducer;
    }


    @Override
    public void writeToPameasNotification(PameasNotificationTO pameasNotification) {
        try {
            log.info("pushing notification to  kafka {}", pameasNotification);
            this.notificationProducer.send(new ProducerRecord<>(NOTIFICATION_TOPIC, pameasNotification));
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
//            producer.close();
        }
    }

    @KafkaListener(topics = "smart-safety-system", groupId = "uaeg-consumer-group")
    @Override
    public void monitorSmartSafety(String message) {
        log.info("Received Message in smart-safety-system ${}", message);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            SmartSafetySystemEventTO smartSafetySystemEventTO = mapper.readValue(message, SmartSafetySystemEventTO.class);
            if (smartSafetySystemEventTO.getType().equals("Fall")
                    || smartSafetySystemEventTO.getType().equals("Fire")
                    || smartSafetySystemEventTO.getType().equals("Grounding")) {

                Optional<String> geofenceName =
                        distanceCalculatorService.getGeofenceFromCordsAndDeck(smartSafetySystemEventTO.getDeck(), smartSafetySystemEventTO.getPositionX(), smartSafetySystemEventTO.getPositionY());
                if (geofenceName.isPresent()) {
                    BlockedGeofenceTO blockedGeofenceTO = new BlockedGeofenceTO(geofenceName.get(), "blocked");

                    String conductorUrl = System.getenv("CONDUCTOR_URI");
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(conductorUrl + "workflow/detect_blocked_geofence?priority=0"))
                            .header("Content-Type", "application/json")
                            .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(blockedGeofenceTO)))
                            .build();
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    log.info("made a call to ${} detect_blocked_geofence", conductorUrl);
                }
            }
        } catch (InterruptedException | IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @KafkaListener(topics = "evacuation-coordinator",  groupId = "uaeg-consumer-group", autoStartup = "true")
    public void monitorEvacuationCoordinator(String message) {
        log.info("Received Message in evacuation-coordinator ${}", message);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            EvacuationCoordinatorEventTO eventTO = mapper.readValue(message, EvacuationCoordinatorEventTO.class);
            KafkaHeartBeatResponse response = new KafkaHeartBeatResponse();
            response.setOriginator("PaMEAS-Location");
            // change PaMEAS status to the received one
            this.evacuationStatusTO.setStatus(String.valueOf(eventTO.getEvacuationStatus()));
            if (this.evacuationStatusTO.getStatus().equals("0")) {
                response.setOperationMode(0);
            } else {
                response.setOperationMode(1);
            }
            response.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
            this.heartBeatProducer.send(new ProducerRecord<>("evacuation-component-status", response));

            //if the status was set to 2 (EMBARKATION) start the mustering flows
            if (eventTO.getEvacuationStatus()== 2) {
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("4", "4.1");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/instruct_crew_to_positions?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to ${} instruct_crew_to_positions", conductorUrl);
            }

            if (eventTO.getEvacuationStatus() == 1) {
                log.info("evacuation status changed to Situation Assessment. Nothing to do for now");
            }


        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    @KafkaListener(topics = "heartbeat-request", groupId = "uaeg-consumer-group")
    public void monitorHeartbeat(String message) {
//        log.info("message from heartbeat-request ${}", message);
        sendHeartBeatResponse("heartbeat-response");
    }

    public void sendHeartBeatResponse(String topic) {
        KafkaHeartBeatResponse response = new KafkaHeartBeatResponse();
        if(this.evacuationStatusTO == null  ){
            evacuationStatusTO = new EvacuationStatusTO();
            evacuationStatusTO.setStatus("0");
        }
        if( this.evacuationStatusTO.getStatus() == null){
            this.evacuationStatusTO.setStatus("0");
        }

        if (!this.evacuationStatusTO.getStatus().equals("0")) {
            response.setOperationMode(1);
        } else {
            response.setOperationMode(0);
        }
        response.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
        this.heartBeatProducer.send(new ProducerRecord<>(topic, response));
    }

    @Override
    @KafkaListener(topics = "resource-discovery-request", groupId = "uaeg-consumer-group")
    public void monitorResourceDiscover(String message) {
//        log.info("message from resource-discovery-request ${}", message);
        KafkaHeartBeatResponse response = new KafkaHeartBeatResponse();
        sendHeartBeatResponse("resource-discovery-response");
    }

    @Override
    @KafkaListener(topics = "smart-bracelet-sensor-data", groupId = "uaeg-consumer-group")
    public void monitorBraceletSaturation(String message) {
        log.info("message from /smart-bracelet-sensor-data ${}", message);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            BraceletDataTO braceletDataTO = mapper.readValue(message, BraceletDataTO.class);
            Optional<PameasPerson> person = elasticService.getPersonByBraceletId(braceletDataTO.getId());
            if (person.isPresent()) {
                person.get().getPersonalInfo().setOxygenSaturation(braceletDataTO.getSp02());
                String decryptedPersonaId = this.cryptoUtils.decryptBase64Message(person.get().getPersonalInfo().getPersonalId());
                elasticService.updatePerson(decryptedPersonaId, person.get());
            } else {
                log.error("no person found with bracelet {}", braceletDataTO.getId());
            }
        } catch (JsonProcessingException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | NoSuchPaddingException | KafkaException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @KafkaListener(topics = "smart-bracelet-event-notification", groupId = "uaeg-consumer-group")
    public void monitorBraceletFallEvent(String message) {
//        log.info("message from /smart-bracelet-event-notification ${}", message);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            BraceletFallTO braceletFallTO = mapper.readValue(message, BraceletFallTO.class);
            Optional<PameasPerson> person = elasticService.getPersonByBraceletId(braceletFallTO.getId());
            if (person.isPresent()) {
                PameasNotificationTO notificationTO = Wrappers.pameasPersonToNotificationTO(person.get());
                this.writePameasNotification(notificationTO);

                //finally update so that the person is displayed as fallen
                person.get().getPersonalInfo().setHasFallen("true");
                String decryptedPersonaId = this.cryptoUtils.decryptBase64Message(person.get().getPersonalInfo().getPersonalId());
                elasticService.updatePerson(decryptedPersonaId, person.get());

            }

        } catch (JsonProcessingException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @KafkaListener(topics = "srap", groupId = "uaeg-consumer-group")
    public void monitorSRAP(String message) {
        log.info("message from /srap ${}", message);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            SrapTO srapTO = mapper.readValue(message, SrapTO.class);
            if (!StringUtils.isEmpty(srapTO.getPassengerId())) {
                //  status: “string” (assistance_required, movement_delayed, free_movement)
                if (srapTO.getStatus().equals("assistance_required")) {
                    Optional<PameasPerson> person = this.elasticService.getPersonByPersonalIdentifierDecrypted(srapTO.getPassengerId());
                    if (person.isPresent()) {
                        PameasNotificationTO notificationTO = Wrappers.pameasPersonToNotificationTO(person.get());
                        this.writePameasNotification(notificationTO);
                    }
                }
            } else {
                if (!StringUtils.isEmpty(srapTO.getZoneId())) {

                    if (srapTO.getStatus().equals("closed")) {
                        //Zone was blocked!!
                        String conductorUrl = System.getenv("CONDUCTOR_URI");
                        List<String> zoneGeofences = SRAPUtils.zoneId2Geofence(srapTO.getZoneId());
                        assert zoneGeofences != null;


                        zoneGeofences.forEach(geofence->{
                            BlockedGeofenceTO blockedGeofenceTO = new BlockedGeofenceTO();
                            blockedGeofenceTO.setGeofence(geofence);
                            blockedGeofenceTO.setStatus("blocked");
                            HttpRequest request = null;
                            try {
                                request = HttpRequest.newBuilder()
                                        .uri(URI.create(conductorUrl + "workflow/detect_blocked_geofence?priority=0"))
                                        .header("Content-Type", "application/json")
                                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(blockedGeofenceTO)))
                                        .build();
                                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                                log.info("made a call to {}confirm_crew_positions", conductorUrl);
                                log.info("response {}", response.body());
                            } catch (IOException | InterruptedException e) {
                                log.error(e.getMessage());
                            }

                        });

                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("error parsing SRAP message {}", e.getMessage());
        }
    }

    @Override
    @KafkaListener(topics = "pameas-notification", groupId = "uaeg-consumer-group")
    public void monitorPameasNotification(String message) {
        log.info("Received Message in pameas-notification ${}", message);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            PameasNotificationTO pameasNotificationTO = mapper.readValue(message, PameasNotificationTO.class);
            if (pameasNotificationTO.getType().equals("crew_member_position_update")) {
                String hashMacAddress = pameasNotificationTO.getId();
                ConfirmCrewInPositionTO confirmCrewInPositionTO = new ConfirmCrewInPositionTO(hashMacAddress, "true");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/confirm_crew_positions?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(confirmCrewInPositionTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to {}confirm_crew_positions", conductorUrl);
                log.info("response {}", response.body());
            }

            if (pameasNotificationTO.getType().equals("all_crew_in_position")) {
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO();
                phaseTaskTO.setPhase("5");
                phaseTaskTO.setTaskId("5.1");

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/alert_passengers?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to {}confirm_crew_positions", conductorUrl);
                log.info("response {}", response.body());
            }


            if (pameasNotificationTO.getType().equals("INIT_PASSENGER_ALERTING")) {
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("5", "5.1");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/alert_passengers?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to ${} alert_passengers", conductorUrl);
                log.info("response {}", response.body());
            }

            if (pameasNotificationTO.getType().equals("PASSENGER_ALERT_COMPLETED")) {
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("6", "6.2");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/mustering_instruction_passengers?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to {}mustering_instruction_passengers", conductorUrl);
                log.info("response {}", response.body());
            }

            if (pameasNotificationTO.getType().equals("PASSENGER_INSTRUCTION_COMPLETED")) {
                dbProxyService.setEvacuationStatus("6.3");
                log.info("update evacuation status to 6.3");
            }

            //handle the declaration of a new passenger emergency
            if (pameasNotificationTO.getType().equals("PASSENGER_EMERGENCY")) {
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                String passengerHashedMacAddress = pameasNotificationTO.getId();
                if (passengerHashedMacAddress == null) {
                    passengerHashedMacAddress = pameasNotificationTO.getMacAddress();
                }
                DetectPassengerIncidentTO detectPassengerIncidentTO = new DetectPassengerIncidentTO(passengerHashedMacAddress);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/detect_incident?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(detectPassengerIncidentTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to {}detect_incident", conductorUrl);
                log.info("response {}", response.body());
            }

            List<String> mobilityIssues = Arrays.asList("assisted_gait",
                    "walking_disability",
                    "severe_walking_disability",
                    "unable_to_walk",
                    "visually_impaired",
                    "hearing_impaired",
                    "cognitive_impaired"
            );

            List<String> medicalIssues = Arrays.asList("equip_required",
                    "stretcher",
                    "heavy_doses"
            );

            List<String> pregnencyIssues = List.of("complicated"
            );

            //Generate Passenger issues for any to passengers with mobility issues etc.
            if (pameasNotificationTO.getType().equals("PASSENGER_INSTRUCTION_COMPLETED")) {
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                this.dbProxyService.getPassengerDetails().stream().forEach(pameasPerson -> {
                    if (medicalIssues.contains(pameasPerson.getPersonalInfo().getMedicalCondition()) ||
                            pregnencyIssues.contains(pameasPerson.getPersonalInfo().getPrengencyData()) ||
                            mobilityIssues.contains(pameasPerson.getPersonalInfo().getMobilityIssues())) {

                        String passengerHashedMacAddress = pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress();
                        DetectPassengerIncidentTO detectPassengerIncidentTO = new DetectPassengerIncidentTO(passengerHashedMacAddress);
                        try {
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(conductorUrl + "workflow/detect_incident?priority=0"))
                                    .header("Content-Type", "application/json")
                                    .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(detectPassengerIncidentTO)))
                                    .build();
                            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                            log.info("made a call to {}detect_incident", conductorUrl);
                            log.info("response {}", response.body());
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                });
            }


            // read Passenger Issue and proceed with its resolution
            if (pameasNotificationTO.getType().equals("PASSENGER_ISSUE")) {
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                ConstraintSolverIncident incident = Wrappers.pameasNotificationTO2ConstraintSolverIncident(pameasNotificationTO);
                ArrayList<ConstraintSolverIncident> incidentArrayList = new ArrayList<>();
                incidentArrayList.add(incident);

                ProposeAssignmentRequestTO requestTO = new ProposeAssignmentRequestTO(incidentArrayList);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/porpose_assignment?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to {}porpose_assignment", conductorUrl);
                log.info("response {}", response.body());

            }

            // respond to a passenger Assignment Acceptance by Bridger
            if (pameasNotificationTO.getType().equals("PASSENGER_INCIDENT_ASSIGNMENT_ACCEPTANCE")) {
                CrewAssignmentRequestTO crewAssignmentRequestTO = new CrewAssignmentRequestTO();
                crewAssignmentRequestTO.setAssignments(new ArrayList<>());
                crewAssignmentRequestTO.getAssignments().add(pameasNotificationTO);
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/crew_assignment_request?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(crewAssignmentRequestTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to {}crew_assignment_request", conductorUrl);
                log.info("response {}", response.body());
            }


            // respond to a passenger Assignment ACK by the Crew Member
            if (pameasNotificationTO.getType().equals("PASSENGER_INCIDENT_ASSIGNMENT_ACK")) {
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                CrewAssignmentAckTO crewAssignmentAckTO = new CrewAssignmentAckTO();
                CrewAssignmentAckTO.AssignmentACK assignmentACK = new CrewAssignmentAckTO.AssignmentACK();
                assignmentACK.setId(pameasNotificationTO.getId());
                assignmentACK.setType("PASSENGER_INCIDENT_ASSIGNMENT_ACK");
                CrewAssignmentAckTO.CrewMemberId[] crewIds = new CrewAssignmentAckTO.CrewMemberId[pameasNotificationTO.getAssignedCrewMemberId().length];
                int i = 0;
                for (String s : pameasNotificationTO.getAssignedCrewMemberId()) {
                    CrewAssignmentAckTO.CrewMemberId crewMemberId = new CrewAssignmentAckTO.CrewMemberId();
                    crewMemberId.setId(s);
                    crewIds[i] = crewMemberId;
                    i++;
                }
                assignmentACK.setCrew(crewIds);
                crewAssignmentAckTO.setAssignment(assignmentACK);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/crew_assignment_ack?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(crewAssignmentAckTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to {}crew_assignment_ack", conductorUrl);
                log.info("response {}", response.body());
            }
            //SRAP blocked geofence
            if (pameasNotificationTO.getType().equals("SRAP_BLOCKED_GEOFENCE")) {
                BlockedGeofenceTO blockedGeofenceTO = new BlockedGeofenceTO(pameasNotificationTO.getGeofence(), "blocked");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/detect_blocked_geofence?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(blockedGeofenceTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to ${} detect_blocked_geofence", conductorUrl);

            }


        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public void writePameasNotification(PameasNotificationTO notification) {
        try {
            log.info("pushing to  kafka {}", notification);
            this.notificationProducer.send(new ProducerRecord<>("pameas-notification", notification));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void writeToBracelets(BraceletPojo braceletPojo) {
        try {
            log.info("pushing to  kafka {}", braceletPojo);
            this.braceletProducer.send(new ProducerRecord<>("smart-bracelet-pameas-evac-msg", braceletPojo));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void writeToEvacuationCoordinator(EvacuationCoordinatorEventTO eventTO) {
        try {
            log.info("pushing to  kafka {}", eventTO);
            this.evacuationCoordinatorProducer.send(new ProducerRecord<>("evacuation-coordinator", eventTO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void writeToSmartSafetySystem(SmartSafetySystemEventTO smartSafetySystemEventTO) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String dateIndex = formatter.format(LocalDate.now());
            log.info("pushing to  kafka {}", smartSafetySystemEventTO);
            this.smartSafetyProducer.send(new ProducerRecord<>("smart-safety-system-"+dateIndex, smartSafetySystemEventTO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void writeToLegacySystem(LegacySystemTO legacySystemTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String dateIndex = formatter.format(LocalDate.now());
        try {
            log.info("pushing to  kafka {}", legacySystemTO);
            this.legacyProducer.send(new ProducerRecord<>("legacy-"+dateIndex, legacySystemTO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void writeToSRAP(SrapTO srapTO) {
        try {
            log.info("pushing to  kafka {}", srapTO);
            this.srapProducer.send(new ProducerRecord<>("srap", srapTO));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}



