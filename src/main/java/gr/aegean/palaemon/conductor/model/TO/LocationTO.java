package gr.aegean.palaemon.conductor.model.TO;

import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationTO implements Serializable {

    private String macAddress;
    private String hashedMacAddress;
    private UserGeofenceUnit geofence;
    private UserLocationUnit location;
}
