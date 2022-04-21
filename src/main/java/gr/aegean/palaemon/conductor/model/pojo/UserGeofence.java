package gr.aegean.palaemon.conductor.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class UserGeofence { // history of clients geofences

    private String macAddress;
    private String hashedMacAddress;
    private List<UserGeofenceUnit> geofenceHistory;
}