package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class ConnectedPersonTO {
    private String name;
    private String surname;
    private String gender;
    private String age;
    private String ticketNumber;
}