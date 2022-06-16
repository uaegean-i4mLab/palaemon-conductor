package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConfirmCrewInPositionTO {

    private String hashMacAddress;
    @JsonProperty("in_position")
    private String inPosition;
}
