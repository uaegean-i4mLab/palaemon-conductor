package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SrapTO {

    @JsonProperty("passenger_id")
    private String passengerId;
    @JsonProperty("zone_id")
    private String zoneId;
    private String status;
}
