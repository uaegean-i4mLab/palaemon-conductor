package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.pojo.CalculateDistanceRequest;
import gr.aegean.palaemon.conductor.model.pojo.ConstraintSolverIncident;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;

import java.util.List;

public interface DistanceCalculatorService {

    public Integer calculateDistance(CalculateDistanceRequest calculateDistanceRequest);


}
