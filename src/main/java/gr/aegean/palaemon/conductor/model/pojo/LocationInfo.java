package gr.aegean.palaemon.conductor.model.pojo;


import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LocationInfo  implements Serializable {
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<UserGeofenceUnit> geofenceHistory;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<UserLocationUnit> locationHistory;
    @Field(type = Text)
    private String speed;
}
