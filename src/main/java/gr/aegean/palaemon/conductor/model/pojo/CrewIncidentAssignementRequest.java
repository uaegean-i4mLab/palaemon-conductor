package gr.aegean.palaemon.conductor.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrewIncidentAssignementRequest {

    private  List<ConstraintSolverIncident> incidents;
    List<IncidentCrewPerson> crewMembers;
}
