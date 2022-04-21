package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NetworkInfo implements Serializable {


    private List<DeviceInfo> deviceInfoList;
    private String messagingAppClientId;


}