package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.PassengerAssignmentResponse;
import gr.aegean.palaemon.conductor.service.DBProxyService;
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

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public UpdatePassengersMSandPathTask(String taskDefName, DBProxyService dbProxyService) {
        this.taskDefName = taskDefName;
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


        processUpdateGeofenceTask(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void processUpdateGeofenceTask(Task task, TaskResult result) {

        logger.info("Running task: " + task.getTaskDefName());
        List<HashMap<String, String>> passengerAssignements = (List<HashMap<String, String>>) task.getInputData().get("passenger_assignments");
        List<PassengerAssignmentResponse> passengerAssignmentResponses =
                passengerAssignements.stream().map(Wrappers::hashmap2PassengerAssignmentResponse).collect(Collectors.toList());
        passengerAssignmentResponses.forEach(passengerAssignmentResponse -> {
            dbProxyService.updatePassengerAssignedMS(passengerAssignmentResponse.getMusterStation(), passengerAssignmentResponse.getHashedMacAddress());

            logger.info("Updated Passenger:   {}",
                    passengerAssignmentResponse.getHashedMacAddress());
        });
    }


}
