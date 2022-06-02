package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.Passenger;
import gr.aegean.palaemon.conductor.model.pojo.PassengerMessageBodyRequests;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configurable
public class MakeMessageBodyRequestBasedOnPhaseTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(MakeMessageBodyRequestBasedOnPhaseTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;


    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public MakeMessageBodyRequestBasedOnPhaseTask(String taskDefName) {
        this.taskDefName = taskDefName;

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
        logger.info("Executing11 {}.", taskDefName);

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

        LinkedHashMap<String, String> messageObject = (LinkedHashMap<String, String>) task.getInputData().get("message_object");

        String messageCode = messageObject.get("messageCode");
        String type = messageObject.get("type");
        String sender = messageObject.get("sender");
        String audience = messageObject.get("audience");
        String layout = messageObject.get("layout");
        String deliveryChannel = messageObject.get("deliveryChannel");
        // additional inputs
        Map<String, String> languages = (LinkedHashMap<String, String>) task.getInputData().get("languages");

        List<LinkedHashMap<String, String>> passengerAssignments = (List<LinkedHashMap<String, String>>) task.getInputData().get("passenger_assignments");

        Map<String, String> pathIds = new LinkedHashMap<>();   // LinkedHashMap<String, String>) task.getInputData().get("pathIds");
        Map<String, String> assignedMSs = new LinkedHashMap<>(); //(LinkedHashMap<String, String>) task.getInputData().get("assignedMSs");
        Map<String, String> actions = new LinkedHashMap<>();
        if (passengerAssignments != null) {
            passengerAssignments.forEach(passengerAssignmentResponse -> {
                pathIds.put(passengerAssignmentResponse.get("hashedMacAddress"), passengerAssignmentResponse.get("pathId"));
                actions.put(passengerAssignmentResponse.get("hashedMacAddress"), passengerAssignmentResponse.get("action"));
                assignedMSs.put(passengerAssignmentResponse.get("hashedMacAddress"), passengerAssignmentResponse.get("musterStation"));
            });
        }


        List<String> blocked = (List<String>) task.getInputData().get("blocked");
        // fill the request object
        PassengerMessageBodyRequests passengerMessageBodyRequests = new PassengerMessageBodyRequests();
        passengerMessageBodyRequests.setPassengerLanguages(languages);
        passengerMessageBodyRequests.setActions(actions);
        passengerMessageBodyRequests.setAssignedPathIDs(pathIds);
        passengerMessageBodyRequests.setMusterStation(assignedMSs);

        Map<String, String> messageCodes = new HashMap<>();
        if (audience.toLowerCase().equals("crew")) {
            Map<String, String> crewLanguages = new HashMap<>();
            Map<String, String> crewActions = new HashMap<>();
            Map<String, String> crewPathIds = new HashMap<>();
            Map<String, String> crewAssignedMSs = new HashMap<>();

            List<LinkedHashMap> crewMembers = (List<LinkedHashMap>) task.getInputData().get("crew_details");
            List<Passenger> crewMembersList = crewMembers.stream().map(Wrappers::hashMap2PameasPerson).map(Wrappers::paemasPerson2Passenger).
                    collect(Collectors.toList());
            crewMembersList.stream().forEach(crewMember -> {
                messageCodes.put(crewMember.getHashedMacAddress(), messageCode);
                crewLanguages.put(crewMember.getHashedMacAddress(), "en");
                crewActions.put(crewMember.getHashedMacAddress(), "");
                crewPathIds.put(crewMember.getHashedMacAddress(), "");
                crewAssignedMSs.put(crewMember.getHashedMacAddress(), "");
            });

            passengerMessageBodyRequests.setPassengerLanguages(crewLanguages);
            passengerMessageBodyRequests.setActions(crewActions);
            passengerMessageBodyRequests.setMusterStation(crewAssignedMSs);
            passengerMessageBodyRequests.setAssignedPathIDs(crewPathIds);
        }
        if (audience.toLowerCase().equals("passengers")) {
            Map<String, String> passengerLanguages = new HashMap<>();
            List<LinkedHashMap> passenger_details = (List<LinkedHashMap>) task.getInputData().get("passenger_details");

            List<Passenger> passengerList = passenger_details.stream().map(Wrappers::hashMap2PameasPerson).map(Wrappers::paemasPerson2Passenger).
                    collect(Collectors.toList());

            passenger_details.stream().map(Wrappers::hashMap2PameasPerson).forEach(pameasPerson -> {
                pameasPerson.getPersonalInfo().getPreferredLanguage().forEach(s -> {
                    passengerLanguages.put(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress(), s);
                });
            });


            passengerMessageBodyRequests.setPassengerLanguages(passengerLanguages);
            if (messageCode.equals("5.1") || messageCode.equals("6.2")) {
                passengerMessageBodyRequests.setActions(new HashMap<>());
                passengerMessageBodyRequests.setMusterStation(new HashMap<>());
                passengerMessageBodyRequests.setAssignedPathIDs(new HashMap<>());
            }

            passengerList.stream().forEach(passenger -> {
                messageCodes.put(passenger.getHashedMacAddress(), messageCode);
                if (messageCode.equals("5.1") || messageCode.equals("6.2")) {
                    passengerMessageBodyRequests.getActions().put(passenger.getHashedMacAddress(), "");
                    passengerMessageBodyRequests.getMusterStation().put(passenger.getHashedMacAddress(), "");
                    passengerMessageBodyRequests.getAssignedPathIDs().put(passenger.getHashedMacAddress(), "");
                }
            });
        }


        logger.info("Input: ");
        logger.info("MessageObject :{}", messageObject);


        passengerMessageBodyRequests.setMessageCodes(messageCodes);
        passengerMessageBodyRequests.setBlockedGeofences(blocked);

        logger.info("Output: ");
        logger.info("PassengerMessageBodyRequests {}", passengerMessageBodyRequests);

        result.getOutputData().put("message_body_request", passengerMessageBodyRequests);
        logger.info("-----\n");
    }


}
