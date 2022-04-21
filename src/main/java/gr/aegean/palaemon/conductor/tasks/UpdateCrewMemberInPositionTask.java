package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Optional;

@Configurable
public class UpdateCrewMemberInPositionTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(UpdateCrewMemberInPositionTask.class);


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
    public UpdateCrewMemberInPositionTask(String taskDefName, DBProxyService dbProxyService) {
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

//        String confStarter = (String) task.getInputData().get("start_id") + task.getTaskDefName();

        String hashMacAddress = (String) task.getInputData().get("hashMacAddress");
        String inPosition = (String) task.getInputData().get("in_position");
        Optional<PameasPerson> crewMemberOpt =
                dbProxyService.getCrewMembers().stream().filter(pameasPerson ->
                        pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress().equals(hashMacAddress)
                ).findFirst();

        logger.info("Running task: " + task.getTaskDefName());
        logger.info("Input: ");
        logger.info("hashMacAddress Param:   {}",
                (String) task.getInputData().get("hashMacAddress"));
        logger.info("in_position Param:   {}",
                (String) task.getInputData().get("in_position"));

        if (crewMemberOpt.isPresent()) {
            dbProxyService.updateCrewInPosition(hashMacAddress, Boolean.parseBoolean(inPosition));
            logger.info("Updated Crew Member {} inPosition Status to: {}", hashMacAddress,
                    (String) task.getInputData().get("in_position"));
            logger.info("-----\n");
        }
    }


}
