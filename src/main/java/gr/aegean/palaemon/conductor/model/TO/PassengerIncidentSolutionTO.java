package gr.aegean.palaemon.conductor.model.TO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PassengerIncidentSolutionTO {

    private List<IncidentAssignmentTO> passengerIncidentList;
    private List<CrewMemberTO> crewMemberList;
}

