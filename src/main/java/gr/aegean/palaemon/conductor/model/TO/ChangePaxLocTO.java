package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChangePaxLocTO {
    private String messagingId;
    private String geofence;
}
