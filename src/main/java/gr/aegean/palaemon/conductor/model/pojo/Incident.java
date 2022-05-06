package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Incident implements Serializable {

    private String id;
    private String passengerName;
    private String passengerSurname;
    private String healthIssues;
    private String mobilityIssues;
    private String pregnancyStatus;
    private String assignedCrewMemberIdDecrypted;
    private String xLoc;
    private String yLoc;
    private String[] preferredLanguage;
    private String geofenceId;
    private String deck;
    private String timestamp;
    private IncidentStatus status;


    public enum IncidentStatus {
        OPEN("OPEN"),
        CLOSED("CLOSED");
        private final String name;

        private IncidentStatus(String s) {
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
