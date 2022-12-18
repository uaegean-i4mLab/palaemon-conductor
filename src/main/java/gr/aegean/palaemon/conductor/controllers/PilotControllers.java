package gr.aegean.palaemon.conductor.controllers;

import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
import gr.aegean.palaemon.conductor.model.pojo.KeycloakAccessTokenResponse;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.ElasticService;
import gr.aegean.palaemon.conductor.utils.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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


    private final String DB_PROXY_URI = System.getenv("DB_PROXY_URI");

    @GetMapping("/pilot/makePax")
    public @ResponseBody String movePersonFromMSWithTicket123() {


        TestingUtils.addTestPerson("", "99", "102", "Evaggelos",
                "Damigos", "pax1", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862050", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P1",
                "502130123456789", "919825098250", "", "SB000P1", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "15.80",
                "90.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Georgios",
                "Kalogridakis", "pax2", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862051", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P2",
                "502130123456789", "919825098250", "", "SB000P2", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "15.80",
                "92.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Georgios",
                "Kalos", "pax3", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862052", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P3",
                "502130123456789", "919825098250", "", "SB000P2", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "17.80",
                "95.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Vasiliki",
                "Karagianni", "pax4", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862053", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P4",
                "502130123456789", "919825098250", "", "SB000P2", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "22.80",
                "101.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Ilias",
                "Karaiskos", "pax5", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862054", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P5",
                "502130123456789", "919825098250", "", "SB000P5", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "31.80",
                "90.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Aimilios",
                "Kleftogiannis", "pax6", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862055", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P6",
                "502130123456789", "919825098250", "", "SB000P5", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "15.80",
                "98.50", "1", "0", List.of("9BG1"));

        // ***********
        TestingUtils.addTestPerson("", "99", "102", "Dimitrios",
                "Koutoukis", "pax7", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862056", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P7",
                "502130123456789", "919825098250", "", "SB000P7", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "40.80",
                "87.50", "1", "0", List.of("9BG2"));

        TestingUtils.addTestPerson("", "99", "102", "Anastasios",
                "Liveris", "pax8", "max", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862057", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P8",
                "502130123456789", "919825098250", "", "SB000P8", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "15.80",
                "80.50", "1", "0", List.of("9BG2"));


        TestingUtils.addTestPerson("", "99", "102", "Georgia",
                "Makrodima", "pax9", "female", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862058", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P9",
                "502130123456789", "919825098250", "", "SB000P8", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "15.80",
                "85.50", "1", "0", List.of("9BG2"));

        TestingUtils.addTestPerson("", "99", "102", "Evaggelos",
                "Maniatis", "pax10", "male", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862059", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P10",
                "502130123456789", "919825098250", "", "SB000P10", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "15.80",
                "91.50", "1", "0", List.of("9BG2"));

        TestingUtils.addTestPerson("", "99", "102", "Konstantinos",
                "Moularas", "pax11", "male", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862060", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P11",
                "502130123456789", "919825098250", "", "SB000P10", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "15.80",
                "88.50", "1", "0", List.of("9BG2"));

        TestingUtils.addTestPerson("", "99", "102", "Konstantinos",
                "Pantazis", "pax12", "male", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862061", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P12",
                "502130123456789", "919825098250", "", "SB000P11", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "15.80",
                "78.50", "1", "0", List.of("9BG2"));

        TestingUtils.addTestPerson("", "99", "102", "Virginia",
                "Sklavounou", "pax13", "female", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862062", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P13",
                "502130123456789", "919825098250", "", "SB000P11", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "15.80",
                "105.50", "1", "0", List.of("9BG2"));


        TestingUtils.addTestPerson("", "99", "102", "Georgios",
                "Spathias", "pax14", "female", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862063", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P14",
                "502130123456789", "919825098250", "", "SB000P11", "9", "1665427687",
                "1", "event", "1231", "9BG2", "true", "9", "15.80",
                "110.50", "1", "0", List.of("9BG2"));


        return "ok";
    }


    @GetMapping("/pilot/geo")
    public @ResponseBody String initGeofences() {
        List<String> geofenceName = Arrays.asList("9BG1",
                "9BG2",  "CCab9223", "CCab92YY",
                //  *************
                // MUSTER STATION
                "9CG0",
                // ***************
                "9BG3", "9BG4", "9BG1+");
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
