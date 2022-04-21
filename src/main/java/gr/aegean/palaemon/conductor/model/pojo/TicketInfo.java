package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfo {

    private String name;
    private String surname;
    private String dateOfBirth;
    private String gender;
    private String ticketNumber;

}