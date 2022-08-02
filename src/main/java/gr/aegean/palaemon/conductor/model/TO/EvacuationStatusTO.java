package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EvacuationStatusTO implements Serializable {

    private String id;
    private String status;


}
