package gr.aegean.palaemon.conductor.controllers;

import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class PilotControllers {


    @Autowired
    ElasticService elasticService;

    @Autowired
    DBProxyService dbProxyService;

    @GetMapping("/pilot/makePax")
    public @ResponseBody String movePersonFromMSWithTicket123() {


        TestingUtils.addTestPerson("", "99", "102", "Aggeliki",
                "Souraiti", "pax1", "female", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862050", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P1",
                "502130123456789", "919825098250", "", "SB000P1", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "25.80",
                "30.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Panagiotis",
                "Siokouros", "pax2", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862051", "pana.shock@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P2",
                "502130123456789", "919825098250", "", "SB000P2", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "26.80",
                "32.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Evaggelos",
                "Maniatis", "pax3", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862052", "pana.shock@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P3",
                "502130123456789", "919825098250", "", "SB000P3", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "27.80",
                "31.50", "1", "0", List.of("9BG1"));


        TestingUtils.addTestPerson("", "99", "102", "Christina",
                "Argyriadi", "pax4", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862053", "vagmandasp@gmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P4",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "37.80",
                "31.50", "1", "0", List.of("9BG1"));

// ------------
        TestingUtils.addTestPerson("", "99", "102", "Georgia",
                "Makridima", "pax5", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862054", "gogo_makrid1720@yahoo.gr", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P5",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "27.80",
                "35.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Virginia",
                "Sklavounou", "pax6", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862055", "virnaki.s@gmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P6",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "27.80",
                "37.50", "1", "0", List.of("9BG1"));


        TestingUtils.addTestPerson("", "99", "102", "Vasiliki",
                "Karagianni", "pax7", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862056", "vasilikikar2000@yahoo.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P7",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "27.80",
                "32.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Georgios",
                "Kalogridakis", "pax8", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862057", "kalogri96@gmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P8",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "29.80",
                "31.50", "1", "0", List.of("9BG1"));


        TestingUtils.addTestPerson("", "99", "102", "Stauros",
                "Pouloglou-Otzaman", "pax9", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862058", "nm17079@mail.ntua.gr", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P9",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "27.80",
                "35.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Persephony",
                "Pantazidi", "pax10", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862059", "nm17803@ntua.gr", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P10",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "31.80",
                "30.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Margarita",
                "Argyrou", "pax11", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862060", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P11",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "32.80",
                "33.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Ioannis",
                "Mariolis", "pax12", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862061", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P12",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "30.80",
                "33.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Georgios",
                "Kalos", "pax13", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862062", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P13",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "30.80",
                "27.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Katerina",
                "Mpouranta", "pax14", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862063", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P14",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "32.80",
                "27.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Georgios",
                "Apostolatos", "pax15", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862063", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P15",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "32.80",
                "22.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Konstantinos",
                "Mpourlas", "pax16", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862064", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P16",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "32.80",
                "42.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Anna",
                "Georgiadi", "pax17", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862065", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P17",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "33.80",
                "42.50", "1", "0", List.of("9BG1"));


        TestingUtils.addTestPerson("", "99", "102", "Anastasios",
                "Livieris", "pax18", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862066", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P18",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "23.80",
                "42.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Konstantinos",
                "Pantazis", "pax19", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862067", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P19",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "22.80",
                "42.50", "1", "0", List.of("9BG1"));


        TestingUtils.addTestPerson("", "99", "102", "Panagiotis",
                "Ntesikas", "pax20", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862068", "margarita.a.20@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P20",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "27.80",
                "32.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Myrto",
                "Chatzikaneloy", "pax21", "female", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862070", "chatzikanellou.myrto@gmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P21",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "37.80",
                "32.50", "1", "0", List.of("9BG1"));


        TestingUtils.addTestPerson("", "99", "102", "Marios",
                "Chatzitheodosiou", "pax22", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862071", "marios-chatzitheodosiou@hotmail.gr", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P22",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "36.80",
                "32.50", "1", "0", List.of("9BG1"));

        TestingUtils.addTestPerson("", "99", "102", "Christoforos",
                "Lemonis", "pax23", "male", "21", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862072", "xristoforoslemonis21@gmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P23",
                "502130123456789", "919825098250", "", "SB000P4", "9", "1665427687",
                "1", "event", "1231", "9BG1", "true", "9", "36.80",
                "32.50", "1", "0", List.of("9BG1"));






        return "ok";
    }




}
