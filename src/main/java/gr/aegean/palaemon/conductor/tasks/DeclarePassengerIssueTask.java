package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.TO.IncidentTO;
import gr.aegean.palaemon.conductor.model.TO.NotificationIncidentTO;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.pojo.Incident;
import gr.aegean.palaemon.conductor.model.pojo.MessageBody;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.service.PassengerMessagingService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Configurable
public class DeclarePassengerIssueTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(DeclarePassengerIssueTask.class);
    private PassengerMessagingService passengerMessagingService;

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
    public DeclarePassengerIssueTask(String taskDefName, DBProxyService dbProxyService, KafkaService kafkaService, PassengerMessagingService passengerMessagingService) {
        this.taskDefName = taskDefName;
        this.dbProxyService = dbProxyService;
        this.kafkaService= kafkaService;
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
        logger.info("Executing3 {}.", taskDefName);

        TaskResult result = new TaskResult(task);

        result.setStatus(TaskResult.Status.COMPLETED);


        declarePassengerIssueTask(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void declarePassengerIssueTask(Task task, TaskResult result) {
        String geofence = (String) task.getInputData().get("geofence");
        String healthIssues = (String) task.getInputData().get("healthIssues");
        String name = (String) task.getInputData().get("name");
        String surname = (String) task.getInputData().get("surname");
        String mobilityIssues = (String) task.getInputData().get("mobilityIssues");
        String pregnancyIssues = (String) task.getInputData().get("pregnancyIssues");
        String xLoc = (String) task.getInputData().get("xLoc");
        String yLoc = (String) task.getInputData().get("yLoc");
        String deck = (String) task.getInputData().get("deck");
        String hashedMacAddress = (String) task.getInputData().get("passenger_id");
        String passengerId = (String) task.getInputData().get("passengerId");


        logger.info("Running task: " + task.getTaskDefName());
        logger.info("Input: ");
        logger.info("Output: ");

        IncidentTO incidentTO = new IncidentTO();
        incidentTO.setHealthIssues(healthIssues);
        incidentTO.setMobilityIssues(mobilityIssues);
        incidentTO.setGeofence(geofence);
        incidentTO.setPassengerName(name);
        incidentTO.setTimestamp( new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        incidentTO.setPassengerSurname(surname);
        incidentTO.setPregnancyStatus(pregnancyIssues);

        String id = UUID.randomUUID().toString();
        incidentTO.setId(id);
        incidentTO.setStatus(Incident.IncidentStatus.OPEN);
        //TODO this should not only be "en"
        incidentTO.setPreferredLanguage(new String[]{"en"});
        incidentTO.setXLoc(xLoc);
        incidentTO.setYLoc(yLoc);
        incidentTO.setAssignedCrewMemberId(null);
        incidentTO.setDeck(deck);
        incidentTO.setIncidentId(id);

        dbProxyService.declarePassengerIncident(incidentTO);

        NotificationIncidentTO notificationIncidentTO = Wrappers.notificationTo2NotificationIncidientTO(incidentTO);
        PameasNotificationTO pameasNotificationTO = new PameasNotificationTO();
        pameasNotificationTO.setType("PASSENGER_ISSUE");
        pameasNotificationTO.setHealthIssues(incidentTO.getHealthIssues());
        pameasNotificationTO.setTimestamp(incidentTO.getTimestamp());
        pameasNotificationTO.setId(incidentTO.getId());
        pameasNotificationTO.setPassengerName(incidentTO.getPassengerName());
        pameasNotificationTO.setPassengerSurname(incidentTO.getPassengerSurname());
        pameasNotificationTO.setPassengerId(passengerId);

        pameasNotificationTO.setStatus(notificationIncidentTO.getStatus());
        pameasNotificationTO.setMobilityIssues(notificationIncidentTO.getMobilityIssues());
        pameasNotificationTO.setAssignedCrewMemberId( null);
        pameasNotificationTO.setCrew(null);
        pameasNotificationTO.setGeofence(incidentTO.getGeofence());
        pameasNotificationTO.setPregnancyStatus(incidentTO.getPregnancyStatus());
        pameasNotificationTO.setPreferredLanguage(incidentTO.getPreferredLanguage());
        pameasNotificationTO.setPreferredLanguage(incidentTO.getPreferredLanguage());
        pameasNotificationTO.setMacAddress(hashedMacAddress);
        pameasNotificationTO.setIncident(notificationIncidentTO);
        pameasNotificationTO.setXloc(incidentTO.getXLoc());
        pameasNotificationTO.setYloc(incidentTO.getYLoc());
        pameasNotificationTO.setStatus(incidentTO.getStatus().toString());
        pameasNotificationTO.setPreferredLanguage(incidentTO.getPreferredLanguage());


        kafkaService.writeToPameasNotification(pameasNotificationTO);


        String messageToPassenger =
        "<header></header><main><h2 style='color: red; text-align: center;'>Notification</h2>" +
                "<div style='font-size: x-large;'><b> Please stay where you are and do not make any attempt to move</b>." +
                " Assistance is on its way and will arrive shortly.</div>"+
                "</main>";


        ArrayList<MessageBody> bodies = new ArrayList<>();
        MessageBody body = new MessageBody();
        body.setContent(messageToPassenger);
        body.setRecipient(hashedMacAddress);
        bodies.add(body);
        this.passengerMessagingService.callSendMessages(bodies);

    }
}
