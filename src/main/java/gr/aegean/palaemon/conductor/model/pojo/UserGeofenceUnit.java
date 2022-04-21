package gr.aegean.palaemon.conductor.model.pojo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@ToString
@Getter
@Setter
@NoArgsConstructor
public class UserGeofenceUnit implements Serializable { //like location

    private String gfEvent;
    private String gfId;
    private String gfName;
    private String macAddress;
    private String isAssociated;
    private String dwellTime;
    private String hashedMacAddress;
    private String timestamp;
    private String deck;

}