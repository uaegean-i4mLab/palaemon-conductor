package gr.aegean.palaemon.conductor.controllers;

import gr.aegean.palaemon.conductor.model.TO.*;
import gr.aegean.palaemon.conductor.model.pojo.KeycloakAccessTokenResponse;
import gr.aegean.palaemon.conductor.model.pojo.LegacySystemTO;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import gr.aegean.palaemon.conductor.model.pojo.SmokeDetectedTO;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
                "7DG3", "7CG1", "7CG2", "7BG1", "7BG2", "7BG3", "7BG5", "7BG6",
                "9BG1", "9BG2", "9CG0", "9BG4", "9BG3", "9BG1+");


        try {


            KeycloakAccessTokenResponse accessTokenResponse = TestingUtils.getOAuthAccessToken();
            geofenceName.forEach(geoName -> {
//                log.info("writing geo:" + geoName);
                HttpRequest.Builder request = HttpRequest.newBuilder();
                if (geoName.equals("7DG4") || geoName.equals("7BG6") || geoName.equals("9CG0")) {

                    if (!geoName.equals("9CG0")) {
                        request.uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" +
                                        geoName + "\",\n \t\t \"mustering\" : true,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"));
                    } else {
                        request.uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" +
                                        geoName + "\",\n \t\t \"mustering\" : true,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"9\"\n  }"));
                    }


                } else {
                    if (geoName.indexOf("8") == 0 || geoName.indexOf("S8") == 0) {
                        request
                                .uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"8\"\n  }"))
                        ;
                    }
                    if (geoName.indexOf("7") == 0 || geoName.indexOf("S7") == 0) {
                        request.uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
                        ;
                    }
                    if (geoName.indexOf("9") == 0 || geoName.indexOf("S9") == 0) {
                        request
                                .uri(URI.create(this.DB_PROXY_URI + "addGeofence/"))
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"9\"\n  }"))
                        ;
                    }

                }
                try {
                    HttpClient.newHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());

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
            KeycloakAccessTokenResponse accessTokenResponse = TestingUtils.getOAuthAccessToken();
            //add Person
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addPerson/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(" {\n   \"saturation\": \"99\",  " +
                            "\"heartBeat\": \"102\",  \"name\": \"NikosCrew\",\n    " +
                            "  \"surname\": \"TestCrew\",\n      \"identifier\": \"2\",\n  " +
                            "    \"gender\": \"Male\",\n      \"age\": \"1965-02-02\",\n     " +
                            " \"connectedPassengers\": [\n      ],\n      \"embarkation_port\": \"\",\n    " +
                            "  \"disembarkation_port\": \"\",\n      \"ticketNumber\": \"\",\n     " +
                            " \"email\": \"crew@gmail.com\",\n      \"postal_address\": \"Kallistratous 50\",\n    " +
                            "  \"emergency_contact_details\": \"6943808730\",\n      \"country_of_residence\": \"GR\",\n     " +
                            " \"medical_condnitions\": \"\",\n      \"mobility_issues\": \"\",\n    " +
                            "  \"pregnency_data\": \"\",\n      \"is_crew\": true,\n      \"role\": \"engineer\",\n   " +
                            "   \"emergency_duty\": \"medical_unit\",\n      \"preferred_language\": [ \"IE\" ],\n     " +
                            " \"in_position\": false,\n      \"assignment_status\": \"UNASSIGNED\",\n      \"assigned_muster_station\": null\n}\n"))
                    .build();
            HttpResponse<String> response;
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add device
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addDevice/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"2\",\n   " +
                            " \"macAddress\": \"58:37:8B:DE:42:F8\",\n    " +
                            "\"imsi\": \"502130123456789\",\n    \"msisdn\": \"306943808730\",\n    " +
                            "\"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"306943808730\"  }"))
                    .build();

            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add location
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addLocation/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n  " +
                            "  \"macAddress\":\"58:37:8B:DE:42:F7\"," +
                            "\n\t\"hashedMacAddress\":\"dabf99b54f86bcc4f5949a8bcd7e29961081b36d42cb0905e1e52a131652adf9\"," +
                            "\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"1\",\n\t\t\"gfName\":\"7BG1\"," +
                            "\n\t\t\"macAddress\":\"58:37:8B:DE:42:F8\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1665427687\"," +
                            "\n\t\t\"hashedMacAddress\":\"dabf99b54f86bcc4f5949a8bcd7e29961081b36d42cb0905e1e52a131652adf9\"," +
                            "\n\t\t\"timestamp\":\"1665427687\",\n\t\t\"deck\":\"7\"\n\t}," +
                            "\n\t \"location\":{\n\t\t \"xLocation\":\"91.91315958190962\",\n\t\t \"yLocation\":\"27.497502709677637\"," +
                            "\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"7\"," +
                            "\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\"," +
                            "\n\t\t \"hashedMacAddress\": \"dabf99b54f86bcc4f5949a8bcd7e29961081b36d42cb0905e1e52a131652adf9\"," +
                            "\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence1\"],\n\t\t \"timestamp\":\"1665427687\"\n\t }\n  }"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }


    @GetMapping("addTestPassengerWithHealthIssues")
    public @ResponseBody String addTestPassengerWithHealthIssues() {

        try {
            KeycloakAccessTokenResponse accessTokenResponse = TestingUtils.getOAuthAccessToken();
            //add Person
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addPerson/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(" {\n   \"saturation\": \"99\",  \"heartBeat\": \"102\", " +
                            " \"name\": \"John\",\n      \"surname\": \"Doe\",\n      \"identifier\": \"3\",\n    " +
                            "  \"gender\": \"Male\",\n      \"age\": \"1965-01-01\",\n      \"connectedPassengers\": [\n      ],\n    " +
                            "  \"embarkation_port\": \"\",\n      \"disembarkation_port\": \"\",\n      \"ticketNumber\": \"\",\n    " +
                            "  \"email\": \"triantafyllou.ni@gmail.com\",\n      \"postal_address\": \"Kallistratous 50\",\n  " +
                            "    \"emergency_contact_details\": \"6943808730\",\n      \"country_of_residence\": \"GR\",\n     " +
                            " \"medical_condnitions\": \"equip_required\",\n      \"mobility_issues\": \"\",\n   " +
                            "   \"pregnency_data\": \"\",\n      \"is_crew\": false,\n      \"role\": \"passenger\",\n     " +
                            " \"emergency_duty\": \"\",\n      \"preferred_language\": [ \"IE\" ],\n      \"in_position\": false,\n  " +
                            "    \"assignment_status\": \"UNASSIGNED\",\n      \"assigned_muster_station\": null\n}\n"))
                    .build();
            HttpResponse<String> response;
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add device
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addDevice/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"3\",\n   " +
                            " \"macAddress\": \"58:37:8B:DE:42:G7\",\n    \"imsi\": \"502130123456789\",\n  " +
                            "  \"msisdn\": \"919825098250\",\n  " +
                            "  \"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"Nikos-Admin\", " +
                            " \"braceletId\": \"SB0001\" }"))
                    .build();


            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add location
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addLocation/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n  " +
                            "  \"macAddress\":\"58:37:8B:DE:42:G7\"," +
                            "\n\t\"hashedMacAddress\":\"5f4f0a1aa2f93d195f609e4b50870d53ac4a1930525f0902a1a45ecc5a254565\"," +
                            "\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"1\",\n\t\t\"gfName\":\"7BG1\"," +
                            "\n\t\t\"macAddress\":\"58:37:8B:DE:42:G7\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1665427687\"," +
                            "\n\t\t\"hashedMacAddress\":\"5f4f0a1aa2f93d195f609e4b50870d53ac4a1930525f0902a1a45ecc5a254565\"," +
                            "\n\t\t\"timestamp\":\"1665427687\",\n\t\t\"deck\":\"7\"\n\t}," +
                            "\n\t \"location\":{\n\t\t \"xLocation\":\"91.91315958190962\",\n\t\t \"yLocation\":\"27.497502709677637\"," +
                            "\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"7\"," +
                            "\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\"," +
                            "\n\t\t \"hashedMacAddress\": \"5f4f0a1aa2f93d195f609e4b50870d53ac4a1930525f0902a1a45ecc5a254565\"," +
                            "\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence1\"],\n\t\t \"timestamp\":\"1665427687\"\n\t }\n  }"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }


    @GetMapping("addTestPassengerWithMobilityIssues")
    public @ResponseBody String addTestPassengerWithMobilityIssues() {

        try {
            KeycloakAccessTokenResponse accessTokenResponse = TestingUtils.getOAuthAccessToken();
            //add Person
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addPerson/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(" {\n   \"saturation\": \"99\",  \"heartBeat\": \"102\", " +
                            " \"name\": \"Jane\",\n      \"surname\": \"Doe\",\n      \"identifier\": \"01\",\n    " +
                            "  \"gender\": \"Female\",\n      \"age\": \"1965-01-01\",\n      \"connectedPassengers\": [\n      ],\n    " +
                            "  \"embarkation_port\": \"\",\n      \"disembarkation_port\": \"\",\n      \"ticketNumber\": \"\",\n    " +
                            "  \"email\": \"triantafyllou.ni@gmail.com\",\n      \"postal_address\": \"Kallistratous 50\",\n  " +
                            "    \"emergency_contact_details\": \"6943808730\",\n      \"country_of_residence\": \"GR\",\n     " +
                            " \"medical_condnitions\": \"\",\n      \"mobility_issues\": \"unable_to_walk\",\n   " +
                            "   \"pregnency_data\": \"\",\n      \"is_crew\": false,\n      \"role\": \"passenger\",\n     " +
                            " \"emergency_duty\": \"\",\n      \"preferred_language\": [ \"IE\" ],\n      \"in_position\": false,\n  " +
                            "    \"assignment_status\": \"UNASSIGNED\",\n      \"assigned_muster_station\": null\n}\n"))
                    .build();
            HttpResponse<String> response;
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add device
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addDevice/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"01\",\n   " +
                            " \"macAddress\": \"58:37:8B:DE:42:G8\",\n    \"imsi\": \"502130123456789\",\n  " +
                            "  \"msisdn\": \"919825098250\",\n  " +
                            "  \"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"Nikos-Admin\", " +
                            " \"braceletId\": \"SB0005\" }"))
                    .build();


            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add location
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addLocation/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n  " +
                            "  \"macAddress\":\"58:37:8B:DE:42:G8\"," +
                            "\n\t\"hashedMacAddress\":\"bdc3cff7d90a485e082be0cdd06442e7040ffef6d4bbb40478b34ffd1e6c30bb\"," +
                            "\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"1\",\n\t\t\"gfName\":\"7BG1\"," +
                            "\n\t\t\"macAddress\":\"58:37:8B:DE:42:G8\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1665427687\"," +
                            "\n\t\t\"hashedMacAddress\":\"bdc3cff7d90a485e082be0cdd06442e7040ffef6d4bbb40478b34ffd1e6c30bb\"," +
                            "\n\t\t\"timestamp\":\"1665427687\",\n\t\t\"deck\":\"7\"\n\t}," +
                            "\n\t \"location\":{\n\t\t \"xLocation\":\"91.91315958190962\",\n\t\t \"yLocation\":\"27.497502709677637\"," +
                            "\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"7\"," +
                            "\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\"," +
                            "\n\t\t \"hashedMacAddress\": \"bdc3cff7d90a485e082be0cdd06442e7040ffef6d4bbb40478b34ffd1e6c30bb\"," +
                            "\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence1\"],\n\t\t \"timestamp\":\"1665427687\"\n\t }\n  }"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }


    @GetMapping("addTestPassengerOn7DG3")
    public @ResponseBody String addTestPassengerOn7DG3() {
        try {
            KeycloakAccessTokenResponse accessTokenResponse = TestingUtils.getOAuthAccessToken();
            //add Person
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addPerson/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(" {\n   \"saturation\": \"99\", " +
                            " \"heartBeat\": \"102\",  \"name\": \"p2\",\n      \"surname\": \"p2_surname\",\n     " +
                            " \"identifier\": \"02\",\n      \"gender\": \"Male\",\n      \"age\": \"1950-01-01\",\n  " +
                            "    \"connectedPassengers\": [\n      ],\n      \"embarkation_port\": \"PIREAUS\",\n    " +
                            "  \"disembarkation_port\": \"CHANIA\",\n      \"ticketNumber\": \"123\",\n      \"email\": \"p2@gmail.com\",\n   " +
                            "   \"postal_address\": \"Kallistratous 50\",\n      \"emergency_contact_details\": \"6943808730\",\n   " +
                            "   \"country_of_residence\": \"GR\",\n      \"medical_condnitions\": \"none\",\n      \"mobility_issues\": \"\",\n    " +
                            "  \"pregnency_data\": \"\",\n      \"is_crew\": false,\n      \"role\": \"passenger\",\n      \"emergency_duty\": \"\",\n " +
                            "     \"preferred_language\": [ \"EN\" ],\n      \"in_position\": false,\n      \"assignment_status\": \"UNASSIGNED\",\n  " +
                            "    \"assigned_muster_station\": null\n}\n"))
                    .build();
            HttpResponse<String> response;
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add device
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addDevice/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"02\",\n   " +
                            " \"macAddress\": \"58:37:8B:DE:42:F7\",\n    \"imsi\": \"502130123456789\",\n  " +
                            "  \"msisdn\": \"919825098250\",\n  " +
                            "  \"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"Mumla_User\", " +
                            " \"braceletId\": \"SB0003\" }"))
                    .build();

            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            //add location
            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(this.DB_PROXY_URI + "addLocation/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"macAddress\":\"58:37:8B:DE:42:F7\"," +
                            "\n\t\"hashedMacAddress\":\"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"1\",\n\t\t\"gfName\":\"7DG3\",\n\t\t\"macAddress\":\"58:37:8B:DE:42:F8\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1665427687\",\n\t\t\"hashedMacAddress\":\"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\t\"timestamp\":\"1665427687\",\n\t\t\"deck\":\"7\"\n\t},\n\t \"location\":{\n\t\t \"xLocation\":\"91.91315958190962\",\n\t\t \"yLocation\":\"27.497502709677637\",\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"7\",\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\",\n\t\t \"hashedMacAddress\": \"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence1\"],\n\t\t \"timestamp\":\"1665427687\"\n\t }\n  }"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }

    @GetMapping("addOnlyTestCrewOnD9")
    public @ResponseBody String addOnlyTestCrewOnD9() {
        try {
            TestingUtils.addTestPerson("medical_unit", "99", "102", "D9_test12",
                    "D9_test12_sur", "D9_12", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test12@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "9CG0, muster-station", "58:37:8B:DE:42:B0",
                    "502130123456789", "919825098250", "306943808730", "SB00012", "9", "1665427687",
                    "1", "event", "1231", "9BG3", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9BG3"));
            // ADD a crew member on 9BG4 corridor
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "D9_test13",
                    "D9_test12_sur", "D9_13", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", null, "58:37:8B:DE:42:B1",
                    "502130123456789", "919825098250", "306943808730", "SB00013", "9", "1665427687",
                    "1", "event", "1231", "9BG4", "true", "9", "26.80",
                    "90.50", "1", "0", List.of("9BG4"));
            // ADD a crew member on 9BG1
            TestingUtils.addTestPerson("first_response_unit", "99", "102", "D9_test14",
                    "D9_test14_sur", "D9_14", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", null, "58:37:8B:DE:42:B2",
                    "502130123456789", "919825098250", "306943808730", "SB00014", "9", "1665427687",
                    "1", "event", "1231", "9BG1", "true", "9", "24.80",
                    "85.50", "1", "0", List.of("9BG1"));


        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }

    @GetMapping("addTestPassengersOnD9")
    public @ResponseBody String addTestPassengersOnD9() {
        try {
            //Healthy Test person on 9BG2 with Mumla_User and Nikos MAC Address
            TestingUtils.addTestPerson("", "99", "102", "D9_test1",
                    "D9_test1_sur", "D9_1", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "123", "test@test.gr", "Address 1", "306943808730",
                    "GR", "none", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:F7",
                    "502130123456789", "919825098250", "Mumla_User", "SB0001", "9", "1665427687",
                    "1", "event", "1231", "9BG2", "true", "9", "27.497502709677637",
                    "91.91315958190962", "1", "0", List.of("9CG2"));
            //Healthy Test user 9BG1
            TestingUtils.addTestPerson("", "99", "102", "D9_test2",
                    "D9_test2_sur", "D9_2", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test2@test.gr", "Address 2", "306943808730",
                    "GR", "none", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:F8",
                    "502130123456789", "919825098250", "Plumble_User", "SB0002", "9", "1665427687",
                    "1", "event", "1231", "9BG1", "true", "9", "27.80",
                    "92.50", "1", "0", List.of("9BG1"));
            // Healthy Test user on 9BG3
            TestingUtils.addTestPerson("", "99", "102", "D9_test3",
                    "D9_test3_sur", "D9_3", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test3@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:F9",
                    "502130123456789", "919825098250", "Nikos-Admin-2", "SB0003", "9", "1665427687",
                    "1", "event", "1231", "9BG3", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9BG3"));
            // Healthy Test user on 9BG4
            TestingUtils.addTestPerson("", "99", "102", "D9_test4",
                    "D9_test4_sur", "D9_4", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test4@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A1",
                    "502130123456789", "919825098250", "9BG4-User", "SB0004", "9", "1665427687",
                    "1", "event", "1231", "9BG4", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9BG4"));
            // Healthy Test user on 9BG1+ (stairs)
            TestingUtils.addTestPerson("", "99", "102", "D9_test5",
                    "D9_test5_sur", "D9_5", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test5@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A2",
                    "502130123456789", "919825098250", "9BG4-User", "SB0005", "9", "1665427687",
                    "1", "event", "1231", "9BG1+", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9BG1+"));
            // Healthy Test user on 9CG0 (stairs)
            TestingUtils.addTestPerson("", "99", "102", "D9_test6",
                    "D9_test6_sur", "D9_6", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test6@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A3",
                    "502130123456789", "919825098250", "9CG0-User", "SB0006", "9", "1665427687",
                    "1", "event", "1231", "9CG0", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9CG0"));
            // Healthy Test user on GCab9223
            TestingUtils.addTestPerson("", "99", "102", "D9_test7",
                    "D9_test7_sur", "D9_7", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test7@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A4",
                    "502130123456789", "919825098250", "GCab9223-User", "SB0007", "9", "1665427687",
                    "1", "event", "1231", "GCab9223", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("GCab9223"));
            // Healthy Test user on GCab9217
            TestingUtils.addTestPerson("", "99", "102", "D9_test8",
                    "D9_test8_sur", "D9_8", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test8@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A5",
                    "502130123456789", "919825098250", "GCab9222-User", "SB0008", "9", "1665427687",
                    "1", "event", "1231", "GCab9217", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("GCab9217"));
            // Mobility Issues Test user on 9BG2
            TestingUtils.addTestPerson("", "99", "102", "D9_test9",
                    "D9_test9_sur", "D9_9", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test8@test.gr", "Address 3", "306943808730",
                    "GR", "", "unable_to_walk", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A6",
                    "502130123456789", "919825098250", "Mobility-Issues", "SB0009", "9", "1665427687",
                    "1", "event", "1231", "9BG2", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9BG2"));
            // Health Issues Test user on 9BG2
            TestingUtils.addTestPerson("", "99", "102", "D9_test10",
                    "D9_test10_sur", "D9_10", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test8@test.gr", "Address 3", "306943808730",
                    "GR", "equip_required", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A7",
                    "502130123456789", "919825098250", "Health-Issues", "SB0009", "9", "1665427687",
                    "1", "event", "1231", "9BG2", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9BG2"));
            // Pregnancy Issues Test user on 9BG2
            TestingUtils.addTestPerson("", "99", "102", "D9_test11",
                    "D9_test11_sur", "D9_11", "female", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test8@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "complicated", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:A8",
                    "502130123456789", "919825098250", "Pregnancy-Issues", "SB0009", "9", "1665427687",
                    "1", "event", "1231", "9BG2", "true", "9", "26.80",
                    "93.50", "1", "0", List.of("9BG2"));


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
        eventTO.setEvacuationStatus(2);
        eventTO.setOriginator("evacuation-coordinator");
        eventTO.setTimestamp((new Timestamp(System.currentTimeMillis())).toString());
        kafkaService.writeToEvacuationCoordinator(eventTO);
        return "OK";

    }

    @GetMapping("/activateSituationAssessment")
    public @ResponseBody String activateSituationAssessment() {
        EvacuationCoordinatorEventTO eventTO = new EvacuationCoordinatorEventTO();
        eventTO.setEvacuationStatus(1);
        eventTO.setOriginator("evacuation-coordinator");
        eventTO.setTimestamp((new Timestamp(System.currentTimeMillis())).toString());
        kafkaService.writeToEvacuationCoordinator(eventTO);
        return "OK";
    }


    @GetMapping("/allCrewInPosition")
    public @ResponseBody String allCrewInPosition() {
        PameasNotificationTO pameasNotification = new PameasNotificationTO();
        pameasNotification.setStatus("");
        pameasNotification.setId(UUID.randomUUID().toString());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        pameasNotification.setTimestamp(timestamp.toString());
        pameasNotification.setType("all_crew_in_position");
        kafkaService.writeToPameasNotification(pameasNotification);
        return "OK";
    }


    @GetMapping("/sendPassengerInstructions")
    public @ResponseBody String sendInstructionsToPassengers() {
        PameasNotificationTO pameasNotification = new PameasNotificationTO();
        pameasNotification.setStatus("");
        pameasNotification.setId(UUID.randomUUID().toString());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        pameasNotification.setTimestamp(timestamp.toString());
        pameasNotification.setType("SEND_MUSTER_INSTRUCTIONS");
        kafkaService.writeToPameasNotification(pameasNotification);
        return "OK";
    }


    @GetMapping("/initiatePassengerAlerting")
    public @ResponseBody String initiatePassengerAlerting() {
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

        String uri = System.getenv("CONDUCTOR_URI") + "/workflow/alert_passengers?priority=0";
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

    @PostMapping("/smart-safety-event")
    public @ResponseBody String blockGeofence(@RequestBody SmartSafetySystemEventTO eventTO) {
        this.kafkaService.writeToSmartSafetySystem(eventTO);
        return "OK";
    }


    @PostMapping("/generateSmokeDetectorAlarm")
    public @ResponseBody String generateSmokeDetector(@RequestBody SmokeDetectedTO smokeDetectedTO) {
        LegacySystemTO legacySystemTO = new LegacySystemTO();
        legacySystemTO.setGzgm(true);
        legacySystemTO.setNavigationalSystem(true);
        legacySystemTO.setAdjecentDetectors(true);
        legacySystemTO.setPropulsionSystem(true);
        legacySystemTO.setSteeringSystem(true);
        legacySystemTO.setMitigationSystemActivated(true);
        legacySystemTO.setExternalCommunication(true);
        legacySystemTO.setInternalCommunication(false);
        legacySystemTO.setContainmentDoorsClosed(true);

        this.kafkaService.writeToLegacySystem(legacySystemTO);


        SmartSafetySystemEventTO smartSafetySystemEventTO = new SmartSafetySystemEventTO();
        smartSafetySystemEventTO.setType("Fire");
        smartSafetySystemEventTO.setDeck(smokeDetectedTO.getDeck());
        smartSafetySystemEventTO.setPositionX(smokeDetectedTO.getXPosition());
        smartSafetySystemEventTO.setPositionY(smartSafetySystemEventTO.getPositionY());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        smartSafetySystemEventTO.setTimestamp(timestamp.toString());

        this.kafkaService.writeToSmartSafetySystem(smartSafetySystemEventTO);

        return "ok";
    }


}
