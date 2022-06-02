package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.RulesEngineService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configurable
public class GetMessageBodyTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(GetMessageBodyTask.class);


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
    public GetMessageBodyTask(String taskDefName, RulesEngineService rulesEngineService) {
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
        logger.info("Executing6 {}.", taskDefName);

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

        LinkedHashMap<String, Object> messageBodyRequest = (LinkedHashMap<String, Object>) task.getInputData().get("message_body_request");

        logger.info("Input: ");
        logger.info("Message Body Request:   {}", messageBodyRequest);

        PassengerMessageBodyRequests mbRequest = Wrappers.hashMap2MessageBodyRequest(messageBodyRequest);

        logger.info("Output: ");
        List<Map<String, String>> messageBodies = rulesEngineService.getMessageBody(mbRequest);
        List<MessageBody> output = messageBodies.stream().map(Wrappers::hashmap2MessageBody).collect(Collectors.toList());
//        logger.info("Passenger Assigments: {}", assignmentResponses);
        result.getOutputData().put("message_bodies", output);
        logger.info("message_bodies: {}", output);
        logger.info("-----\n");
    }


}
