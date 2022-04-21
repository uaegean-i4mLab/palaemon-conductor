package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.pojo.*;

import java.util.List;
import java.util.Map;

public interface RulesEngineService {

    public List<PassengerAssignmentResponse> fetchPassengerMSAssignments(List<Passenger> passengerList, List<String> blockedMS, List<MusterStation> musterStations);

    public List<Map<String,String>>  getMessageBody(PassengerMessageBodyRequests requests);

    public MessageObject getMessageObject(String phase, String taskId);
}
