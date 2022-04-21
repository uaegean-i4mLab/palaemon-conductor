package gr.aegean.palaemon.conductor.model.pojo;



import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PassengerAssignmentRequest {
    private List<Passenger> passengers;
    private List<MusterStation> musteringStations;
    private List<String> blocked;
}
