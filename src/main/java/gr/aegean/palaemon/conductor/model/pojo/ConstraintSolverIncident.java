package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConstraintSolverIncident {

    private String incidentId;
    private String name;
    private  String surname;
    private  String passengerLanguage;
    private  String healthCondition;
    private String mobilityCondition;
    private String pregnancyCondition;
    private  String deck;
    private  String geofence;
    @JsonProperty("x_loc")
    private  String xLoc;
    @JsonProperty("y_loc")
    private  String yLoc;

    /*

/*{
        "incidentId":"incident1",
        "name":"passName1",
        "surname":"passSurname1",
        "passengerLanguage":"en",
        "healthCondition":"severe_walking_disability",
        "deck":"deck1",
        "geofence":"room1",
        "x_loc":"1231",
        "y_loc":"1334"
        }

 */


}
