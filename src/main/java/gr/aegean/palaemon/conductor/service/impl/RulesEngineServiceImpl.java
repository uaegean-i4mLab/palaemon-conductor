package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.RulesEngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class RulesEngineServiceImpl implements RulesEngineService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<PassengerAssignmentResponse> fetchPassengerMSAssignments(List<Passenger> passengerList, List<String> blockedMS, List<MusterStation> musterStations) {
        String url = System.getenv("RULES_ENGINE_URI") + "fetchMusteringActions";
        PassengerAssignmentRequest passengerAssignmentRequest = new PassengerAssignmentRequest();
        passengerAssignmentRequest.setPassengers(passengerList);
        passengerAssignmentRequest.setBlocked(blockedMS);
        passengerAssignmentRequest.setMusteringStations(musterStations);


        HttpHeaders headers = new HttpHeaders();
        HttpEntity<PassengerAssignmentRequest> request = new HttpEntity<>(passengerAssignmentRequest, headers);

        ObjectMapper mapper = new ObjectMapper();
//        try {
//            log.info(mapper.writeValueAsString(passengerAssignmentRequest));
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage());
//        }

        PassengerAssignmentResponse[] response = restTemplate.postForObject(url, request, PassengerAssignmentResponse[].class);
        //log.info(response);
        return Arrays.asList(Objects.requireNonNull(response));
    }

    @Override
    public List<Map<String, String>> getMessageBody(PassengerMessageBodyRequests requests) {
        String url = System.getenv("RULES_ENGINE_URI") + "fetchMessageBody";//"http://localhost:8082/fetchMessageBody"; //

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<PassengerMessageBodyRequests> request = new HttpEntity<>(requests, headers);

//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            log.info(mapper.writeValueAsString(requests));
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage());
//        }
        Map<String, String>[] response = restTemplate.postForObject(url, request, Map[].class);
        return Arrays.asList(response);
        // log.info(response);
        //[{"hashedMacAddress":"dabf99b54f86bcc4f5949a8bcd7e29961081b36d42cb0905e1e52a131652adf9",
        // "content":"Please remain in your position and a Crew member will assist you to reach MusterStation 1",
        // "visualAid":null}]
    }

    @Override
    public MessageObject getMessageObject(String phase, String taskId) {
        String url = System.getenv("RULES_ENGINE_URI") + "getMessageObject?phase=" + phase + "&taskId=" + taskId;
        return restTemplate.getForObject(url, MessageObject.class);
    }
}
