package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.pojo.PassengerAssignmentResponse;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Configurable
public class UpdatePassengersMSandPathTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(UpdatePassengersMSandPathTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private DBProxyService dbProxyService;

    private KafkaService kafkaService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public UpdatePassengersMSandPathTask(String taskDefName, DBProxyService dbProxyService, KafkaService kafkaService) {
        this.taskDefName = taskDefName;
        this.dbProxyService = dbProxyService;
        this.kafkaService = kafkaService;
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


        processUpdatePassengerMSandPath(task, result);
        logger.info("-----");

        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void processUpdatePassengerMSandPath(Task task, TaskResult result) {

        logger.info("Running task: " + task.getTaskDefName());
        List<HashMap<String, String>> passengerAssignments = (List<HashMap<String, String>>) task.getInputData().get("passenger_assignments");
        List<PassengerAssignmentResponse> passengerAssignmentResponses =
                passengerAssignments.stream().map(Wrappers::hashmap2PassengerAssignmentResponse).collect(Collectors.toList());

        String[] hashedMacAddresses = new String[passengerAssignmentResponses.size()];
        String[] mStations = new String[passengerAssignmentResponses.size()];

        for (int i=0; i < passengerAssignmentResponses.size();i++){
            hashedMacAddresses[i] = passengerAssignmentResponses.get(i).getHashedMacAddress();
            mStations[i] = passengerAssignmentResponses.get(i).getMusterStation();
        }
        dbProxyService.updatePassengerAssignedMSBulk(mStations,hashedMacAddresses);

//        passengerAssignmentResponses.forEach(passengerAssignmentResponse -> {
//            dbProxyService.updatePassengerAssignedMS(passengerAssignmentResponse.getMusterStation(), passengerAssignmentResponse.getHashedMacAddress());
//            logger.info("Updated Passenger:   {}",
//                    passengerAssignmentResponse.getHashedMacAddress());
//        });

        PameasNotificationTO pameasNotificationTO = new PameasNotificationTO();
        pameasNotificationTO.setType("PASSENGER_INSTRUCTION_COMPLETED");
        kafkaService.writeToPameasNotification(pameasNotificationTO);
        logger.info("--------------");


    }


}
