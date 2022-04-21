package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DutySchedule {
    private LocalDateTime dutyStartDateTime;
    private LocalDateTime dutyEndDateTime;
}
