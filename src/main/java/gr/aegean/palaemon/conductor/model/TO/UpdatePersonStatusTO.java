package gr.aegean.palaemon.conductor.model.TO;

import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePersonStatusTO {

    private Personalinfo.AssignmentStatus assignmentStatus;
    private boolean inPosition;
    private String musteringStation;
    private String id;
    private String hashedMacAddress;

}
