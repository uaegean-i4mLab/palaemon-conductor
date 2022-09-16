package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SmokeDetectedTO {

 private String deck;
 @JsonProperty("xPosition")
 private String xPosition;
 @JsonProperty("yPosition")
 private String yPosition;

}
