package gr.aegean.palaemon.conductor.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.EvacuationCoordinatorEventTO;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.TO.PhaseTaskTO;
import gr.aegean.palaemon.conductor.model.pojo.KeycloakAccessTokenResponse;
import gr.aegean.palaemon.conductor.model.pojo.MessageObject;
import gr.aegean.palaemon.conductor.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@Slf4j
public class InitializationControllers {

    private final String OAUTH_URI = System.getenv("OAUTH_URI");
    private final String CLIENT_ID = System.getenv("CLIENT_ID");
    private final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private final String DB_PROXY_URI = System.getenv("DB_PROXY_URI"); // http://dss.aegean.gr:8090/

    @Autowired
    KafkaService kafkaService;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/initGeo")
    public @ResponseBody String initGeofences() {
        List<String> geofenceName = Arrays.asList("9G1",
                "S9-8.1", "S8-7.1", "S7-6.1", "8G1",
                "8G2", "8G3", "8G4", "8G5", "8G6", "8G7", "8G8",
                "8G9", "8G10", "7BG4", "7BG5", "7DG1", "7DG2", "7DG3", "7DG4",
                "7DG3", "7CG1", "7CG2", "7BG1", "7BG2", "7BG3", "7BG5", "7BG6");


        try {
            AtomicReference<HttpRequest> request = new AtomicReference<>();

            KeycloakAccessTokenResponse accessTokenResponse = getOAuthAccessToken();
            geofenceName.forEach(geoName -> {
                if (geoName.equals("7DG1") || geoName.equals("7BG6")) {
                    request.set(HttpRequest.newBuilder()
                            .uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                            .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : true,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
                            .build());
                } else {
                    if (geoName.indexOf("8") == 0 || geoName.indexOf("S8") == 0) {
                        request.set(HttpRequest.newBuilder()
                                .uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"8\"\n  }"))
                                .build());
                    }
                    if (geoName.indexOf("7") == 0 || geoName.indexOf("S7") == 0) {
                        request.set(HttpRequest.newBuilder()
                                .uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
                                .build());
                    }
                    if (geoName.indexOf("9") == 0 || geoName.indexOf("S9") == 0) {
                        request.set(HttpRequest.newBuilder()
                                .uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"9\"\n  }"))
                                .build());
                    }

                }
                try {
                    AtomicReference<HttpResponse<String>> response = new AtomicReference<>(HttpClient.newHttpClient().send(request.get(), HttpResponse.BodyHandlers.ofString()));
                    response.set(HttpClient.newHttpClient().send(request.get(), HttpResponse.BodyHandlers.ofString()));
                } catch (IOException | InterruptedException e) {
                    log.error(e.getMessage());

                }
            });

        } catch (Exception e) {
            log.error(e.getMessage());
            return "error";
        }

        return "OK";
    }


    @GetMapping("addTestCrew")
    public @ResponseBody String addCrewMember() {

        try {
            KeycloakAccessTokenResponse accessTokenResponse = getOAuthAccessToken();
            //add Person
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addPerson/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(" {\n   \"saturation\": \"99\",  \"heartBeat\": \"102\",  \"name\": \"NikosCrew\",\n      \"surname\": \"TestCrew\",\n      \"identifier\": \"2\",\n      \"gender\": \"Male\",\n      \"age\": \"1965-01-01\",\n      \"connectedPassengers\": [\n      ],\n      \"embarkation_port\": \"\",\n      \"disembarkation_port\": \"\",\n      \"ticketNumber\": \"\",\n      \"email\": \"triantafyllou.ni@gmail.com\",\n      \"postal_address\": \"Kallistratous 50\",\n      \"emergency_contact_details\": \"6943808730\",\n      \"country_of_residence\": \"GR\",\n      \"medical_condnitions\": \"\",\n      \"mobility_issues\": \"\",\n      \"pregnency_data\": \"\",\n      \"is_crew\": true,\n      \"role\": \"engineer\",\n      \"emergency_duty\": \"medical_unit\",\n      \"preferred_language\": [ \"IE\" ],\n      \"in_position\": false,\n      \"assignment_status\": \"UNASSIGNED\",\n      \"assigned_muster_station\": null\n}\n"))
                    .build();
            HttpResponse<String> response;
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add device
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addDevice/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"2\",\n    \"macAddress\": \"58:37:8B:DE:42:F7\",\n    \"imsi\": \"502130123456789\",\n    \"msisdn\": \"919825098250\",\n    \"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\"\n  }"))
                    .build();

            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add location
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addLocation/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"macAddress\":\"58:37:8B:DE:42:F7\",\n\t\"hashedMacAddress\":\"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"1\",\n\t\t\"gfName\":\"7BG1\",\n\t\t\"macAddress\":\"58:37:8B:DE:42:F8\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1600807918\",\n\t\t\"hashedMacAddress\":\"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\t\"timestamp\":\"1600807918\",\n\t\t\"deck\":\"7\"\n\t},\n\t \"location\":{\n\t\t \"xLocation\":\"91.91315958190962\",\n\t\t \"yLocation\":\"27.497502709677637\",\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"7\",\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\",\n\t\t \"hashedMacAddress\": \"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence1\"],\n\t\t \"timestamp\":\"1600807918\"\n\t }\n  }"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }


    @GetMapping("/activateEvacuationProtocol")
    public @ResponseBody String setEvacuationStatus() {
        //
//        PhaseTaskTO phaseTaskTO = new PhaseTaskTO("4", "4.1");
        EvacuationCoordinatorEventTO eventTO = new EvacuationCoordinatorEventTO();
        eventTO.setEvacuationStatus("2");
        eventTO.setOriginator("evacuation-coordinator");
        eventTO.setTimestamp((new Timestamp(System.currentTimeMillis())).toString());
        kafkaService.writeToEvacuationCoordinator(eventTO);
        return "OK";

    }


    @GetMapping("/initiatePassengerAlerting")
    public @ResponseBody String mockAllCrewInPosition() {
        // in previous iterations the crew members reported they were in position,
        // then this was sent to the bridge and the EC "clicked" the initiate passenger alerting
        // in current iteration with Tactilon these features are merged
        // crew members communicate to the bridge via Push2Talk
        // so the EC initiates PassengerAlerting instantly

        //http://localhost:8080/api/workflow/alert_passengers?priority=0
        /*
            {
                "phase": "5",
	            "task_id":"5.1"
            }
         */
        PhaseTaskTO phaseTaskTO = new PhaseTaskTO();
        phaseTaskTO.setPhase("5");
        phaseTaskTO.setTaskId("5.1");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<PhaseTaskTO> request =
                new HttpEntity<PhaseTaskTO>(phaseTaskTO, headers);

        String uri = "http://localhost:8080/api/workflow/alert_passengers?priority=0";
        ResponseEntity<String> responseEntityStr = restTemplate.
                postForEntity(uri, request, String.class);


//        PameasNotificationTO pameasNotification = new PameasNotificationTO();
//        pameasNotification.setStatus("");
//        pameasNotification.setId(UUID.randomUUID().toString());
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        pameasNotification.setTimestamp(timestamp.toString());
//        pameasNotification.setType("all_crew_in_position");
//        kafkaService.writeToPameasNotification(pameasNotification);
        return "OK";

    }


    private KeycloakAccessTokenResponse getOAuthAccessToken() {
        try {
            AtomicReference<HttpRequest> request = new AtomicReference<>(HttpRequest.newBuilder()
                    .uri(URI.create(OAUTH_URI))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .method("POST", HttpRequest.BodyPublishers.ofString("client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&grant_type=client_credentials&scope=openid"))
                    .build());
            AtomicReference<HttpResponse<String>> response = new AtomicReference<>(HttpClient.newHttpClient().send(request.get(), HttpResponse.BodyHandlers.ofString()));
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(response.get().body(), KeycloakAccessTokenResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


}