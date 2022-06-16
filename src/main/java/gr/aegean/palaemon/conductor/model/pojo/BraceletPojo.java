package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BraceletPojo {


    /*
    message_code: "string",
timestamp: "timestamp",
braceletId: "string",
broadcast: "boolean"
     */

    @JsonProperty("message_code")
    String messageCode;
    String timestamp;
    String braceletId;
    String broadcast;

}
