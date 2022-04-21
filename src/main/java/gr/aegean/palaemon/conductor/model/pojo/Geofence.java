package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Geofence {

    private String id;
    @JsonProperty("gfName")
    private String gfName;
    private String deck;
    private String status;
    @JsonProperty("mustering")
    private String musteringStation;

    
}
