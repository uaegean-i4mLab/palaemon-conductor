package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.TO.SbPaMEASMessageTO;
import gr.aegean.palaemon.conductor.model.pojo.MessageBody;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configurable
public class CallSBMessagingServiceTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(CallSBMessagingServiceTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private KafkaService kafkaService;
    private DBProxyService dbProxyService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public CallSBMessagingServiceTask(String taskDefName, KafkaService kafkaService, DBProxyService dbProxyService) {
        this.taskDefName = taskDefName;
        this.kafkaService = kafkaService;
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
        logger.info("Executing {}.", taskDefName);

        TaskResult result = new TaskResult(task);

        result.setStatus(TaskResult.Status.COMPLETED);


        callSBMessagingService(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void callSBMessagingService(Task task, TaskResult result) {


        logger.info("Running task: " + task.getTaskDefName());

        ArrayList<Map<String, String>> messageBodies = (ArrayList<Map<String, String>>) task.getInputData().get("message_bodies");

        logger.info("Input: ");
        logger.info("Message Bodies:   {}", messageBodies);
        List<MessageBody> parsedBodies = messageBodies.stream().map(Wrappers::hashmap2MessageBody).collect(Collectors.toList());

        List<PameasPerson> passengers = dbProxyService.getPassengerDetails();
        List<PameasPerson> crewMembers = dbProxyService.getCrewMembers();
        List<PameasPerson> users = Stream.concat(passengers.stream(), crewMembers.stream()).collect(Collectors.toList());

        parsedBodies.stream().forEach(messageBody -> {
            Optional<PameasPerson> p = getPersonFromHashMac(messageBody.getHashedMacAddress(), users);
            if (p.isPresent()) {
                String braceletId = getSBFromPerson(p.get());
                SbPaMEASMessageTO measMessageTO = new SbPaMEASMessageTO();
                measMessageTO.setId(braceletId);
                measMessageTO.setOriginator("evacuation-coordinator");
                measMessageTO.setMsg(messageBody.getContent());
                //TODO fix this to make sure proper message types are used
                measMessageTO.setMessageType("notification");

                kafkaService.writeToSBMessageTO(measMessageTO);
            }

        });

        logger.info("Output: ");

//        result.getOutputData().put("message_bodies", output);
        logger.info("-----\n");
    }

    private String getSBFromPerson(PameasPerson p) {
        if (p.getNetworkInfo() != null)
            return p.getNetworkInfo().getBraceletId();
        return null;
    }

    private Optional<PameasPerson> getPersonFromHashMac(String hashedMac, List<PameasPerson> persons) {
        return persons.stream().filter(pameasPerson ->
                pameasPerson.getNetworkInfo().getDeviceInfoList() != null && pameasPerson.getNetworkInfo().getDeviceInfoList().size() > 0 &&
                        pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress().equals(hashedMac)).findFirst();
    }

}
