package gr.aegean.palaemon.conductor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.EvacuationStatusTO;
import gr.aegean.palaemon.conductor.model.TO.PersonFullTO;
import gr.aegean.palaemon.conductor.model.TO.UpdatePersonStatusTO;
import gr.aegean.palaemon.conductor.model.pojo.DeviceInfo;
import gr.aegean.palaemon.conductor.model.pojo.KeycloakAccessTokenResponse;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
public class GenerateDataTests {


    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void addGeofence() throws IOException, InterruptedException {

        List<String> geofenceName = Arrays.asList("9G1",
                "S9-8.1",
                "S8-7.1",
                "S7-6.1",
                "8G1",
                "8G2",
                "8G3",
                "8G4",
                "8G5",
                "8G6",
                "8G7",
                "8G8",
                "8G9",
                "8G10",
                "7BG4",
                "7BG5",

                "7DG1",
                "7DG2",
                "7DG3",
                "7DG4",
                "7DG3",
                "7CG1",
                "7CG2",
                "7BG1",
                "7BG2",
                "7BG3",
                "7BG5",
                "7BG6");


        AtomicReference<HttpRequest> request = new AtomicReference<>(HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build());
        AtomicReference<HttpResponse<String>> response = new AtomicReference<>(HttpClient.newHttpClient().send(request.get(), HttpResponse.BodyHandlers.ofString()));
        System.out.println(response.get().body());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.get().body(), KeycloakAccessTokenResponse.class);

        geofenceName.forEach(geoName -> {
            if (geoName.equals("7DG1") || geoName.equals("7BG6")) {
                request.set(HttpRequest.newBuilder()
                        .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                        .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : true,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
                        .build());
            } else {
                if (geoName.indexOf("8") == 0 || geoName.indexOf("S8") == 0) {
                    request.set(HttpRequest.newBuilder()
                            .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                            .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"8\"\n  }"))
                            .build());
                }
                if (geoName.indexOf("7") == 0 || geoName.indexOf("S7") == 0) {
                    request.set(HttpRequest.newBuilder()
                            .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                            .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
                            .build());
                }
                if (geoName.indexOf("9") == 0 || geoName.indexOf("S9") == 0) {
                    request.set(HttpRequest.newBuilder()
                            .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                            .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"" + geoName + "\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"9\"\n  }"))
                            .build());
                }

            }

            try {
                response.set(HttpClient.newHttpClient().send(request.get(), HttpResponse.BodyHandlers.ofString()));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"geofence 1\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
//                .build();
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"geofence 3\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
//                .build();
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"geofence 4\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
//                .build();
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"geofence 5\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
//                .build();
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"geofence 6\",\n \t\t \"mustering\" : false,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
//                .build();
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"Muster Station\",\n \t\t \"mustering\" : true,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
//                .build();
//
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//        request = HttpRequest.newBuilder()
//                .uri(URI.create("http://dss.aegean.gr:8090/addGeofence/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\n   \t \"gfName\" : \"geofence 2\",\n \t\t \"mustering\" : true,\n\t\t \"status\":\"OPEN\",\n\t\t \"deck\":\"7\"\n  }"))
//                .build();
//        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//
//        System.out.println(response.body());
    }


    @Test
    public void testAddPassengerData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.body(), KeycloakAccessTokenResponse.class);

        //add Person
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addPerson/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString(" {\n      \"name\": \"test1\",\n      \"surname\": \"testSurname1\",\n      \"identifier\": \"1\",\n      \"gender\": \"Male\",\n      \"age\": \"1965-01-01\",\n      \"connectedPassengers\": [\n        {\n          \"name\": \"Nikos\",\n          \"surname\": \"Triantafyllou\",\n          \"gender\": \"Male\",\n          \"age\": \"2007-05-10\"\n        }\n\n      ],\n      \"embarkation_port\": \"pireaus\",\n      \"disembarkation_port\": \"chios\",\n      \"ticketNumber\": \"123\",\n      \"email\": \"triantafyllou.ni@gmail.com\",\n      \"postal_address\": \"Kallistratous 50\",\n      \"emergency_contact_details\": \"6943808730\",\n      \"country_of_residence\": \"GR\",\n      \"medical_condnitions\": \"equip_required\",\n      \"mobility_issues\": \"none\",\n      \"pregnency_data\": \"normal\",\n      \"is_crew\": false,\n      \"role\": \"passenger\",\n\n\n\n      \"emergency_duty\": \"\",\n\n      \"preferred_language\": [ \"IE\" ],\n      \"in_position\": false,\n      \"assignment_status\": \"ASSIGNED\",\n      \"assigned_muster_station\": null\n}\n"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        TimeUnit.SECONDS.sleep(2);
        //add device
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addDevice/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"1\",\n    \"macAddress\": \"58:37:8B:DE:42:F9\",\n    \"imsi\": \"502130123456789\",\n    \"msisdn\": \"919825098250\",\n    \"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"18a4f641457adea15e4ff9f8d203802ba749714f87e22aedffd2a4dcb33b4f65\"\n  }"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        TimeUnit.SECONDS.sleep(2);
        //add location
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addLocation/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"macAddress\":\"58:37:8B:DE:42:F9\",\n\t\"hashedMacAddress\":\"18a4f641457adea15e4ff9f8d203802ba749714f87e22aedffd2a4dcb33b4f65\",\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"2\",\n\t\t\"gfName\":\"7DG1\",\n\t\t\"deck\":\"7\",\n\t\t\"macAddress\":\"58:37:8B:DE:42:F8\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1600807918\",\n\t\t\"hashedMacAddress\":\"18a4f641457adea15e4ff9f8d203802ba749714f87e22aedffd2a4dcb33b4f65\",\n\t\t\"timestamp\":\"1600807918\"\n\t\t\n\t},\n\t \"location\":{\n\t\t \"xLocation\":\"72.5425\",\n\t\t \"yLocation\":\"34.2354\",\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"deckA\",\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\",\n\t\t \"hashedMacAddress\": \"18a4f641457adea15e4ff9f8d203802ba749714f87e22aedffd2a4dcb33b4f65\",\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence2\"],\n\t\t \"timestamp\":\"1600807918\"\n\t }\n  }"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }


    @Test
    public void testSecondPassengerData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.body(), KeycloakAccessTokenResponse.class);

        //add Person
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addPerson/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString(" {\n      \"name\": \"test2\",\n      \"surname\": \"testSurname2\",\n      \"identifier\": \"3\",\n      \"gender\": \"Female\",\n      \"age\": \"1965-02-02\",\n      \"connectedPassengers\": [\n        {\n          \"name\": \"Nikos\",\n          \"surname\": \"Triantafyllou\",\n          \"gender\": \"Male\",\n          \"age\": \"2007-05-10\"\n        }\n\n      ],\n      \"embarkation_port\": \"pireaus\",\n      \"disembarkation_port\": \"chios\",\n      \"ticketNumber\": \"123\",\n      \"email\": \"triantafyllou.ni@gmail.com\",\n      \"postal_address\": \"Kallistratous 50\",\n      \"emergency_contact_details\": \"6943808730\",\n      \"country_of_residence\": \"GR\",\n      \"medical_condnitions\": \"equip_required\",\n      \"mobility_issues\": \"none\",\n      \"pregnency_data\": \"normal\",\n      \"is_crew\": false,\n      \"role\": \"passenger\",\n\n\n\n      \"emergency_duty\": \"\",\n\n      \"preferred_language\": [ \"IE\" ],\n      \"in_position\": false,\n      \"assignment_status\": \"ASSIGNED\",\n      \"assigned_muster_station\": null\n}\n"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        TimeUnit.SECONDS.sleep(2);
        //add device
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addDevice/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"3\",\n    \"macAddress\": \"58:37:8B:DE:42:G9\",\n    \"imsi\": \"502130123456789\",\n    \"msisdn\": \"919825098250\",\n    \"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"b546b6242a23c2cd309560e7c50d7dcf35ddab902a1f4b223cf0e95b00bdcd3a\"\n  }"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        TimeUnit.SECONDS.sleep(2);
        //add location
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addLocation/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"macAddress\":\"58:37:8B:DE:42:G9\",\n\t\"hashedMacAddress\":\"b546b6242a23c2cd309560e7c50d7dcf35ddab902a1f4b223cf0e95b00bdcd3a\",\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"5\",\n\t\t\"gfName\":\"7BG1\",\n\t\t\"deck\":\"7\",\n\t\t\"macAddress\":\"58:37:8B:DE:42:G9\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1600807918\",\n\t\t\"hashedMacAddress\":\"b546b6242a23c2cd309560e7c50d7dcf35ddab902a1f4b223cf0e95b00bdcd3a\",\n\t\t\"timestamp\":\"1600807918\"\n\t\t\n\t},\n\t \"location\":{\n\t\t \"xLocation\":\"76.5425\",\n\t\t \"yLocation\":\"34.2354\",\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"deckA\",\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\",\n\t\t \"hashedMacAddress\": \"b546b6242a23c2cd309560e7c50d7dcf35ddab902a1f4b223cf0e95b00bdcd3a\",\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence2\"],\n\t\t \"timestamp\":\"1600807918\"\n\t }\n  }"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }


    @Test
    public void testAddCrew() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.body(), KeycloakAccessTokenResponse.class);

        //add Person
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addPerson/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString(" {\n   \"saturation\": \"99\",  \"heartBeat\": \"102\",  \"name\": \"NikosCrew\",\n      \"surname\": \"TestCrew\",\n      \"identifier\": \"2\",\n      \"gender\": \"Male\",\n      \"age\": \"1965-01-01\",\n      \"connectedPassengers\": [\n      ],\n      \"embarkation_port\": \"\",\n      \"disembarkation_port\": \"\",\n      \"ticketNumber\": \"\",\n      \"email\": \"triantafyllou.ni@gmail.com\",\n      \"postal_address\": \"Kallistratous 50\",\n      \"emergency_contact_details\": \"6943808730\",\n      \"country_of_residence\": \"GR\",\n      \"medical_condnitions\": \"\",\n      \"mobility_issues\": \"\",\n      \"pregnency_data\": \"\",\n      \"is_crew\": true,\n      \"role\": \"engineer\",\n      \"emergency_duty\": \"medical_unit\",\n      \"preferred_language\": [ \"IE\" ],\n      \"in_position\": false,\n      \"assignment_status\": \"UNASSIGNED\",\n      \"assigned_muster_station\": null\n}\n"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        //add device
        TimeUnit.SECONDS.sleep(2);
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addDevice/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n     \n    \"identifier\": \"2\",\n    \"macAddress\": \"58:37:8B:DE:42:F7\",\n    \"imsi\": \"502130123456789\",\n    \"msisdn\": \"919825098250\",\n    \"imei\": \"49-015420-323751-8\",\n\t\t\"messagingAppClientId\": \"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\"\n  }"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        //add location
        TimeUnit.SECONDS.sleep(2);
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/addLocation/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n    \"macAddress\":\"58:37:8B:DE:42:F7\",\n\t\"hashedMacAddress\":\"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\"geofence\":{\n\t\t\"gfEvent\":\"2132121\",\n\t\t\"gfId\":\"1\",\n\t\t\"gfName\":\"7BG1\",\n\t\t\"macAddress\":\"58:37:8B:DE:42:F8\",\n\t\t\"isAssociated\":\"false\",\n\t\t\"dwellTime\":\"1600807918\",\n\t\t\"hashedMacAddress\":\"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\t\"timestamp\":\"1600807918\",\n\t\t\"deck\":\"7\"\n\t},\n\t \"location\":{\n\t\t \"xLocation\":\"91.91315958190962\",\n\t\t \"yLocation\":\"27.497502709677637\",\n\t\t \"errorLevel\":\"0\",\n\t\t \"isAssociated\":\"false\",\n\t\t \"campusId\":\"7\",\n\t\t \"buildingId\":\"shipA\",\n\t\t \"floorId\":\"floor0\",\n\t\t \"hashedMacAddress\": \"b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759\",\n\t\t \"geofenceId\":\"1\",\n\t\t \"geofenceNames\":[\"geofence1\"],\n\t\t \"timestamp\":\"1600807918\"\n\t }\n  }"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }


    @Test
    public void testAddPassengerFullData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.body(), KeycloakAccessTokenResponse.class);

        //add Person
        PersonFullTO personFullTO = new PersonFullTO();
        personFullTO.setAge("21");
        personFullTO.setConnectedPassengers(new ArrayList<>());
        personFullTO.setCrew(false);
        personFullTO.setAssignmentStatus(Personalinfo.AssignmentStatus.ASSIGNED);
        personFullTO.setAssignedMusteringStation(null);
        personFullTO.setCountryOfResidence("GR");


        personFullTO.setEmail("test@test2.gr");
        personFullTO.setDisembarkationPort("Chania");
        personFullTO.setEmbarkationPort("Pireaus");
        personFullTO.setPrengencyData("none");
        personFullTO.setGender("Male");
        personFullTO.setDutySchedule(new ArrayList<>());
        personFullTO.setEmergencyDuty("");
        personFullTO.setIdentifier("el/el/123456789");
        personFullTO.setEmergencyContact("69438087xx");
        personFullTO.setInPosition(false);
        personFullTO.setName("Test3");
        personFullTO.setSurname("TestSurname3");
        personFullTO.setRole("passenger");

        personFullTO.setMedicalCondition("none");
        personFullTO.setPostalAddress("Address 1");
        personFullTO.setMobilityIssues("none");
        personFullTO.setTicketNumber("12345");
        personFullTO.setPreferredLanguage(new String[]{"en"});


        ArrayList<DeviceInfo> deviceInfos = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setImsi("502130123456789");
        deviceInfo.setImei("49-015420-323751-8");
        deviceInfo.setMsisdn("919825098250");
        deviceInfo.setMacAddress("58:37:8B:DE:42:F9");
        deviceInfo.setHashedMacAddress("18a4f641457adea15e4ff9f8d203802ba749714f87e22aedffd2a4dcb33b4f65");
        deviceInfos.add(deviceInfo);
        personFullTO.setDeviceInfoList(deviceInfos);
        personFullTO.setMessagingAppClientId("Nikos-Admin");
        personFullTO.setGeofenceHistory(new ArrayList<>());
        personFullTO.setLocationHistory(new ArrayList<>());

        String uri
                = "http://dss.aegean.gr:8090/addPerson2ES";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenResponse.getAccessToken();
        headers.set("Authorization", bearer);

        HttpEntity<PersonFullTO> req = new HttpEntity<>(personFullTO, headers);
        String resp = restTemplate.postForObject(uri, req, String.class);
        System.out.println(resp);

    }


    @Test
    public void testAddPCrewFullData() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.body(), KeycloakAccessTokenResponse.class);

        //add Person
        PersonFullTO personFullTO = new PersonFullTO();
        personFullTO.setAge("41");
        personFullTO.setConnectedPassengers(new ArrayList<>());
        personFullTO.setCrew(true);
        personFullTO.setAssignmentStatus(Personalinfo.AssignmentStatus.ASSIGNED);
        personFullTO.setAssignedMusteringStation(null);
        personFullTO.setCountryOfResidence("GR");


        personFullTO.setEmail("crew@test2.gr");
        personFullTO.setDisembarkationPort("Chania");
        personFullTO.setEmbarkationPort("Pireaus");
        personFullTO.setPrengencyData("none");
        personFullTO.setGender("Male");
        personFullTO.setDutySchedule(new ArrayList<>());
        personFullTO.setEmergencyDuty("fire-fighting team");
        personFullTO.setIdentifier("el/el/987654321");
        personFullTO.setEmergencyContact("69438087xx");
        personFullTO.setInPosition(true);
        personFullTO.setName("CrewTestName");
        personFullTO.setSurname("CrewTestSurname");
        personFullTO.setRole("crew");

        personFullTO.setMedicalCondition("none");
        personFullTO.setPostalAddress("Address 1");
        personFullTO.setMobilityIssues("none");
        personFullTO.setPreferredLanguage(new String[]{"en"});


        ArrayList<DeviceInfo> deviceInfos = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setImsi("502130123456789");
        deviceInfo.setImei("49-015420-323751-8");
        deviceInfo.setMsisdn("919825098250");
        deviceInfo.setMacAddress("58:37:8B:DE:42:F9");
        deviceInfo.setHashedMacAddress("18a4f641457adea15e4ff9f8d203802ba749714f87e22aedffd2a4dcb33b4f66");
        deviceInfos.add(deviceInfo);
        personFullTO.setDeviceInfoList(deviceInfos);
        personFullTO.setMessagingAppClientId("Tactilon-ID-1");
        personFullTO.setGeofenceHistory(new ArrayList<>());
        personFullTO.setLocationHistory(new ArrayList<>());

        String uri
                = "http://dss.aegean.gr:8090/addPerson2ES";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenResponse.getAccessToken();
        headers.set("Authorization", bearer);

        HttpEntity<PersonFullTO> req = new HttpEntity<>(personFullTO, headers);
        String resp = restTemplate.postForObject(uri, req, String.class);
        System.out.println(resp);

    }












    @Test
    public void setEvacuationStatus() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.body(), KeycloakAccessTokenResponse.class);

        //set Evacuation Status
        request = HttpRequest.newBuilder()
                .uri(URI.create("http://dss.aegean.gr:8090/setEvacuationStatus/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"status\": \"6.3\"\n \n}"))
                .build();
        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }


    @Test
    public void testCallingDBProxyLocation(){

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dss1.aegean.gr/auth/realms/palaemon/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("client_id=palaemonRegistration&client_secret=bdbbb8d5-3ee7-4907-b95c-2baae17bd10f&grant_type=client_credentials&scope=openid"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            KeycloakAccessTokenResponse accessTokenResponse = mapper.readValue(response.body(), KeycloakAccessTokenResponse.class);

            String DBPROXY_URL = "http://dss.aegean.gr:8090";
            ObjectMapper ow = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            String json = ow.writeValueAsString("{}");
            request = HttpRequest.newBuilder()
                    .uri(URI.create(DBPROXY_URL + "/addLocation"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
