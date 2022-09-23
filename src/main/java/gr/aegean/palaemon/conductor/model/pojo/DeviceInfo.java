package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo implements Serializable {

    @Field(type = Text)
    private String macAddress;
    @Field(type = Text)
    private String hashedMacAddress;
    @Field(type = Text)
    private String imsi;
    @Field(type = Text)
    private String msisdn;
    @Field(type = Text)
    private String imei;
    @Field(type = Text)
    private  String braceletId;


}