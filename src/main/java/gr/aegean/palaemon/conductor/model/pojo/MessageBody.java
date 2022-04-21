package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageBody {
    private String hashedMacAddress;
    private String content;
    private String visualAid;

}
