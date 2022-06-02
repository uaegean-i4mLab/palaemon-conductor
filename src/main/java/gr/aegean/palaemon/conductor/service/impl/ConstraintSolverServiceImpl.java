package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.PassengerIncidentSolutionTO;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.ConstraintSolverService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConstraintSolverServiceImpl implements ConstraintSolverService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    DistanceCalculatorServiceImpl distanceCalculatorService;

    @Override
    public PassengerIncidentSolutionTO makeAssignment(List<ConstraintSolverIncident> incidents, List<PameasPerson> crewMembers) {
        String url = System.getenv("CONSTRAINT_SOLVER_URI") + "makeAssignment";


        CrewIncidentAssignementRequest assignmentRequest = new CrewIncidentAssignementRequest();
        assignmentRequest.setIncidents(incidents);
        assignmentRequest.setCrewMembers(crewMembers.stream().map(pameasPerson ->
                Wrappers.pameasPerson2IncidentCrewPerson(pameasPerson, incidents, distanceCalculatorService)).collect(Collectors.toList()));

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CrewIncidentAssignementRequest> request = new HttpEntity<>(assignmentRequest, headers);

        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info(mapper.writeValueAsString(assignmentRequest));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

//        PassengerAssignmentResponse[] response = restTemplate.postForObject(url, request, PassengerAssignmentResponse[].class);
        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        try {
            return mapper.readValue(response, PassengerIncidentSolutionTO.class);
        } catch (JsonProcessingException e) {
          log.error(e.getMessage());

        }
        return null;
    }
}
