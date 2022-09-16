package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KafkaHeartBeatResponse {

    private String originator;
    @JsonProperty("operation_mode")
    private int operationMode;
    private String timestamp;
}
