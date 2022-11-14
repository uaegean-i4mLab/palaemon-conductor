package gr.aegean.palaemon.conductor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.BlockedGeofenceTO;
import gr.aegean.palaemon.conductor.model.TO.SrapTO;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.ElasticService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.CryptoUtils;
import gr.aegean.palaemon.conductor.utils.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class TestingControllers {

    @Autowired
    KafkaService kafkaService;

    @Autowired
    ElasticService elasticService;

    @Autowired
    CryptoUtils cryptoUtils;

    @Autowired
    DBProxyService dbProxyService;


    @GetMapping("addTestCrewMembersOnD7")
    public @ResponseBody String addTestCrewMembersOnD7() {
        try {
            //Crew member on 7BG6
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "Richie",
                    "Gilmore", "c4", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C3",
                    "502130123456789", "919825098250", "306943808730", "SB00015", "7", "1665427687",
                    "1", "event", "1231", "7BG6", "true", "7", "25.80",
                    "80.50", "1", "0", List.of("7BG6"));

            //Crew member 7DG4
            TestingUtils.addTestPerson("medical_unit", "99", "102", "Nikos",
                    "Triantafyllou", "c1", "male", "40", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test12@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "7DG4", "58:37:8B:DE:42:C0",
                    "502130123456789", "919825098250", "306943808730", "SB00012", "7", "1665427687",
                    "1", "event", "1231", "7DG4", "true", "7", "26.80",
                    "93.50", "1", "0", List.of("7DG4"));

            // ADD a crew member on 7CG1 corridor
            TestingUtils.addTestPerson("firefighting_unit", "99", "102", "Jack",
                    "Black", "c2", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C1",
                    "502130123456789", "919825098250", "306943808731", "SB00013", "9", "1665427687",
                    "1", "event", "1231", "7CG1", "true", "9", "22.80",
                    "120.50", "1", "0", List.of("7CG1"));

            //Crew member on 7BG6
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "Richie",
                    "Gilmore", "c3", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C2",
                    "502130123456789", "919825098250", "306943808734", "SB00014", "7", "1665427687",
                    "1", "event", "1231", "7BG6", "true", "7", "30.80",
                    "60.50", "1", "0", List.of("7BG6"));


            //Crew member on 7BG6
            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "Richie",
                    "Gilmore", "c5", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C5",
                    "502130123456789", "919825098250", "306943808738", "SB00015", "7", "1665427687",
                    "1", "event", "1231", "7BG6", "true", "7", "25.80",
                    "30.50", "1", "0", List.of("7BG6"));

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }


    @GetMapping("addPassengerOnDeck7Mobility")
    public @ResponseBody String addPassengerOnDeck7Mobility() {
        try {
            // Passenger on D7
            TestingUtils.addTestPerson("", "99", "102", "Passenger10",
                    "Surname10", "10", "male", "42", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
                    "GR", "", "assisted_gait", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:M2",
                    "502130123456789", "919825098250", "Mumla_User", "SB0001", "7", "1665427687",
                    "1", "event", "1231", "7DG3", "true", "7", "24.80",
                    "85.50", "1", "0", List.of("7DG3"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }


    /*
        Adds a test crew member on 7DG4, 9BG4
        passengers on : 7DG3, 7DG2 (health issues), 9BG1
     */
    @GetMapping("addTestDataD7andD9")
    public @ResponseBody String addTestDataD7andD9() {
        try {
//            //Crew member 7DG4
//            TestingUtils.addTestPerson("medical_unit", "99", "102", "Nikos",
//                    "Triantafyllou", "c1", "male", "40", new ArrayList<>(), "PIRAEUS",
//                    "CHANIA", "456", "test12@test.gr", "Address 3", "306943808730",
//                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
//                    new String[]{"EN"}, "crew", "7DG4", "58:37:8B:DE:42:B0",
//                    "502130123456789", "919825098250", "306943808730", "SB00012", "7", "1665427687",
//                    "1", "event", "1231", "7DG4", "true", "7", "26.80",
//                    "93.50", "1", "0", List.of("7DG4"));
//
//            // ADD a crew member on 9BG4 corridor
//            TestingUtils.addTestPerson("firefighting_unit", "99", "102", "Jack",
//                    "Black", "D9_13", "male", "35", new ArrayList<>(), "PIRAEUS",
//                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
//                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
//                    new String[]{"EN"}, "crew", null, "58:37:8B:DE:42:B1",
//                    "502130123456789", "919825098250", "306943808730", "SB00013", "9", "1665427687",
//                    "1", "event", "1231", "9BG4", "true", "9", "26.80",
//                    "90.50", "1", "0", List.of("9BG4"));
//
//            //Crew member on 7BG6
//            TestingUtils.addTestPerson("passenger_mustering_unit", "99", "102", "Richie",
//                    "Gilmore", "D9_13", "male", "35", new ArrayList<>(), "PIRAEUS",
//                    "CHANIA", "456", "test13@test.gr", "Address 3", "306943808730",
//                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
//                    new String[]{"EN"}, "crew", null, "58:37:8B:DE:42:B1",
//                    "502130123456789", "919825098250", "306943808730", "SB00013", "7", "1665427687",
//                    "1", "event", "1231", "7BG6", "true", "7", "26.80",
//                    "90.50", "1", "0", List.of("7BG6"));


            // Passenger on D7
            TestingUtils.addTestPerson("", "99", "102", "Passenger1",
                    "Surname1", "1", "male", "42", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
                    "GR", "assisted_gait", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:B2",
                    "502130123456789", "919825098250", "Mumla_User", "SB0001", "7", "1665427687",
                    "1", "event", "1231", "7DG3", "true", "7", "24.80",
                    "85.50", "1", "0", List.of("7DG3"));
//
//            //Passenger Health issue D7
//            TestingUtils.addTestPerson("", "99", "102", "Passenger2",
//                    "Surname2", "2", "male", "28", new ArrayList<>(), "PIRAEUS",
//                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
//                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:B3",
//                    "502130123456789", "919825098250", "kat", "SB0002", "7", "1665427687",
//                    "1", "event", "1231", "7DG2", "true", "7", "24.80",
//                    "85.50", "1", "0", List.of("7DG2"));
//
//            //Passenger Health issue D9 (assisted_gait)
//            TestingUtils.addTestPerson("", "99", "102", "Passenger3",
//                    "Surname3", "3", "male", "54", new ArrayList<>(), "PIRAEUS",
//                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
//                    "GR", "", "assisted_gait", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:B4",
//                    "502130123456789", "919825098250", "Plumble_User", "SB0002", "9", "1665427687",
//                    "1", "event", "1231", "9BG1", "true", "9", "24.80",
//                    "85.50", "1", "0", List.of("9BG1"));
//
//            //Passenger no Health issue D7
//            TestingUtils.addTestPerson("", "99", "102", "Passenger4",
//                    "Surname4", "4", "male", "54", new ArrayList<>(), "PIRAEUS",
//                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
//                    "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                    new String[]{"EN"}, "passenger", null, "58:37:8B:DE:42:P4",
//                    "502130123456789", "919825098250", "Mlumble_User", "SB0015", "7", "1665427687",
//                    "1", "event", "1231", "7BG1", "true", "7", "24.80",
//                    "85.50", "1", "0", List.of("7BG1"));


        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }

    @GetMapping("addSitumTestUser")
    public @ResponseBody String addSitumTestUSER() {
        try {
            // Passenger on D7
            TestingUtils.addTestPerson("", "99", "102", "SitumUser1",
                    "SitumUser1", "a101", "male", "42", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "456", "test14@test.gr", "Address 3", "306943808730",
                    "GR", "assisted_gait", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "passenger", null, "684560580327",
                    "502130123456789", "919825098250", "Mumla_User", "SB0007", "7", "1665427687",
                    "1", "event", "1231", "7DG3", "true", "7", "24.80",
                    "85.50", "1", "0", List.of("7DG3"));
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


    @GetMapping("updateMessageClientIdsAndMusterStations")
    public @ResponseBody String updateMessageClientId() {
        //Update a healthy and a person with mobility issues
        // healthy Mumble_User;
        // mobility Plumble_User;

        try {

            List<PameasPerson> allPassengersDecrypted = this.elasticService.getAllPassengersDecrypted();

            Optional<PameasPerson> toUpdateMobility = allPassengersDecrypted.stream().filter(pameasPerson -> {
                return pameasPerson.getNetworkInfo() != null && !pameasPerson.getPersonalInfo().isCrew()
                        && pameasPerson.getPersonalInfo() != null && !StringUtils.isEmpty(pameasPerson.getPersonalInfo().getMobilityIssues());
            }).findAny();

            if (toUpdateMobility.isEmpty()) {
                Optional<PameasPerson> toFullyUpdate = allPassengersDecrypted.stream().findFirst();
                toFullyUpdate.get().getPersonalInfo().setMobilityIssues("assisted_gait");
                String idDecrypted = toFullyUpdate.get().getPersonalInfo().getPersonalId();
                try {
                    toFullyUpdate.get().getNetworkInfo().setMessagingAppClientId("Mumla_User");
                    toFullyUpdate.get().getNetworkInfo().setBraceletId("SB0001");
                    toFullyUpdate.get().getPersonalInfo().setName(this.cryptoUtils.encryptBase64(toFullyUpdate.get().getPersonalInfo().getName()));
                    toFullyUpdate.get().getPersonalInfo().setSurname(this.cryptoUtils.encryptBase64(toFullyUpdate.get().getPersonalInfo().getSurname()));
                    toFullyUpdate.get().getPersonalInfo().setPersonalId(this.cryptoUtils.encryptBase64(toFullyUpdate.get().getPersonalInfo().getPersonalId()));
                    this.elasticService.updatePerson(idDecrypted, toFullyUpdate.get());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }


            Optional<PameasPerson> toUpdateHealthy = allPassengersDecrypted.stream().filter(pameasPerson -> {
                        return pameasPerson.getNetworkInfo() != null && !pameasPerson.getPersonalInfo().isCrew()
                                && pameasPerson.getPersonalInfo() != null && StringUtils.isEmpty(pameasPerson.getPersonalInfo().getMobilityIssues())
                                && StringUtils.isEmpty(pameasPerson.getPersonalInfo().getPrengencyData()) &&
                                StringUtils.isEmpty(pameasPerson.getPersonalInfo().getMedicalCondition());
                    })
                    .findAny();
            toUpdateHealthy.ifPresent(pameasPerson -> {
                pameasPerson.getNetworkInfo().setMessagingAppClientId("Plumble_User");
                try {
                    String idDecrypted = pameasPerson.getPersonalInfo().getPersonalId();
                    pameasPerson.getPersonalInfo().setName(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getName()));
                    pameasPerson.getPersonalInfo().setSurname(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getSurname()));
                    pameasPerson.getPersonalInfo().setPersonalId(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getPersonalId()));
                    this.elasticService.updatePerson(idDecrypted, pameasPerson);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });

//            List<PameasPerson> toUpdateMS = allPassengersDecrypted.stream();
//                    .filter(pameasPerson -> {
//                return StringUtils.isEmpty(pameasPerson.getPersonalInfo().getAssignedMusteringStation());
//            }).collect(Collectors.toList());

            List<PameasPerson> toAssign7dg4 = allPassengersDecrypted.stream().limit(allPassengersDecrypted.size() / 5).collect(Collectors.toList());
            toAssign7dg4.forEach(pameasPerson -> {
                pameasPerson.getPersonalInfo().setAssignedMusteringStation("7DG4");
                try {
                    String idDecrypted = pameasPerson.getPersonalInfo().getPersonalId();
                    pameasPerson.getPersonalInfo().setName(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getName()));
                    pameasPerson.getPersonalInfo().setSurname(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getSurname()));
                    pameasPerson.getPersonalInfo().setPersonalId(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getPersonalId()));
                    this.elasticService.updatePerson(idDecrypted, pameasPerson);

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });

            allPassengersDecrypted.removeAll(toAssign7dg4);
            allPassengersDecrypted.forEach(pameasPerson -> {
                pameasPerson.getPersonalInfo().setAssignedMusteringStation("7DG4");
                try {
                    String idDecrypted = pameasPerson.getPersonalInfo().getPersonalId();
                    pameasPerson.getPersonalInfo().setName(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getName()));
                    pameasPerson.getPersonalInfo().setSurname(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getSurname()));
                    pameasPerson.getPersonalInfo().setPersonalId(this.cryptoUtils.encryptBase64(pameasPerson.getPersonalInfo().getPersonalId()));
                    this.elasticService.updatePerson(idDecrypted, pameasPerson);

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });


        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "OK";
    }


}
