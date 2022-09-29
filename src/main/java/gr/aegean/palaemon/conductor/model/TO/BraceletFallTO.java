package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BraceletFallTO {

    /*
    {
"timestamp":  "_"(to be added by platform at reception)
""ID": "SB0000"; (smart bracelet ID ; 0000-9999)
"event_id":  "number" (0= Fall ; 1= Button )
     */

    private String timestamp;
    @JsonProperty("component_id")
    private String id;
    @JsonProperty("event_id")
    private int eventId;
}
