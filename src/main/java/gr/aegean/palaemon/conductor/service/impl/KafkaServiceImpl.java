package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.*;
import gr.aegean.palaemon.conductor.model.components.HandlePassengerIssueAsync;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.*;
import gr.aegean.palaemon.conductor.utils.CryptoUtils;
import gr.aegean.palaemon.conductor.utils.SRAPUtils;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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
    static final String SB_MESSAGE_TOPIC = "smart-bracelet-pameas-evac-msg";

    private final KafkaProducer<String, PameasNotificationTO> notificationProducer;

    private final KafkaProducer<String, BraceletPojo> braceletProducer;


    @Autowired
    private DBProxyService dbProxyService;

    @Autowired
    private DistanceCalculatorService distanceCalculatorService;

    @Autowired
    private PassengerMessagingService passengerMessagingService;
    @Autowired
    private CrewMessagingService crewMessagingService;


    @Autowired
    ElasticService elasticService;

    @Autowired
    EvacuationStatusTO evacuationStatusTO;

    @Autowired
    CryptoUtils cryptoUtils;

    @Autowired
    HandlePassengerIssueAsync handlePassengerIssueAsync;

    private final KafkaProducer<String, KafkaHeartBeatResponse> heartBeatProducer;
    private final KafkaProducer<String, EvacuationCoordinatorEventTO> evacuationCoordinatorProducer;

    private final KafkaProducer<String, SmartSafetySystemEventTO> smartSafetyProducer;

    private final KafkaProducer<String, SrapTO> srapProducer;

    private final KafkaProducer<String, LegacySystemTO> legacyProducer;

    private final KafkaProducer<String, SbPaMEASMessageTO> sbPaMEASMessageTOKafkaProducer;


    @Autowired
    public KafkaServiceImpl(KafkaProducer<String, PameasNotificationTO> notificationProducer,
                            KafkaProducer<String, BraceletPojo> braceletProducer,
                            KafkaProducer<String, KafkaHeartBeatResponse> heartBeatProducer,
                            KafkaProducer<String, EvacuationCoordinatorEventTO> evacuationCoordinatorProducer,
                            KafkaProducer<String, SmartSafetySystemEventTO> smartSafetyProducer,
                            KafkaProducer<String, SrapTO> srapProducer,
                            KafkaProducer<String, LegacySystemTO> legacyProducer,
                            KafkaProducer<String, SbPaMEASMessageTO> sbPaMEASMessageTOKafkaProducer) {
        this.notificationProducer = notificationProducer;
        this.braceletProducer = braceletProducer;
        this.heartBeatProducer = heartBeatProducer;
        this.evacuationCoordinatorProducer = evacuationCoordinatorProducer;
        this.smartSafetyProducer = smartSafetyProducer;
        this.srapProducer = srapProducer;
        this.legacyProducer = legacyProducer;
        this.sbPaMEASMessageTOKafkaProducer = sbPaMEASMessageTOKafkaProducer;
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

    @Override
    public void writeToSBMessageTO(SbPaMEASMessageTO sbPaMEASMessageTO) {
        try {
//            log.info("pushing sb message to  kafka {}", sbPaMEASMessageTO);
            this.sbPaMEASMessageTOKafkaProducer.send(new ProducerRecord<>(SB_MESSAGE_TOPIC, sbPaMEASMessageTO));
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
        log.info("Will do nothing, bridge will decide");
//        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        try {
//            SmartSafetySystemEventTO smartSafetySystemEventTO = mapper.readValue(message, SmartSafetySystemEventTO.class);
//            if (smartSafetySystemEventTO.getType().equals("Fall")
//                    || smartSafetySystemEventTO.getType().equals("Fire")
//                    || smartSafetySystemEventTO.getType().equals("Grounding")) {
//
//                Optional<String> geofenceName =
//                        distanceCalculatorService.getGeofenceFromCordsAndDeck(smartSafetySystemEventTO.getDeck(),
//                                smartSafetySystemEventTO.getPositionX(), smartSafetySystemEventTO.getPositionY());
//                if (geofenceName.isPresent()) {
//                    BlockedGeofenceTO blockedGeofenceTO = new BlockedGeofenceTO(geofenceName.get(), "blocked");
//
//                    String conductorUrl = System.getenv("CONDUCTOR_URI");
//                    HttpRequest request = HttpRequest.newBuilder()
//                            .uri(URI.create(conductorUrl + "workflow/detect_blocked_geofence?priority=0"))
//                            .header("Content-Type", "application/json")
//                            .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(blockedGeofenceTO)))
//                            .build();
//                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//                    log.info("made a call to ${} detect_blocked_geofence", conductorUrl);
//                }
//            }
//        } catch (InterruptedException | IOException e) {
//            log.error(e.getMessage());
//        }
    }

    @Override
    @KafkaListener(topics = "evacuation-coordinator", groupId = "uaeg-consumer-group", autoStartup = "true")
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
//            this.heartBeatProducer.send(new ProducerRecord<>("evacuation-component-status", response));

            //if the status was set to 2 (EMBARKATION) start the mustering flows
            //PIMM Activate Evacuation Protocol
            if (eventTO.getEvacuationStatus() == 2) {
                //instruct crew to positions!
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("4", "4.1");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/instruct_crew_to_positions?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("SEND CREW TO POSITIONS!");
                log.info("made a call to ${} instruct_crew_to_positions", conductorUrl);
            }

            if (eventTO.getEvacuationStatus() == 21) {
                //instruct crew to positions!
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("5", "5.1");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/alert_passengers?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("ALERT PASSENGERS!");
                log.info("made a call to ${} alert_passengers", conductorUrl);
            }


            //All_crew in position is called by PIMM directly

            //PIMM Passenger Mustering button
            if (eventTO.getEvacuationStatus() == 3) {
                //SEND INSTRUCTIONS (life jackets etc.)
//                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("6", "6.1");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(URI.create(conductorUrl + "workflow/embarkation_messaging?priority=0"))
//                        .header("Content-Type", "application/json")
//                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
//                        .build();
//                HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//                log.info("made a call to ${} instruct_crew_to_positions", conductorUrl);

                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("6", "6.2");
                //SEND MUSTERING DIRECTIONS
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/mustering_instruction_passengers?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String>  httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("SEND NOTIFICATIONS AND MUSTERING INSTRUCTIONS to PASSENGERS!");
                log.info("made a call to {}mustering_instruction_passengers", conductorUrl);
                log.info("response {}", httpResponse.body());
            }

            // PIMM EMBARKATION
            if (eventTO.getEvacuationStatus() == 4) {
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("7", "7.1");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/embarkation_messaging?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                log.info("SEND EMBARKATION NOTIFICATIONS!!");
                HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to ${} start embarkation", conductorUrl);
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
        response.setOriginator("PaMEAS-Location");
        if (this.evacuationStatusTO == null) {
            evacuationStatusTO = new EvacuationStatusTO();
            evacuationStatusTO.setStatus("0");
        }
        if (this.evacuationStatusTO.getStatus() == null) {
            this.evacuationStatusTO.setStatus("0");
        }

        if (!this.evacuationStatusTO.getStatus().equals("0")) {
            response.setOperationMode(1);
        } else {
            response.setOperationMode(0);
        }
        response.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
        log.info("heatBeatResponse ${}", response.toString());
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
//        log.info("message from /smart-bracelet-sensor-data ${}", message);
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
                PameasNotificationTO notificationTO = Wrappers.pameasPersonToNotificationTO(person.get(), person.get().getPersonalInfo().getPersonalId());
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
            if (srapTO.getIndividualStatus() != null) {

                srapTO.getIndividualStatus().forEach((key, value) -> {
                    if (srapTO.getIndividualStatus().get(key).equals("assistance_required")) {
                        Optional<PameasPerson> person = this.elasticService.getPersonByPersonalIdentifierDecrypted(key);
                        if (person.isPresent()) {
                            try {
                                person.get().getPersonalInfo().setName(cryptoUtils.decryptBase64Message(person.get().getPersonalInfo().getName()));
                                person.get().getPersonalInfo().setSurname(cryptoUtils.decryptBase64Message(person.get().getPersonalInfo().getSurname()));
                            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                                     IllegalBlockSizeException | BadPaddingException e) {
                                log.error(e.getMessage());
                            }


                            PameasNotificationTO notificationTO = Wrappers.pameasPersonToNotificationTO(person.get(),
                                    person.get().getPersonalInfo().getPersonalId());
                            this.writePameasNotification(notificationTO);
                        }
                    }
                });
            }

            if (!StringUtils.isEmpty(srapTO.getZoneId())) {
                if (srapTO.getStatus().equals("closed")) {
                    //Zone was blocked!!
                    String conductorUrl = System.getenv("CONDUCTOR_URI");
                    List<String> zoneGeofences = SRAPUtils.zoneId2Geofence(srapTO.getZoneId());
                    assert zoneGeofences != null;


                    zoneGeofences.forEach(geofence -> {
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


            if (pameasNotificationTO.getType().equals("PASSENGER_ALERT_COMPLETED")
                    || pameasNotificationTO.getType().equals("SEND_MUSTER_INSTRUCTIONS")) {
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

            //PASSENGER_EXITING_MS
            if (pameasNotificationTO.getType().equals("PASSENGER_EXITING_MS")) {
                Optional<PameasPerson> person = this.elasticService.getPersonByHashedMacAddress(DigestUtils.sha256Hex(pameasNotificationTO.getMacAddress()));
                Optional<PameasPerson> crewMember =
                        this.elasticService.getCrewAssignedToMS(person.get().getPersonalInfo().getAssignedMusteringStation());

                if (person.isPresent()) {
                    List<MessageBody> messageBodies = new ArrayList<>();
                    MessageBody mb = new MessageBody();
                    mb.setContent("<header></header><main><h2 style='color: red; text-align: center;'>ALERT</h2>" +
                            "<div style='font-size: x-large;'><b style='color: red; text-align: center;'>" +
                            "Your are leaving the Muster Station! <span>RETURN IMMEDIATELY!</span></b></div>"+
                            "</main>:: sound: siren");
                    mb.setRecipient(DigestUtils.sha256Hex(pameasNotificationTO.getMacAddress()));
                    messageBodies.add(mb);
                    this.passengerMessagingService.callSendMessages(messageBodies);

                    MessageBody mb2 = new MessageBody();
                    mb2.setContent("ATTENTION!!! Passenger "
                            + this.cryptoUtils.decryptBase64Message(person.get().getPersonalInfo().getSurname()) +
                            " "
                            + this.cryptoUtils.decryptBase64Message(person.get().getPersonalInfo().getName()) +
                            " is leaving the Muster Station!!");
                    mb2.setRecipient(DigestUtils.sha256Hex(crewMember.get().getNetworkInfo().getDeviceInfoList().get(0).getMacAddress()));
                    MessageBody mb3 = new MessageBody();
                    mb3.setContent("Passenger is heading towards :"
                            + person.get().getLocationInfo().getGeofenceHistory().get(person.get().getLocationInfo().getGeofenceHistory().size() - 1).getGfName());
                    mb3.setRecipient(DigestUtils.sha256Hex(crewMember.get().getNetworkInfo().getDeviceInfoList().get(0).getMacAddress()));
                    messageBodies.clear();
                    messageBodies.add(mb2);
                    messageBodies.add(mb3);
                    this.crewMessagingService.callSendMessages(messageBodies);
                }

            }

            // read Passenger Issue and proceed with its resolution
            if (pameasNotificationTO.getType().equals("PASSENGER_ISSUE")) {
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                ConstraintSolverIncident incident = Wrappers.pameasNotificationTO2ConstraintSolverIncident(pameasNotificationTO);
                this.handlePassengerIssueAsync.addIncident(incident);
//                ArrayList<ConstraintSolverIncident> incidentArrayList = new ArrayList<>();
//                incidentArrayList.add(incident);
//
//                ProposeAssignmentRequestTO requestTO = new ProposeAssignmentRequestTO(incidentArrayList);
//
//                HttpRequest request = HttpRequest.newBuilder()
//                        .uri(URI.create(conductorUrl + "workflow/porpose_assignment?priority=0"))
//                        .header("Content-Type", "application/json")
//                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestTO)))
//                        .build();
//                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//                log.info("made a call to {}porpose_assignment", conductorUrl);
//                log.info("response {}", response.body());

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


        } catch (IOException | InterruptedException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
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
            this.smartSafetyProducer.send(new ProducerRecord<>("smart-safety-system-" + dateIndex, smartSafetySystemEventTO));
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
            this.legacyProducer.send(new ProducerRecord<>("legacy-" + dateIndex, legacySystemTO));
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


    @Override
    @KafkaListener(topics = "legacy", groupId = "uaeg-consumer-group")
    public void monitorLegacy(String message) {
        log.info("message from /legacy ${}", message);

    }

    @Override
    @KafkaListener(topics = "shm-alarm", groupId = "uaeg-consumer-group")
    public void monitorShmAlarmLegacy(String message) {
        log.info("message from /shm-alarm ${}", message);

    }


    @Override
    @KafkaListener(topics = "stability-toolkit", groupId = "uaeg-consumer-group")
    public void monitorStabilityToolkit(String message) {
        log.info("message from /stability-toolkit ${}", message);
    }

    @Override
    @KafkaListener(topics = "cameras", groupId = "uaeg-consumer-group")
    public void monitorCameras(String message) {
        log.info("message from /cameras ${}", message);
    }

    @Override
    @KafkaListener(topics = "weather", groupId = "uaeg-consumer-group")
    public void monitorWeather(String message) {
        log.info("message from /weather ${}", message);
    }

    @KafkaListener(topics = "smoke-detector", groupId = "uaeg-consumer-group")
    public void monitorSmoke(String message) {
        log.info("message from /smoke-detector ${}", message);
    }
}



