package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PameasNotificationTO {

    String type;
    String id;
    String status;
    String timestamp;
    String macAddress;
    String passengerName;
    String passengerSurname;
    String[] preferredLanguage;
    @JsonProperty("health_issues")
    String healthIssues;

    String mobilityIssues;

    String pregnancyStatus;

    String[] assignedCrewMemberId;
    @JsonProperty("x_loc")
    String xloc;
    @JsonProperty("y_loc")
    String yloc;
    @JsonProperty("geofence")
    String geofence;

    NotificationIncidentTO incident;
    NotificationIncidentCrewTO[] crew;

}
