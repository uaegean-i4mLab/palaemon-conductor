package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.palaemon.conductor.model.pojo.Incident;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentTO {
    private String id;

    private String passengerName;
    private String passengerSurname;
    private String healthIssues;
    private String mobilityIssues;
    private String pregnancyStatus;
    private String assignedCrewMemberId;
    @JsonProperty("x_loc")
    private String xLoc;
    @JsonProperty("y_loc")
    private String yLoc;
    private String[] preferredLanguage;
    private String geofence;
    private String deck;
    private String timestamp;
    private Incident.IncidentStatus status;
    private String incidentId;
}
