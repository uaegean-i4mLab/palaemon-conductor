package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KafkaHeartBeatRequest {

    private String originator;
    private String timestamp;
}
