package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.EvacuationStatusTO;
import gr.aegean.palaemon.conductor.model.TO.IncidentTO;
import gr.aegean.palaemon.conductor.model.TO.UpdatePersonStatusTO;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.AccessTokenService;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DBProxyServiceImpl implements DBProxyService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AccessTokenService accessTokenService;


    @Override
    public void updateGeofenceStatus(Geofence geofence) {
        String url
                = System.getenv("DB_PROXY_URI") + "updateGeofence";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        HttpEntity<Geofence> request = new HttpEntity<>(geofence, headers);

        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
    }

    @Override
    public void updatePassengerAssignedMS(String musteringStation, String hashedMacAddress) {
        String url
                = System.getenv("DB_PROXY_URI") + "updatePassengerMS";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        UpdatePersonStatusTO updatePersonRequest = new UpdatePersonStatusTO();
        updatePersonRequest.setHashedMacAddress(hashedMacAddress);
        updatePersonRequest.setMusteringStation(musteringStation);

        HttpEntity<UpdatePersonStatusTO> request = new HttpEntity<>(updatePersonRequest, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
    }

    @Override
    public void updateCrewInPosition(String hashedMacAddress, boolean inPosition) {
        String url
                = System.getenv("DB_PROXY_URI") + "updateCrewInPosition";
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);
        UpdatePersonStatusTO updatePersonRequest = new UpdatePersonStatusTO();
        updatePersonRequest.setHashedMacAddress(hashedMacAddress);
        updatePersonRequest.setInPosition(inPosition);
        HttpEntity<UpdatePersonStatusTO> request = new HttpEntity<>(updatePersonRequest, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
    }

    @Override
    public ShipsGeofences getAllGeofences() {
        String uri
                = System.getenv("DB_PROXY_URI") + "getGeofenceStatus";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

//        log.info(bearer);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response.getBody(), ShipsGeofences.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }

//        return response.getBody();
    }

    @Override
    public String getEvacuationStatus() {
        String uri
                = System.getenv("DB_PROXY_URI") + "getEvacuationStatus";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<EvacuationStatusTO> response = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, EvacuationStatusTO.class);
        return response.getBody().getStatus();
    }

    @Override
    public List<PameasPerson> getPassengerDetails() {
        String uri
                = System.getenv("DB_PROXY_URI") + "getPassengers";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseString = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, String.class);
        ObjectMapper mapper = new ObjectMapper();

        try {
            return Arrays.asList(mapper.readValue(responseString.getBody(), PameasPerson[].class));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public List<PameasPerson> getCrewMembers() {
        String uri
                = System.getenv("DB_PROXY_URI") + "getCrew";

        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseString = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, String.class);
        ObjectMapper mapper = new ObjectMapper();

        try {
            return Arrays.asList(mapper.readValue(responseString.getBody(), PameasPerson[].class));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Optional<PameasPerson> getSinglePassengerDetails(String hashedMacAddress) {
        String uri
                = System.getenv("DB_PROXY_URI") + "getPassengers";
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseString = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, String.class);
        ObjectMapper mapper = new ObjectMapper();
        Optional<PameasPerson> result = Optional.empty();

        try {
            PameasPerson[] persons = mapper.readValue(responseString.getBody(), PameasPerson[].class);
            return Arrays.stream(persons).filter(pameasPerson ->
                    pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress().equals(hashedMacAddress)).findFirst();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return result;
    }


    @Override
    public void updatePassengerPath(UpdatePersonStatusTO personStatusTO) {
//        String uri
//                = System.getenv("DB_PROXY_URI") + "updatePassengerPath";
//
//        HttpHeaders headers = new HttpHeaders();
//        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
//        headers.set("Authorization", bearer);
//
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    }

    @Override
    public void declarePassengerIncident(IncidentTO incidentTO) {
        String url
                = System.getenv("DB_PROXY_URI") + "declarePassengerIncident";
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        HttpEntity<IncidentTO> request = new HttpEntity<>(incidentTO, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
    }

    @Override
    public void updateCrewMemberStatus(String hashAddress, String id, Personalinfo.AssignmentStatus status) {
        String url
                = System.getenv("DB_PROXY_URI") + "updateCrewMemberStatus";
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        UpdatePersonStatusTO personStatusTO = new UpdatePersonStatusTO();
        personStatusTO.setAssignmentStatus(status);
        personStatusTO.setHashedMacAddress(hashAddress);
        personStatusTO.setId(id);

        HttpEntity<UpdatePersonStatusTO> request = new HttpEntity<>(personStatusTO, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
    }

    @Override
    public void savePassengerIncident(IncidentTO incidentTO) {
        String url
                = System.getenv("DB_PROXY_URI") + "declarePassengerIncident";
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);
        HttpEntity<IncidentTO> request = new HttpEntity<>(incidentTO, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
    }

    @Override
    public void updatePassengerIncident(IncidentTO incidentTO) {
        String url
                = System.getenv("DB_PROXY_URI") + "updatePassengerIncident";
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);
        HttpEntity<IncidentTO> request = new HttpEntity<>(incidentTO, headers);
        String response = restTemplate.postForObject(url, request, String.class);
        log.info(response);
    }

    @Override
    public Optional<IncidentTO> getIncidentFromId(String incidentId) {
        String uri
                = System.getenv("DB_PROXY_URI") + "getPassengerIncident?id=" + incidentId;
        HttpHeaders headers = new HttpHeaders();
        String bearer = "Bearer " + accessTokenService.getAccessToken().get();
        headers.set("Authorization", bearer);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<IncidentTO> incidentTo = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, IncidentTO.class);
        return Optional.ofNullable(incidentTo.getBody());
    }
}
