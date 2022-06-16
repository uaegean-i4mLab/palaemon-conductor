package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CrewAssignmentRequestTO {
    ArrayList<PameasNotificationTO> assignments;

}
