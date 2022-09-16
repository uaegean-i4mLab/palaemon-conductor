package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LegacySystemTO {

    private boolean mitigationSystemActivated;
    private boolean adjecentDetectors;
    private boolean containmentDoorsClosed;
    @JsonProperty("Gzgm")
    private boolean gzgm;
    private boolean internalCommunication;
    private boolean externalCommunication;
    private boolean propulsionSystem;
    private boolean steeringSystem;
    private boolean navigationalSystem;

}
