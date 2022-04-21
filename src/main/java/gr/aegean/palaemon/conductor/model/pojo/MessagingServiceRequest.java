package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessagingServiceRequest {

    private String type;
    private List<MessageServiceReceiver> receivers;

}
