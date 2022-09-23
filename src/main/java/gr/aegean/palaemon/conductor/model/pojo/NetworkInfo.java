package gr.aegean.palaemon.conductor.model.pojo;

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
public class NetworkInfo  implements Serializable {


    @Field(type = FieldType.Nested, includeInParent = true)
    private List<DeviceInfo> deviceInfoList;
    @Field(type = Text)
    private String messagingAppClientId;

    @Field(type = Text)
    private String arGlassesId;

    @Field(type = Text)
    private String braceletId;


}