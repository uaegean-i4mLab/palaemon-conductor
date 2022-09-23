package gr.aegean.palaemon.conductor.model.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class UserLocationUnit implements Serializable {

    @Field(type = Text)
    @JsonProperty("xLocation")
    private String xLocation;
    @Field(type = Text)
    @JsonProperty("yLocation")
    private String yLocation;
    @Field(type = Text)
    private String errorLevel;
    @Field(type = Text)
    private String isAssociated;
    @Field(type = Text)
    private String campusId;
    @Field(type = Text)
    private String buildingId;
    @Field(type = Text)
    private String floorId;
    @Field(type = Text)
    private String hashedMacAddress;
    @Field(type = Text)
    private String geofenceId;
//    @Field( type = FieldType.Nested)
    private List<String> geofenceNames;
    // private String rssiVal;
    @Field(type = Text)
    private String timestamp;
}