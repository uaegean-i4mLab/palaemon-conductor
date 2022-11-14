package gr.aegean.palaemon.conductor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.MockPassengersRequestTO;
import gr.aegean.palaemon.conductor.model.TO.SimRequestTO;
import gr.aegean.palaemon.conductor.model.pojo.KeycloakAccessTokenResponse;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.ElasticService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.CryptoUtils;
import gr.aegean.palaemon.conductor.utils.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

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
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
public class TRL4TestingControllers {
    private final String OAUTH_URI = System.getenv("OAUTH_URI");
    private final String CLIENT_ID = System.getenv("CLIENT_ID");
    private final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");
    private final String DB_PROXY_URI = System.getenv("DB_PROXY_URI"); // http://dss.aegean.gr:8090/

    @Autowired
    KafkaService kafkaService;
    @Autowired
    ElasticService elasticService;
    @Autowired
    CryptoUtils cryptoUtils;
    @Autowired
    DBProxyService dbProxyService;
    @Autowired
    RestTemplate restTemplate;


    @GetMapping("/trl4/initGeo")
    public @ResponseBody String initGeofences() {
        return this.bulidGeofences();
    }


    @GetMapping("/trl4/addCrew")
    public @ResponseBody String addTestCrewMembersOnD7() {
        //adds 5 crew members the first one with Situm DeviceID
        return addTestCrewMembers();
    }


    @PostMapping("/trl4/mockPassengers")
    public @ResponseBody String mockPassengers(@RequestBody MockPassengersRequestTO[] mockPassengersRequests)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        //adds the requested passengers and includes mobility issues to 3 random passengers
        return this.addPassengers(mockPassengersRequests);
    }

    @PostMapping("/trl4/buildAll")
    public @ResponseBody String buildAll(@RequestBody MockPassengersRequestTO[] mockPassengersRequests) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.bulidGeofences();
        this.addTestCrewMembers();
        this.addPassengers(mockPassengersRequests);
        return "OK";
    }













    private String addTestCrewMembers(){
        try {
            //Crew member on 7BG6 with SitumId, Situm DeviceIds as Mac and MessagingId
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "Richie",
                    "Gilmore", "c4", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "684560580327",
                    "502130123456789", "919825098250", "306943808730", "SB0001", "7", "1665427687",
                    "1", "event", "1231", "7BG6", "true", "7", "25.80",
                    "80.50", "1", "0", List.of("7BG6"));

            //Crew member 7DG4
            TestingUtils.addTestPerson("medical_unit", "99", "102", "Nikos",
                    "Triantafyllou", "c1", "male", "40", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test12@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "7DG4", "28:37:8B:DE:42:C2",
                    "502130123456789", "919825098250", "306943808730", "SB00012", "7", "1665427687",
                    "1", "event", "1231", "7DG4", "true", "7", "26.80",
                    "93.50", "1", "0", List.of("7DG4"));

            // ADD a crew member on 7CG1 corridor
            TestingUtils.addTestPerson("firefighting_unit", "99", "102", "Jack",
                    "Black", "c2", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "28:37:8B:DE:42:C3",
                    "502130123456789", "919825098250", "306943808731", "SB00013", "9", "1665427687",
                    "1", "event", "1231", "7CG1", "true", "9", "22.80",
                    "120.50", "1", "0", List.of("7CG1"));

            //Crew member on 7BG6
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "Richie",
                    "Gilmore", "c3", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "28:37:8B:DE:42:C4",
                    "502130123456789", "919825098250", "306943808734", "SB00014", "7", "1665427687",
                    "1", "event", "1231", "7BG6", "true", "7", "30.80",
                    "60.50", "1", "0", List.of("7BG6"));


            //Crew member on 7BG6
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "Richie",
                    "Gilmore", "c5", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "28:37:8B:DE:42:C5",
                    "502130123456789", "919825098250", "306943808738", "SB00015", "7", "1665427687",
                    "1", "event", "1231", "7BG6", "true", "7", "25.80",
                    "30.50", "1", "0", List.of("7BG6"));

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }

    private String addPassengers(MockPassengersRequestTO[] mockPassengersRequests) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String SIM1_URI = "http://pameasrtlsdb.aegean.gr:7013/runEmulation";
        String SIM2_URI = "http://pameasrtlsdb.aegean.gr:7012/runEmulation";
        String SIM3_URI = "http://pameasrtlsdb.aegean.gr:7011/runEmulation";

        String[] simulatorURIs = {SIM1_URI, SIM2_URI, SIM3_URI};
        ArrayList<String> messagingIds = new ArrayList<String>(
                Arrays.asList("Mumla_User", "Plumble_User", "test"));


        HttpRequest request = null;

        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < mockPassengersRequests.length; i++) {
            SimRequestTO simRequestTO = new SimRequestTO();
            simRequestTO.setDeck(mockPassengersRequests[i].getDeck());
            simRequestTO.setNoOfData(mockPassengersRequests[i].getNumberOfPassengers());
            simRequestTO.setHeartProblemPrnctg(mockPassengersRequests[i].getHeartIssuesPercent());
            simRequestTO.setPositionError("0.5");
            simRequestTO.setOxygenProblemPrnctg(mockPassengersRequests[i].getOxygenIssuesPercent());
            try {
                request = HttpRequest.newBuilder()
                        .uri(URI.create(simulatorURIs[i % 3]))
                        .header("Content-Type", "application/json")
                        .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(simRequestTO)))
                        .build();
                HttpResponse<String> response;
                response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage());
                return "ERROR";
            }
        }

        CryptoUtils cryptoUtils = new CryptoUtils();
        //add walking issues
        this.elasticService.getAllPassengersDecrypted().stream().limit(3).forEach(pameasPerson -> {
            log.info(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());
            pameasPerson.getPersonalInfo().setMobilityIssues("unable_to_walk");
            // add messagingIds to passengers
            pameasPerson.getNetworkInfo().setMessagingAppClientId(messagingIds.get(0));
            messagingIds.remove(0);


            String personalIdDecrypted = pameasPerson.getPersonalInfo().getPersonalId();
            try {
                pameasPerson.getPersonalInfo().setPersonalId(cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getPersonalId()));
                pameasPerson.getPersonalInfo().setName(cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getName()));
                pameasPerson.getPersonalInfo().setSurname(cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getSurname()));
                this.elasticService.updatePerson(personalIdDecrypted, pameasPerson);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                log.error(e.getMessage());
            }

        });
        return "OK";
    }


    private String bulidGeofences(){
        List<String> geofenceName = Arrays.asList("9G1", "S9-8.1", "S8-7.1", "S7-6.1", "8G1",
                "8G2", "8G3", "8G4", "8G5", "8G6", "8G7", "8G8", "8G9", "8G10", "7BG4", "7BG5",
                "7DG1", "7DG2", "7DG3", "7DG4", "7DG3", "7CG1", "7CG2", "7BG1", "7BG2", "7BG3", "7BG5", "7BG6",
                "9BG1", "9BG2", "9CG0", "9BG4", "9BG3", "9BG1+");
        try {
            KeycloakAccessTokenResponse accessTokenResponse = TestingUtils.getOAuthAccessToken();
            geofenceName.forEach(geoName -> {
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



}
