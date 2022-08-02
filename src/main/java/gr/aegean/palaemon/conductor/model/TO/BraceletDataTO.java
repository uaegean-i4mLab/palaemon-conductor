package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BraceletDataTO {
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
    private String hr;
    @JsonProperty("Sp02")
    private String sp02;
    @JsonProperty("Temp")
    private String temp;
    @JsonProperty("Pitch")
    private String pitch;
    @JsonProperty("Roll")
    private String roll;
    @JsonProperty("Angle")
    private String angle;
}
