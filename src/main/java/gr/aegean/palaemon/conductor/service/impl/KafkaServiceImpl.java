package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.*;
import gr.aegean.palaemon.conductor.model.pojo.BraceletPojo;
import gr.aegean.palaemon.conductor.model.pojo.ConstraintSolverIncident;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.DistanceCalculatorService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
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
    public KafkaServiceImpl(KafkaProducer<String, PameasNotificationTO> notificationProducer,
                            KafkaProducer<String, BraceletPojo> braceletProducer) {
        this.notificationProducer = notificationProducer;
        this.braceletProducer = braceletProducer;

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
            if (smartSafetySystemEventTO.getType().equals("'Fall")
                    || smartSafetySystemEventTO.getType().equals("'Fire")
                    || smartSafetySystemEventTO.getType().equals("'Grounding")) {

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
    @KafkaListener(topics = "evacuation-coordinator", groupId = "uaeg-consumer-group")
    public void monitorEvacuationCoordinator(String message) {
        log.info("Received Message in evacuation-coordinator ${}", message);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            EvacuationCoordinatorEventTO eventTO = mapper.readValue(message, EvacuationCoordinatorEventTO.class);
            if (eventTO.getEvacuationStatus().equals("2")) {
                PhaseTaskTO phaseTaskTO = new PhaseTaskTO("4", "4.1");
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(conductorUrl + "workflow/instruct_crew_to_positions?priority=0"))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(phaseTaskTO)))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("made a call to ${} instruct_crew_to_positions", conductorUrl);
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
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

            //handle the declaration of a new passenger emergency
            if (pameasNotificationTO.getType().equals("PASSENGER_EMERGENCY")) {
                String conductorUrl = System.getenv("CONDUCTOR_URI");
                String passengerHashedMacAddress = pameasNotificationTO.getId();
                if(passengerHashedMacAddress == null){
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
            //


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
    @KafkaListener(topics = "smart-bracelet-event-notification", groupId = "uaeg-consumer-group")
    public void monitorBraceletFall(String message) {
        log.info("Fall detected {}", message);
    }

    @Override
    @KafkaListener(topics = "smart-bracelet-sensor-data", groupId = "uaeg-consumer-group")
    public void monitorBraceletHealth(String message) {
//        log.info("Health data {}", message);
    }
}
