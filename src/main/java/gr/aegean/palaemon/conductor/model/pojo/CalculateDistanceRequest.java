package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CalculateDistanceRequest {
    @JsonProperty("xCoord")
    private String xCoord;
    @JsonProperty("yCoord")
    private String yCoord;
    @JsonProperty("xCoord2")
    private String xCoord2;
    @JsonProperty("yCoord2")
    private String yCoord2;
    private String deck;
    private String timestamp;
    private String musterStationId;


}
