package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageBody {
    @JsonProperty("hashedMacAddress")
    private String recipient;
    private String content;
    private String visualAid;

}
