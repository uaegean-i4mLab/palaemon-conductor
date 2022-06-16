package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BraceletHealthPojo {
/*
{
"timestamp":  "_"(to be added by platform at reception)
"ID":  "SB0000"; (smart bracelet ID ;  0000-9999)
"HR":  heartrate ; (bpm)
"SpO2": Oxygen Sat.; (0-100 %)
"Temp": temperature;  (ºC)
"Pitch";
"Roll":
"Angle":
}
 */

    private String timestamp;
    @JsonProperty("ID")
    private String id;
    @JsonProperty("HR")
    private String heartbeat;
    @JsonProperty("SpO2")
    private String saturation;
    @JsonProperty("Temp")
    private String temperature;
    @JsonProperty("Pitch")
    private String pitch;
    @JsonProperty("Roll")
    private String roll;
    @JsonProperty("Angle")
    private String angle;


}
