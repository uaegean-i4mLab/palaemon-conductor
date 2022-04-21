package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.MessageBody;
import gr.aegean.palaemon.conductor.model.pojo.MessageObject;
import gr.aegean.palaemon.conductor.model.pojo.PassengerMessageBodyRequests;
import gr.aegean.palaemon.conductor.service.RulesEngineService;
import gr.aegean.palaemon.conductor.service.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configurable
public class GetMessageObjectTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(GetMessageObjectTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private RulesEngineService rulesEngineService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public GetMessageObjectTask(String taskDefName, RulesEngineService rulesEngineService) {
        this.taskDefName = taskDefName;
        this.rulesEngineService = rulesEngineService;
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
        logger.info("Executing10 {}.", taskDefName);

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

        String phase = (String) task.getInputData().get("phase");
        String taskId = (String) task.getInputData().get("task_id");

        logger.info("Input: ");
        logger.info("Pameas Phase :-{}-", phase);
        logger.info("Task Id :-{}-", taskId);


        logger.info("Output: ");
        MessageObject messageObject = rulesEngineService.getMessageObject(phase,taskId);

//        logger.info("Passenger Assigments: {}", assignmentResponses);
        result.getOutputData().put("message_object", messageObject);
        result.getOutputData().put("message_object_code", messageObject.getMessageCode());
        //logger.info("message_bodies: {}", output);
        logger.info("-----\n");
    }


}
