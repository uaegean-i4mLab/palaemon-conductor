package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotificationIncidentTO {

    private String id;
    @JsonProperty("passenger_name")
    private String passengerName;
    @JsonProperty("passenger_surname")
    private String passengerSurname;
    @JsonProperty("health_issues")
    private String healthIssues;
    @JsonProperty("mobility_issues")
    private String mobilityIssues;
    @JsonProperty("pregnancy_status")
    private String pregnancyStatus;
    @JsonProperty("xloc")
    private String xloc;
    @JsonProperty("yloc")
    private String yloc;
    @JsonProperty("geofence")
    private String geofence;
    @JsonProperty("timestamp")
    private String timestamp;
    private String status;


}
