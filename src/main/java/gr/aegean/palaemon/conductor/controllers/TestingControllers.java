package gr.aegean.palaemon.conductor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.BlockedGeofenceTO;
import gr.aegean.palaemon.conductor.model.TO.SrapTO;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class TestingControllers {

    @Autowired
    KafkaService kafkaService;

    @Autowired
    ElasticService elasticService;

    @Autowired
    CryptoUtils cryptoUtils;
    /*
        Adds a test crew member on 7DG4, 9BG4
        passengers on : 7DG3, 7DG2 (health issues), 9BG1
     */
    @GetMapping("addTestDataD7andD9")
    public @ResponseBody String addTestDataD7andD9() {
        try {
            //7DG4
            TestingUtils.addTestPerson("medical_unit", "99", "102", "D9_test12",
                    "D9_test12_sur", "c1", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test12@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "7DG4", "58:37:8B:DE:42:B0",
                    "502130123456789", "919825098250", "306943808730", "SB00012", "7", "1665427687",
                    "1", "event", "1231", "7DG4", "true", "7", "26.80",
                    "93.50", "1", "0", List.of("7DG4"));
            // ADD a crew member on 9BG4 corridor
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "D9_test13",
                    "D9_test12_sur", "D9_13", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", null, "58:37:8B:DE:42:B1",
                    "502130123456789", "919825098250", "306943808730", "SB00013", "9", "1665427687",
                    "1", "event", "1231", "9BG4", "true", "9", "26.80",
                    "90.50", "1", "0", List.of("9BG4"));


            // Passenger on D7
            TestingUtils.addTestPerson("first_response_unit", "99", "102", "1",
                    "1", "1", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:B2",
                    "502130123456789", "919825098250", "Mumla_User", "SB0001", "7", "1665427687",
                    "1", "event", "1231", "7DG3", "true", "7", "24.80",
                    "85.50", "1", "0", List.of("7DG3"));

            //Passenger Health issue D7 (equip_required)
            TestingUtils.addTestPerson("first_response_unit", "99", "102", "2",
                    "2", "2", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
                    "GR", "equip_required", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:B3",
                    "502130123456789", "919825098250", "Mumla_User", "SB0002", "7", "1665427687",
                    "1", "event", "1231", "7DG2", "true", "7", "24.80",
                    "85.50", "1", "0", List.of("7DG2"));

            //Passenger Health issue D9
            TestingUtils.addTestPerson("first_response_unit", "99", "102", "3",
                    "3", "3", "male", "1950-01-01", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:B4",
                    "502130123456789", "919825098250", "Mumla_User", "SB0002", "9", "1665427687",
                    "1", "event", "1231", "9BG1", "true", "9", "24.80",
                    "85.50", "1", "0", List.of("9BG1"));


        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }



    @PostMapping("/srap-block-mvz")
    public @ResponseBody String srapBlockedGeofence(@RequestBody BlockedGeofenceTO blockedGeofenceTO) {
        SrapTO srapTO = new SrapTO();
        srapTO.setStatus("closed");
        srapTO.setZoneId(blockedGeofenceTO.getGeofence());
        this.kafkaService.writeToSRAP(srapTO);

        return "will block zone:" + blockedGeofenceTO.getGeofence();
    }

    @PostMapping("/block-geofence")
    public @ResponseBody String blockGeofence(@RequestBody BlockedGeofenceTO blockedGeofenceTO) {
        String conductorUrl = System.getenv("CONDUCTOR_URI");
        ObjectMapper mapper = new ObjectMapper();
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(conductorUrl + "workflow/detect_blocked_geofence?priority=0"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(blockedGeofenceTO)))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("made a call to {}detect_blocked_geofence", conductorUrl);
            log.info("response {}", response.body());
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
        }
        return "OK";
    }


    @GetMapping("updateMessageClientId")
    public @ResponseBody String updateMessageClientId() {

        this.elasticService.getAllPassengersDecrypted().stream().filter(pameasPerson -> {
            return pameasPerson.getNetworkInfo()!= null;
        }).peek(pameasPerson -> pameasPerson.getNetworkInfo().setMessagingAppClientId("Mumble_Client")).forEach(pameasPerson ->{

                    try {
                        this.elasticService.updatePerson(cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getPersonalId()),pameasPerson);
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                             IllegalBlockSizeException | BadPaddingException e) {
                        log.error(e.getMessage());
                    }
                }
                );


        return "OK";
    }


}
