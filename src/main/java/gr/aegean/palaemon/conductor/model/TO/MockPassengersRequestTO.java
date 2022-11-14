package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MockPassengersRequestTO {
    private int deck;
    private String numberOfPassengers;
    private int oxygenIssuesPercent;
    private int heartIssuesPercent;
}
