package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.MessageBody;
import gr.aegean.palaemon.conductor.model.pojo.Passenger;
import gr.aegean.palaemon.conductor.service.CrewMessagingService;
import gr.aegean.palaemon.conductor.service.PassengerMessagingService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Configurable
public class SendUpdatedMSMessageTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(SendUpdatedMSMessageTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private CrewMessagingService crewMessagingService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public SendUpdatedMSMessageTask(String taskDefName, CrewMessagingService crewMessagingService) {
        this.taskDefName = taskDefName;
        this.crewMessagingService = crewMessagingService;
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
        logger.info("Executing9 {}.", taskDefName);

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

        LinkedHashMap<String, Object> msUpdatesMap = (LinkedHashMap<String, Object>) task.getInputData().get("ms_updates");
        List<LinkedHashMap> crewMembersMap = (List<LinkedHashMap>) task.getInputData().get("crew_details");

        List<Passenger> crewMembers = crewMembersMap.stream().map(Wrappers::hashMap2PameasPerson).map(Wrappers::paemasPerson2Passenger).
                collect(Collectors.toList());

        logger.info("Input: ");
        logger.info("MusterStation updates:   {}", msUpdatesMap);
        logger.info("Crew Members updates:   {}", crewMembersMap);

//        PassengerMessageBodyRequests mbRequest = Wrappers.hashMap2MessageBodyRequest(messageBodyRequest);
        if (msUpdatesMap.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Passengers from ");
            msUpdatesMap.forEach((key, value) -> {
                sb.append("Muster Station ").append(key);
                sb.append(" are redirected to ").append((String) value);
            });
            String messageTxt = sb.toString();
            List<MessageBody> messageBodies = new ArrayList<>();
            crewMembers.forEach(crewMember -> {
                MessageBody mb = new MessageBody();
                mb.setContent(messageTxt);
                mb.setHashedMacAddress(crewMember.getHashedMacAddress());
                messageBodies.add(mb);
            });
            crewMessagingService.callSendMessages(messageBodies);
        }


        logger.info("Output: ");
//        List<Map<String, String>> messageBodies = rulesEngineService.getMessageBody(mbRequest);
//        List<MessageBody> output = messageBodies.stream().map(Wrappers::hashmap2MessageBody).collect(Collectors.toList());
//        result.getOutputData().put("message_bodies", output);
//        logger.info("message_bodies: {}", output);
        logger.info("-----\n");
    }


}
