package gr.aegean.palaemon.conductor.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@ToString
@Getter
@Setter
@NoArgsConstructor
public class UserLocationUnit implements Serializable {

    @JsonProperty("xLocation")
    private String xLocation;
    @JsonProperty("yLocation")
    private String yLocation;
    private String errorLevel;
    private String isAssociated;
    private String campusId;
    private String buildingId;
    private String floorId;
    private String hashedMacAddress;
    private String geofenceId;
//    @Field( type = FieldType.Nested)
    private List<String> geofenceNames;
    // private String rssiVal;
    private String timestamp;
}