package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationIncidentCrewTO {

    private String id;
    private String name;
    private String surname;
    private String hashedMacAddress;
    private String emergencyRole;
    private String[] languages;
    private boolean assigned;
    private String geofence;
    private String xloc;
    private String yloc;



}
