package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonAlias;
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


{"timestamp":"2022-04-20T08:16:45.657877","component_id":"SB0001","HR":51,"O2":0,"Temp":43,"Charge":0,"pitch":-1,"roll":-91,"heading":0}
     */
    private String timestamp;
    @JsonProperty("ID")
    @JsonAlias("component_id")
    private String id;
    @JsonProperty("HR")
    private String hr;
    @JsonProperty("Sp02")
    private String sp02;
    @JsonProperty("Temp")
    private String temp;
    @JsonProperty("Pitch")
    @JsonAlias("pitch")
    private String pitch;
    @JsonProperty("Roll")
    @JsonAlias("roll")
    private String roll;
    @JsonProperty("Angle")
    @JsonAlias("heading")
    private String angle;
}
