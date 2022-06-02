package gr.aegean.palaemon.conductor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.MessagingServiceCaller;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessagingServiceCallerImpl implements MessagingServiceCaller {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void callSendMessages(List<MessageBody> bodies) {
        String url = System.getenv("MESSAGING_SERVICE_URI") + "sendMessages";
        MessagingServiceRequest serviceRequest = new MessagingServiceRequest();
        serviceRequest.setType("NOTIFICATION");
        serviceRequest.setReceivers(bodies.stream().map(Wrappers::messageBody2Receiver).collect(Collectors.toList()));

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MessagingServiceRequest> request = new HttpEntity<>(serviceRequest, headers);
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Calling messaging service with {}", mapper.writeValueAsString(serviceRequest));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

//        restTemplate.postForObject(url, request, String.class);
    }
}
