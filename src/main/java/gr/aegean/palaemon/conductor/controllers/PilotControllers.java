package gr.aegean.palaemon.conductor.controllers;

import gr.aegean.palaemon.conductor.model.RestGenericResponse;
import gr.aegean.palaemon.conductor.model.TO.AlertPaxInGeofenceTO;
import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.ElasticService;
import gr.aegean.palaemon.conductor.service.PassengerMessagingService;
import gr.aegean.palaemon.conductor.utils.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class PilotControllers {


    @Autowired
    ElasticService elasticService;

    @Autowired
    DBProxyService dbProxyService;


    @Autowired
    PassengerMessagingService passengerMessagingService;


    private final String DB_PROXY_URI = System.getenv("DB_PROXY_URI");

    @GetMapping("/pilot/makePax")
    public @ResponseBody String movePersonFromMSWithTicket123() {
//

        TestingUtils.addTestPerson("", "99", "102", "Georgios",
                "Spathias", "pax1", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862050", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P1",
                "502130123456789", "919825098250", "", "SB0001", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "13.500000953674316",
                "82.49999237060547", "1", "0", List.of("9BG2"));
//
        TestingUtils.addTestPerson("", "99", "102", "Aggeliki",
                "Stouraiti", "pax2", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862051", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P2",
                "502130123456789", "919825098250", "", "SB000P2", "9", "1665427687",
                "1", "event", "1231", "9BG4", "true", "9", "14.919761657714844",
                "101.85546112060547", "1", "0", List.of("9BG4"));

        TestingUtils.addTestPerson("", "99", "102", "Charis",
                "Oikonomidou", "pax3", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862052", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "complicated", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P3",
                "502130123456789", "919825098250", "", "SB000P2", "9", "1665427687",
                "1", "event", "1231", "9C2YY", "true", "9", "4.50614070892334",
                "45.40822219848633", "1", "0", List.of("9C2YY"));
////
        TestingUtils.addTestPerson("", "99", "102", "Panagiotis",
                "Siokouros", "pax4", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862053", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P4",
                "502130123456789", "919825098250", "", "SB000P2", "9", "1665427687",
                "1", "event", "1231", "9C223", "true", "9", "15.80",
                "98.50", "1", "0", List.of("9C223"));
//
        TestingUtils.addTestPerson("", "99", "102", "Eirini",
                "Stamatopoulou", "pax5", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862054", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P5",
                "502130123456789", "919825098250", "", "SB000P5", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "31.80",
                "90.50", "1", "0", List.of("9BG1"));
//
//
//        // Second group 9C223
//
//
//        TestingUtils.addTestPerson("", "99", "102", "Aimilios",
//                "Kleftogiannis", "pax6", "max", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862055", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P6",
//                "502130123456789", "919825098250", "", "SB000P5", "9", "1665427687",
//                "1", "event", "1231", "9C223", "true", "9", "15.80",
//                "98.50", "1", "0", List.of("9C223"));
//
//        // Third Group 9C2YY
//
//        TestingUtils.addTestPerson("", "99", "102", "Dimitrios",
//                "Koutoukis", "pax7", "max", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862056", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P7",
//                "502130123456789", "919825098250", "", "SB000P7", "9", "1665427687",
//                "1", "event", "1231", "9C2YY", "true", "9", "40.80",
//                "87.50", "1", "0", List.of("9C2YY"));
//
//        TestingUtils.addTestPerson("", "99", "102", "Anastasios",
//                "Liveris", "pax8", "max", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862057", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P8",
//                "502130123456789", "919825098250", "", "SB000P8", "9", "1665427687",
//                "1", "event", "1231", "9C2YY", "true", "9", "15.80",
//                "80.50", "1", "0", List.of("9C2YY"));
//        // Fourth Group
//
//
//        TestingUtils.addTestPerson("", "99", "102", "Georgia",
//                "Makrodima", "pax9", "female", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862058", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P9",
//                "502130123456789", "919825098250", "", "SB000P8", "9", "1665427687",
//                "1", "event", "1231", "9BG2", "true", "9", "15.80",
//                "85.50", "1", "0", List.of("9BG2"));
//
//        TestingUtils.addTestPerson("", "99", "102", "Evaggelos",
//                "Maniatis", "pax10", "male", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862059", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P10",
//                "502130123456789", "919825098250", "", "SB000P10", "9", "1665427687",
//                "1", "event", "1231", "9BG2", "true", "9", "15.80",
//                "91.50", "1", "0", List.of("9BG2"));
//
//        TestingUtils.addTestPerson("", "99", "102", "Konstantinos",
//                "Moularas", "pax11", "male", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862060", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P11",
//                "502130123456789", "919825098250", "", "SB000P10", "9", "1665427687",
//                "1", "event", "1231", "9BG2", "true", "9", "15.80",
//                "88.50", "1", "0", List.of("9BG2"));
//
//        TestingUtils.addTestPerson("", "99", "102", "Konstantinos",
//                "Pantazis", "pax12", "male", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862061", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P12",
//                "502130123456789", "919825098250", "", "SB000P11", "9", "1665427687",
//                "1", "event", "1231", "9BG2", "true", "9", "15.80",
//                "78.50", "1", "0", List.of("9BG2"));
//
//        TestingUtils.addTestPerson("", "99", "102", "Virginia",
//                "Sklavounou", "pax13", "female", "20", new ArrayList<>(), "PIRAEUS",
//                "CHANIA", "A862062", "gelly.st@hotmail.com", "Address 3", "306943808730",
//                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
//                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P13",
//                "502130123456789", "919825098250", "", "SB000P11", "9", "1665427687",
//                "1", "event", "1231", "9BG2", "true", "9", "15.80",
//                "105.50", "1", "0", List.of("9BG2"));


        return "ok";
    }


    @GetMapping("/pilot/geo")
    public @ResponseBody String initGeofences() {
        List<String> geofenceName = Arrays.asList("9BG1",
                "9BG2", "9BG3", "9BG4", "9BG1+",
                //  *************
                // MUSTER STATION
                "9CG0",
                "9C223", "9C2YY"
        );
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


    @GetMapping("/pilot/crew")
    public @ResponseBody String addTestCrewMembersOnD7() {
        try {
            TestingUtils.addTestPerson("command_team", "99", "102", "Manolis",
                    "Sofianopoylos", "c1", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "306970000005", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "9CG0", "58:37:8B:DE:42:C1",
                    "502130123456789", "919825098250", "306970000005", "SB00015", "7", "1665427687",
                    "1", "event", "1231", "9BG4", "true", "7", "14.919761657714844",
                    "101.85546112060547", "1", "0", List.of("9BG4"));

            TestingUtils.addTestPerson("medical_unit", "99", "102", "Evangelos",
                    "Sfakianakis", "c2", "male", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "306940000004", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C2",
                    "502130123456789", "919825098250", "306940000004", "SB00015", "7", "1665427687",
                    "1", "event", "1231", "9CG0", "true", "7", "25.80",
                    "80.50", "1", "0", List.of("9CG0"));
//
            TestingUtils.addTestPerson("passenger_assistance_units", "99", "102", "Alexandros",
                    "Koimtzoglou", "c3", "female", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "306970000003", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C3",
                    "502130123456789", "919825098250", "306970000003", "SB00013", "7", "1665427687",
                    "1", "event", "1231", "9BG2", "true", "7", "25.80",
                    "80.50", "1", "0", List.of("9BG2"));
//
            TestingUtils.addTestPerson("command_team", "99", "102", "Marios",
                    "Koimtzoglou", "c4", "female", "35", new ArrayList<>(), "PIRAEUS",
                    "CHANIA", "306970000002", "test13@test.gr", "Address 3", "306943808730",
                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C4",
                    "502130123456789", "919825098250", "306970000002", "SB00015", "7", "1665427687",
                    "1", "event", "1231", "9BG2", "true", "7", "25.80",
                    "80.50", "1", "0", List.of("9BG2"));
//
//            TestingUtils.addTestPerson("command_team", "99", "102", "Nikos",
//                    "Triantafyllou", "c5", "male", "35", new ArrayList<>(), "PIRAEUS",
//                    "CHANIA", "C5", "test13@test.gr", "Address 3", "306943808730",
//                    "GR", "", "", "", true, Personalinfo.AssignmentStatus.UNASSIGNED,
//                    new String[]{"EN"}, "crew", "", "58:37:8B:DE:42:C5",
//                    "502130123456789", "919825098250", "306943808730", "SB00015", "7", "1665427687",
//                    "1", "event", "1231", "9BG1", "true", "7", "25.80",
//                    "80.50", "1", "0", List.of("9BG1"));


        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return "OK";
    }


    @PostMapping("/alertAllPassengersInGeofence")
    public @ResponseBody RestGenericResponse alertAllPassengersInGeofence(@RequestBody AlertPaxInGeofenceTO alertPaxInGeofenceTO) {
        try {
            List<PameasPerson> persons = this.dbProxyService.getPassengerDetails()
                    .stream().filter(pameasPerson -> {
                        int size = pameasPerson.getLocationInfo().getGeofenceHistory().size();
                        if(size <= 0 || pameasPerson.getLocationInfo().getGeofenceHistory().get(size -1) == null){
                            return false;
                        }
                        return pameasPerson.getLocationInfo().getGeofenceHistory().get(size -1).getGfName().equals(alertPaxInGeofenceTO.getGeofence());
                    }).collect(Collectors.toList());

            List<MessageBody> messageBodies =
            persons.stream().map(pameasPerson -> {
                MessageBody body = new MessageBody();
                body.setRecipient("");
               if(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0) != null){
                   body.setRecipient(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
               }
                body.setContent( "<header></header><main><h2 style='color: red; text-align: center;'>Notification</h2>" +
                        "<div style='font-size: x-large;'><b>Attention!! Immediately head to the Muster Station "+
                        "This is not a drill! </b></div>"+
                        "</main>:: sound: siren");
               return body;
            }).collect(Collectors.toList());

            this.passengerMessagingService.callSendMessages(messageBodies);

            return new RestGenericResponse("OK");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new RestGenericResponse("ERROR");
        }
    }



}


