package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShipsGeofences {

    private List<Geofence> mustering;
    private List<Geofence> simple;
}
