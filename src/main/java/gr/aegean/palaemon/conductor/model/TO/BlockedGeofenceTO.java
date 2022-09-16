package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlockedGeofenceTO {

    /*
    {
  "geofence": "geo1",
	"status":"blocked"
}
     */
    private String geofence;
    private String status;
}
