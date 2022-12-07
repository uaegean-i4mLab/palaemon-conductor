package gr.aegean.palaemon.conductor.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.AddDevicePersonTO;
import gr.aegean.palaemon.conductor.model.TO.ConnectedPersonTO;
import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.TO.PersonTO;
import gr.aegean.palaemon.conductor.model.pojo.KeycloakAccessTokenResponse;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public class TestingUtils {


    private final static String OAUTH_URI = System.getenv("OAUTH_URI");
    private final static String CLIENT_ID = System.getenv("CLIENT_ID");
    private final static String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private final static String DB_PROXY_URI = System.getenv("DB_PROXY_URI"); // http://dss.aegean.gr:8090/




    public static void addTestPerson(String emergencyDuty, String saturation, String heartBeat, String name, String surname, String identifier,
                               String gender, String age, ArrayList<ConnectedPersonTO> connectedPassengers, String embarkation,
                               String disembarkation, String ticketNumber, String email, String postalAddress,
                               String emergencyContact, String countryOfResidence, String medicalCondition,
                               String mobilityIssues, String pregnencyData, boolean isCrew, Personalinfo.AssignmentStatus assigmentStatus,
                               String[] prefLanguage, String role, String musterStation,
                               String macAddress, String imsi, String imei,
                               String messagingAppClientId, String braceletId,
                               String deck, String timestamp, String gfId,
                               String gfEvent, String dwellTime, String gfName, String isAsosciated,
                               String floorId, String yLocation, String xLocation, String campusId,
                               String errorLevel, List<String> geofenceNames) {
        try {
            KeycloakAccessTokenResponse accessTokenResponse = getOAuthAccessToken();
            ObjectMapper mapper = new ObjectMapper();

            //add Person
            PersonTO p = PameasPersonUtils.buildPersonTO(saturation, heartBeat, name,
                    surname, identifier, gender, age, connectedPassengers, embarkation,
                    disembarkation, ticketNumber, email, postalAddress, emergencyContact,
                    countryOfResidence, medicalCondition, mobilityIssues, pregnencyData, isCrew, assigmentStatus,
                    prefLanguage, role, musterStation, emergencyDuty);

            HttpRequest request = null;
            if (accessTokenResponse != null) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create(DB_PROXY_URI + "addPerson/"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(p)))
                        .build();
            } else {
                log.error("accessTokenResponse form Keycloak null");
            }
            HttpResponse<String> response;
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            //add device
            AddDevicePersonTO devicePersonTO = PameasPersonUtils.addDevicePersonTO(identifier, macAddress,
                    imsi, imei, messagingAppClientId, braceletId);
//            if(devicePersonTO.getMacAddress().equals("684560580327")){
//                log.info("ADDING A SITUM account to a test crew");
//                devicePersonTO.setTicketNumber(p.getTicketNumber());
//            }


            TimeUnit.SECONDS.sleep(2);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(DB_PROXY_URI + "addDevice/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(devicePersonTO)))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            //add location
            LocationTO location = PameasPersonUtils.addLocationTO(deck, timestamp, macAddress,
                    gfId, gfEvent, dwellTime, gfName, isAsosciated, floorId, yLocation,
                    xLocation, campusId, errorLevel, geofenceNames);
            request = HttpRequest.newBuilder()
                    // .uri(URI.create(this.DB_PROXY_URI + "addLocation/"))
                    .uri(URI.create(  DB_PROXY_URI + "addLocation/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(location)))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            //add second location to generate speed mock value
             location = PameasPersonUtils.addLocationTO(deck, timestamp, macAddress,
                    gfId, gfEvent, dwellTime, gfName, isAsosciated, floorId, yLocation,
                    xLocation, campusId, errorLevel, geofenceNames);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(  DB_PROXY_URI + "addLocation/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken())
                    .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(location)))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public static KeycloakAccessTokenResponse getOAuthAccessToken() {
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
