package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SbPaMEASMessageTO {


    private String originator;
    @JsonProperty("ID")
    private String id;
    @JsonProperty("msg_type")
    private String messageType;
    private String msg;

}
