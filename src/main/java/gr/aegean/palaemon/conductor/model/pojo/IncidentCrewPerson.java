package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IncidentCrewPerson {
/*
    "crewMemberId": "id1",
            "name":"name1",
            "surname":"surname1",
            "emergencyDuty":"medical_unit",
            "spokenLanguages": ["en"],
            "assignmentStatus":"UNASSIGNED",
            "distanceFromIncidents":{"incident1":100, "incident2":102},
            "deck":"deck1"
            */
    private String crewMemberId;
    private String name;
    private String surname;
    private String emergencyDuty;
    private String[] spokenLanguages;
    private String assignmentStatus;
    private HashMap<String,Integer> distanceFromIncidents;
    private String deck;


}
