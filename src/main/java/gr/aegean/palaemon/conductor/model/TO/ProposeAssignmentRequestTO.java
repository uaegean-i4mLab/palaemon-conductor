package gr.aegean.palaemon.conductor.model.TO;

import gr.aegean.palaemon.conductor.model.pojo.ConstraintSolverIncident;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProposeAssignmentRequestTO {


    ArrayList<ConstraintSolverIncident> incidents;
}
