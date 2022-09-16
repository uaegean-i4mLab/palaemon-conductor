package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SrapTO {

    /*
      Pojo enclosing all the possible inputs from SRAP
     */


    @JsonProperty("passenger_id")
    private String passengerId;
    @JsonProperty("zone_id")
    private String zoneId;
    @JsonProperty("Status")
    private String status;

 // when evacuation status == 1 the following message is sent
    private String messageId;
    private String timestamp;
    private String sender;
    @JsonProperty("SRAP model")
    private String srapModel;
    @JsonProperty("Effectiveness of mitigation measures")
    private String effectivenessOfMitigationMeasures;
    @JsonProperty("Passengers proximity to hazards")
    private String passengersProxToHazards;
    @JsonProperty("Status of Passive containment")
    private String statusOfPassiveContainment;
    @JsonProperty("Spreading")
    private String spreading;
    @JsonProperty("Structural Integrity")
    private String structuralIntegrity;
    @JsonProperty("Stability")
    private String stability;
    @JsonProperty("Hull status")
    private String hullStatus;
    @JsonProperty("Critical system status")
    private String criticalSystemStatus;
    @JsonProperty("Ability to communicate")
    private  String abilityToCommunicate;
    @JsonProperty("Vessel Status")
    private String vesselStatus;
    @JsonProperty("Pax vulnerability onboard")
    private  String paxVulnerabilityOnboard;
    @JsonProperty("Situation Assessment")
    private String situationAssessment;

// when the evacuation status is 2

    @JsonProperty("Individual status")
    private Map<String,String> individualStatus;

    @JsonProperty("Escape routes")
    private Map<String,String> escapeRoutes;

    @JsonProperty("Group performance")
    private Map<String,String> groupPerformance;

    @JsonProperty("Risk of delay")
    private Map<String,String> riskOfDelay;

    // when the evacuation status is  2 this object might also be sent
   @JsonProperty("Urgency for abandonment")
   private String urgencyOfAbandonment;

}
