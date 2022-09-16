package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.MessageBody;
import gr.aegean.palaemon.conductor.service.CrewMessagingService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configurable
public class CallCrewMessagingServiceTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(CallCrewMessagingServiceTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private final CrewMessagingService crewMessagingService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public CallCrewMessagingServiceTask(String taskDefName, CrewMessagingService crewMessagingService) {
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
        logger.info("Executing {}.", taskDefName);

        TaskResult result = new TaskResult(task);

        result.setStatus(TaskResult.Status.COMPLETED);


        callMessagingService(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void callMessagingService(Task task, TaskResult result) {


        logger.info("Running task: " + task.getTaskDefName());

        ArrayList<Map<String, String>> messageBodies = (ArrayList<Map<String, String>>) task.getInputData().get("message_bodies");

        logger.info("Input: ");
        logger.info("Message Bodies:   {}", messageBodies);
        List<MessageBody> parsedBodies = messageBodies.stream().map(Wrappers::hashmap2MessageBody).collect(Collectors.toList());
        crewMessagingService.callSendMessages(parsedBodies);

        logger.info("Output: ");

//        result.getOutputData().put("message_bodies", output);
        logger.info("-----\n");
    }


}
