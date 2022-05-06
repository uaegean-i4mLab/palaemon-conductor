package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

@Configurable
public class GetCrewMembersDetailsTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(GetCrewMembersDetailsTask.class);


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
    public GetCrewMembersDetailsTask(String taskDefName, DBProxyService dbProxyService) {
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
        logger.info("Executing8 {}.", taskDefName);

        TaskResult result = new TaskResult(task);

        result.setStatus(TaskResult.Status.COMPLETED);


        getPassengerDetailsTask(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void getPassengerDetailsTask(Task task, TaskResult result) {
        List<PameasPerson> personList = dbProxyService.getCrewMembers();
//        personList.forEach(pameasPerson -> {
//            logger.info(pameasPerson.toString());
//        });
        logger.info("Running task: " + task.getTaskDefName());
        logger.info("Input: ");
        logger.info("Output: ");
        logger.info("Number of CrewMembers: {}", personList.size());
        logger.info("-----\n");
        //Register the output of the task
        result.getOutputData().put("crew_details",personList);


    }


}
