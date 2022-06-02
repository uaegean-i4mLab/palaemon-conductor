package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.TO.PassengerIncidentSolutionTO;
import gr.aegean.palaemon.conductor.model.pojo.*;

import java.util.List;
import java.util.Map;

public interface ConstraintSolverService {

    public PassengerIncidentSolutionTO makeAssignment(List<ConstraintSolverIncident> incidents, List<PameasPerson> crewMembers);


}
