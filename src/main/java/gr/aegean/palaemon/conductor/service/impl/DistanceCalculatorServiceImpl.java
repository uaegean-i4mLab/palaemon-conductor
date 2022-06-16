package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.GetGeofenceRequestTO;
import gr.aegean.palaemon.conductor.model.pojo.CalculateDistanceRequest;
import gr.aegean.palaemon.conductor.model.pojo.CrewIncidentAssignementRequest;
import gr.aegean.palaemon.conductor.service.DistanceCalculatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.util.Optional;
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
            assert response != null;
            return Math.abs(Math.round(Float.parseFloat(response)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<String> getGeofenceFromCordsAndDeck(String deck, String xCoord, String yCoord) {
        String url = System.getenv("DISTANCE_CALCULATOR_URL") + "getGeofence";
        HttpHeaders headers = new HttpHeaders();
        GetGeofenceRequestTO getGeofenceRequestTO = new GetGeofenceRequestTO(deck, xCoord, yCoord);
        HttpEntity<GetGeofenceRequestTO> request = new HttpEntity<>(getGeofenceRequestTO, headers);
        try {
            String response = restTemplate.postForObject(url, request, String.class);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }
}
