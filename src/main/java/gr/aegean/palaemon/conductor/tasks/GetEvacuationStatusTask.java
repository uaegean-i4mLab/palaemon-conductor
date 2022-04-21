package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.Geofence;
import gr.aegean.palaemon.conductor.model.pojo.ShipsGeofences;
import gr.aegean.palaemon.conductor.service.AccessTokenService;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Configurable
public class GetEvacuationStatusTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(GetEvacuationStatusTask.class);


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
    public GetEvacuationStatusTask(String taskDefName, DBProxyService dbProxyService) {
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
        logger.info("Executing2 {}.", taskDefName);

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

        String res =  dbProxyService.getEvacuationStatus();
        logger.info("Running task: " + task.getTaskDefName());
        logger.info("Input: ");
        logger.info("Output: ");
        logger.info("EvacuationStatus: {}",res);
        logger.info("-----\n");
        //Register the output of the task
        //TODO

        result.getOutputData().put("evacuation_status", res);


    }


}
