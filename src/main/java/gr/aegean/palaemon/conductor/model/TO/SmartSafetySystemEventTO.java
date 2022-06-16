package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SmartSafetySystemEventTO {


    /**
     * {
     * 'type': 'Fall/Fire/Grounding',
     * 'timestamp': '2021-01-13T11:30:16.825950+00:00',
     * 'deck': '5',
     * 'position_x': 13.72,
     * 'position_y': 5.47
     * }
     */
    private String type;
    private String timestamp;
    private String deck;
    @JsonProperty("position_x")
    private String positionX;
    @JsonProperty("position_y")
    private String positionY;

}
