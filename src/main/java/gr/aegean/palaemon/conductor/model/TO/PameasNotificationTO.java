package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PameasNotificationTO {

    String type;
    String id;
    String status;
    String timestamp;

}
