package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Configurable
public class SendPassengerNotificationCompletedTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(SendPassengerNotificationCompletedTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private KafkaService kafkaService;


    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public SendPassengerNotificationCompletedTask(String taskDefName, KafkaService kafkaService) {
        this.taskDefName = taskDefName;

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
        logger.info("Input: ");


//            dbProxyService.updateCrewInPosition(hashMacAddress, Boolean.parseBoolean(inPosition));
        logger.info("Will notify Kafka Passenger notification completed");
        PameasNotificationTO pameasNotification = new PameasNotificationTO();
        pameasNotification.setStatus("");
        pameasNotification.setId(UUID.randomUUID().toString());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        pameasNotification.setTimestamp(timestamp.toString());
        pameasNotification.setType("PASSENGER_ALERT_COMPLETED");
        //TODO
        //kafkaService.writeToPameasNotification(pameasNotification);

        logger.info("-----\n");

    }


}
