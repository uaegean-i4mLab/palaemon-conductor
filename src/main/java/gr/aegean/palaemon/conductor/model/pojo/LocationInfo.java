package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LocationInfo implements Serializable {
    private List<UserGeofenceUnit> geofenceHistory;
    private List<UserLocationUnit> locationHistory;
}
