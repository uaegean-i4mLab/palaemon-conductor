package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;


import java.io.Serializable;
import java.util.List;



@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Personalinfo implements Serializable {


    private String name;

    private String surname;

    private String dateOfBirth;

    private String gender;

    private String personalId;

    private String ticketNumber;

    private List<TicketInfo> ticketInfo;

    private boolean crew;



    private String embarkationPort;

    private String disembarkationPort;


    private String email;

    private String postalAddress;

    private String emergencyContact;

    private String countryOfResidence;


    private String medicalCondition;

    private String mobilityIssues;

    private String prengencyData;


    private String role;

    private String emergencyDuty;




    private List<String> preferredLanguage;


    // New additions


    private boolean inPosition;


    private AssignmentStatus assignmentStatus;

    private List<DutySchedule> dutyScheduleList;

    private String assignedMusteringStation;

    private String assignedPath;

    private String oxygenSaturation;

    private String hasFallen;
    private String heartBeat;

    public enum AssignmentStatus {
        ASSIGNED("ASSIGNED"),
        UNASSIGNED("UNASSIGNED");
        private final String name;
        private AssignmentStatus(String s) {
            name = s;
        }
        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }
    }



}