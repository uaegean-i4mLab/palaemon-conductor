package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.RulesEngineService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Configurable
public class GetPassengerMSAssignmentsTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(GetPassengerMSAssignmentsTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private RulesEngineService rulesEngineService;

    private DBProxyService dbProxyService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public GetPassengerMSAssignmentsTask(String taskDefName, RulesEngineService rulesEngineService, DBProxyService dbProxyService) {
        this.taskDefName = taskDefName;
        this.rulesEngineService = rulesEngineService;
        this.dbProxyService = dbProxyService;
    }

    /* (non-Javadoc)
     * @see com.netflix.conductor.client.worker.Worker#getTaskDefName()
     */
    @Override
    public String getTaskDefName() {
        return taskDefName;
    }

    /* (non-Javadoc)
     * @see com.netflix.conductor.client.worker.Worker#execute(com.netflix.conductor.common.metadata.tasks.Task)
     */
    @Override
    public TaskResult execute(Task task) {

        logger.info("-----");
        logger.info("Executing5 {}.", taskDefName);

        TaskResult result = new TaskResult(task);

        result.setStatus(TaskResult.Status.COMPLETED);


        processGetPassengerAssignments(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void processGetPassengerAssignments(Task task, TaskResult result) {


        logger.info("Running task: " + task.getTaskDefName());

        List<LinkedHashMap> geofences = (List<LinkedHashMap>) task.getInputData().get("geofences");
        List<LinkedHashMap> passengers = (List<LinkedHashMap>) task.getInputData().get("passenger_details");
        String messageCode = (String) task.getInputData().get("message_code");

        logger.info("Input: ");
//        logger.info("Geofence(s) Param:   {}", geofences);
        logger.info("Passenger Details Param:   {}", passengers);

        List<Passenger> passengerList = passengers.stream().map(Wrappers::hashMap2PameasPerson).map(Wrappers::paemasPerson2Passenger).
                collect(Collectors.toList());
        List<Geofence> geofenceList = geofences.stream().map(Wrappers::hashMap2Geofence).collect(Collectors.toList());
        List<String> blocked = geofenceList.stream().filter(geofence -> geofence.getStatus().toUpperCase().equals("BLOCKED"))
                .map(Geofence::getGfName).collect(Collectors.toList());
        List<MusterStation> musterStations = geofenceList.stream().filter(geofence -> geofence.getMusteringStation().equals("true"))
                .map(Wrappers::geofences2MusterStation).collect(Collectors.toList());

        List<PassengerAssignmentResponse> assignmentResponses =
                rulesEngineService.fetchPassengerMSAssignments(passengerList, blocked, musterStations);


        //Prepare the request for the messageBody call
        PassengerMessageBodyRequests passengerMessageBodyRequests = new PassengerMessageBodyRequests();
        LinkedHashMap<String, String> languages = new LinkedHashMap<>();
        passengers.stream().map(Wrappers::hashMap2PameasPerson).forEach(passenger -> {
            String hashedMac = passenger.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress();
            String language = passenger.getPersonalInfo().getPreferredLanguage().get(0);
            languages.put(hashedMac, language);
        });

        LinkedHashMap<String, String> messageCodes = new LinkedHashMap<>();
        passengers.stream().map(Wrappers::hashMap2PameasPerson).forEach(passenger -> {
            String hashedMac = passenger.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress();
            messageCodes.put(hashedMac, messageCode);
        });


        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        LinkedHashMap<String, String> pathIds = new LinkedHashMap<>();
        LinkedHashMap<String, String> assignedMSs = new LinkedHashMap<>();
        assignmentResponses.stream().forEach(passengerAssignmentResponse -> {
            String action = passengerAssignmentResponse.getAction();
            String hashedMac = passengerAssignmentResponse.getHashedMacAddress();
            actions.put(hashedMac,action);
            String pathId = passengerAssignmentResponse.getPathId();
            pathIds.put(hashedMac,pathId);
            String ms = passengerAssignmentResponse.getMusterStation();
            assignedMSs.put(hashedMac,ms);
        });

        passengerMessageBodyRequests.setPassengerLanguages(languages);
        passengerMessageBodyRequests.setActions(actions);
        passengerMessageBodyRequests.setMessageCodes(messageCodes);
        passengerMessageBodyRequests.setAssignedPathIDs(pathIds);
        passengerMessageBodyRequests.setBlockedGeofences(blocked);
        passengerMessageBodyRequests.setMusterStation(assignedMSs);



        // Store original assignments
        Map<String,String> originalAssignments = new HashMap<>();
        Map<String,String> old2NewMSAssignments = new HashMap<>();
        //calculate original passenger MSs
        passengers.stream().map(Wrappers::hashMap2PameasPerson).forEach(pameasPerson -> {
            if(!StringUtils.isEmpty(pameasPerson.getPersonalInfo().getAssignedMusteringStation() )){
                originalAssignments.put(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress(),
                        pameasPerson.getPersonalInfo().getAssignedMusteringStation());
            }
        });
        //check updated assignments and //compare and propagate results // and store them in the DB
        assignmentResponses.forEach(passengerAssignmentResponse -> {
            String hashedMac = passengerAssignmentResponse.getHashedMacAddress();
            String ms = passengerAssignmentResponse.getMusterStation();
            if(originalAssignments.get(hashedMac) != null && !originalAssignments.get(hashedMac).equals(ms)){
                old2NewMSAssignments.put(originalAssignments.get(hashedMac),ms);
                dbProxyService.updatePassengerAssignedMS(ms,hashedMac);
            }
        });

        logger.info("Output: ");
        logger.info("Passenger Assigments: {}", assignmentResponses);
        result.getOutputData().put("passenger_assignments", assignmentResponses);
        result.getOutputData().put("message_body_request", passengerMessageBodyRequests);
        result.getOutputData().put("ms_updates", old2NewMSAssignments);
        logger.info("-----\n");


    }


}
