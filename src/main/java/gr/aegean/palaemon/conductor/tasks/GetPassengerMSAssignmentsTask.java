package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.RulesEngineService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Configurable
@Slf4j
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


//        logger.info("Input: ");
//        logger.info("Geofence(s) Param:   {}", geofences);
//        logger.info("Passenger Details Param:   {}", passengers);

        List<Passenger> passengerList = passengers.stream().map(Wrappers::hashMap2PameasPerson).map(Wrappers::paemasPerson2Passenger).
                collect(Collectors.toList());
        List<Geofence> geofenceList = geofences.stream().map(Wrappers::hashMap2Geofence).collect(Collectors.toList());
        List<String> blocked = geofenceList.stream().filter(geofence -> geofence.getStatus().toUpperCase().equals("BLOCKED"))
                .map(Geofence::getGfName).collect(Collectors.toList());
        List<MusterStation> musterStations = geofenceList.stream().filter(geofence -> geofence.getMusteringStation().equals("true"))
                .map(Wrappers::geofences2MusterStation).collect(Collectors.toList());

        List<PassengerAssignmentResponse> assignmentResponses =
                rulesEngineService.fetchPassengerMSAssignments(passengerList, blocked, musterStations);

        LinkedHashMap<String, String> actions = new LinkedHashMap<>();
        LinkedHashMap<String, String> pathIds = new LinkedHashMap<>();
        LinkedHashMap<String, String> assignedMSs = new LinkedHashMap<>();

        List<String> macAddressesToSkipMessagesTo = new ArrayList<>();

        assignmentResponses.forEach(passengerAssignmentResponse -> {
            if (passengerAssignmentResponse.getHashedMacAddress() != null) {
                if (passengerAssignmentResponse.getAction() == null || passengerAssignmentResponse.getPathId() == null
                        || passengerAssignmentResponse.getMusterStation() == null) {
                    log.error("error getting muster station assignment for passenger {}", passengerAssignmentResponse.getHashedMacAddress());
                } else {
                    String action = passengerAssignmentResponse.getAction();
                    String hashedMac = passengerAssignmentResponse.getHashedMacAddress();
                    actions.put(hashedMac, action);
                    String pathId = passengerAssignmentResponse.getPathId();
                    pathIds.put(hashedMac, pathId);
                    String ms = passengerAssignmentResponse.getMusterStation();
                    assignedMSs.put(hashedMac, ms);
                    if(action.equals("WAIT")) macAddressesToSkipMessagesTo.add(hashedMac);
                }
            }
        });


        //Prepare the request for the messageBody call
        PassengerMessageBodyRequests passengerMessageBodyRequests = new PassengerMessageBodyRequests();
        LinkedHashMap<String, String> languages = new LinkedHashMap<>();
        LinkedHashMap<String, String> messageCodes = new LinkedHashMap<>();
        LinkedHashMap<String, String> currentGeofenceList = new LinkedHashMap<>();
        passengers.stream().map(Wrappers::hashMap2PameasPerson).forEach(passenger -> {
            if (passenger.getLocationInfo().getGeofenceHistory() != null && passenger.getLocationInfo().getGeofenceHistory().size() > 0) {
                String hashedMac = passenger.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress();
                String language = passenger.getPersonalInfo().getPreferredLanguage().get(0);
                languages.put(hashedMac, language);
                messageCodes.put(hashedMac, messageCode);
                currentGeofenceList.put(hashedMac, passenger.getLocationInfo().getGeofenceHistory().get(passenger.getLocationInfo().getGeofenceHistory().size() - 1).getGfName());
            }

        });






        passengerMessageBodyRequests.setPassengerLanguages(languages);
        passengerMessageBodyRequests.setActions(actions);
        passengerMessageBodyRequests.setMessageCodes(messageCodes);
        passengerMessageBodyRequests.setAssignedPathIDs(pathIds);
        passengerMessageBodyRequests.setBlockedGeofences(blocked);
        passengerMessageBodyRequests.setMusterStation(assignedMSs);
        passengerMessageBodyRequests.setCurrentGeofences(currentGeofenceList);

        //do not send message generations for passengers with mobility issues
        macAddressesToSkipMessagesTo.forEach(hashedMAc ->{
            passengerMessageBodyRequests.getPassengerLanguages().remove(hashedMAc);
            passengerMessageBodyRequests.getActions().remove(hashedMAc);
            passengerMessageBodyRequests.getMessageCodes().remove(hashedMAc);
            passengerMessageBodyRequests.getAssignedPathIDs().remove(hashedMAc);
            passengerMessageBodyRequests.getBlockedGeofences().remove(hashedMAc);
            passengerMessageBodyRequests.getMusterStation().remove(hashedMAc);
            passengerMessageBodyRequests.getCurrentGeofences().remove(hashedMAc);
        });



        // Store original assignments
        Map<String, String> originalAssignments = new HashMap<>();
        Map<String, String> old2NewMSAssignments = new HashMap<>();
        //calculate original passenger MSs
        passengers.stream().map(Wrappers::hashMap2PameasPerson).forEach(pameasPerson -> {
            if (!StringUtils.isEmpty(pameasPerson.getPersonalInfo().getAssignedMusteringStation())) {
                originalAssignments.put(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress(),
                        pameasPerson.getPersonalInfo().getAssignedMusteringStation());
            }
        });
        //

        AtomicBoolean isPathUpdated = new AtomicBoolean(false);
        List<String> hashedMacAddresses = new ArrayList<>();
        List<String> mStations = new ArrayList<>();
        //check updated assignments and //compare and propagate results // and store them in the DB
        assignmentResponses.forEach(passengerAssignmentResponse -> {
            String hashedMac = passengerAssignmentResponse.getHashedMacAddress();
            String ms = passengerAssignmentResponse.getMusterStation();



            if (originalAssignments.get(hashedMac) != null && !originalAssignments.get(hashedMac).equals(ms)) {
                isPathUpdated.set(true);
                old2NewMSAssignments.put(originalAssignments.get(hashedMac), ms);
                //dbProxyService.updatePassengerAssignedMS(ms, hashedMac);
                hashedMacAddresses.add(hashedMac);
                mStations.add(ms);
            }
        });
        if(mStations.size() > 0){
            String[] mStationsArr = new String[mStations.size()];
            String[] hashedMacAddressesArr = new String[hashedMacAddresses.size()];
            dbProxyService.updatePassengerAssignedMSBulk(mStations.toArray(mStationsArr),hashedMacAddresses.toArray(hashedMacAddressesArr));
        }


        logger.info("Output: ");
        logger.info("Passenger Assigments: {}", assignmentResponses);
        result.getOutputData().put("passenger_assignments", assignmentResponses);
        result.getOutputData().put("message_body_request", passengerMessageBodyRequests);
        result.getOutputData().put("ms_updates", old2NewMSAssignments);
        result.getOutputData().put("is_path_update",isPathUpdated.get());
        logger.info("-----\n");


    }


}
