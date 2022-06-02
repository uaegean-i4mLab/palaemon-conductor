package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.pojo.CalculateDistanceRequest;
import gr.aegean.palaemon.conductor.model.pojo.CrewIncidentAssignementRequest;
import gr.aegean.palaemon.conductor.service.DistanceCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Slf4j
public class DistanceCalculatorServiceImpl implements DistanceCalculatorService {

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public Integer calculateDistance(CalculateDistanceRequest calculateDistanceRequest) {
        String url = System.getenv("DISTANCE_CALCULATOR_URL") + "getDistance";


        HttpHeaders headers = new HttpHeaders();
        HttpEntity<CalculateDistanceRequest> request = new HttpEntity<>(calculateDistanceRequest, headers);

        try {
            String response = restTemplate.postForObject(url, request, String.class);
            log.info(response);
//TODO retrun the actual vaule here and remove the mock
            //return Integer.parseInt(response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        Random rand = new Random();
        return rand.nextInt(400);

    }
}
