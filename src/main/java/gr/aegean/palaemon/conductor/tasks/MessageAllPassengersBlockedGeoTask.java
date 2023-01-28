package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.MessageBody;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.PassengerMessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MessageAllPassengersBlockedGeoTask implements Worker {

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

    private PassengerMessagingService passengerMessagingService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public MessageAllPassengersBlockedGeoTask(String taskDefName, DBProxyService dbProxyService, PassengerMessagingService passengerMessagingService) {
        this.taskDefName = taskDefName;
        this.dbProxyService = dbProxyService;
        this.passengerMessagingService = passengerMessagingService;
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


        MessageAllPassengerTask(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void MessageAllPassengerTask(Task task, TaskResult result) {
        List<PameasPerson> personList = dbProxyService.getPassengerDetails();
        logger.info("Running task: " + task.getTaskDefName());
        String geofence = (String) task.getInputData().get("geofence");

        List<MessageBody> bodies  =
        personList.stream().map(pameasPerson -> {
            MessageBody body = new MessageBody();
            body.setContent( "<header></header><main><h2 style='color: red; text-align: center;'>Notification</h2>" +
                    "<div style='font-size: x-large;'><b> Attention!! Avoid passage from " + geofence +
                    " .The area is not safe! </b></div>"+
                    "</main>:: sound: siren");
            body.setRecipient(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
            return  body;
        }).collect(Collectors.toList());
        this.passengerMessagingService.callSendMessages(bodies);


    }


}
