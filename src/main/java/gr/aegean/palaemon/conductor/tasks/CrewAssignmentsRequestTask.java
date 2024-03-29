package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.CrewMessagingService;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.*;

@Configurable
public class CrewAssignmentsRequestTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(CrewAssignmentsRequestTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private DBProxyService dbProxyService;

    private CrewMessagingService crewMessagingService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public CrewAssignmentsRequestTask(String taskDefName, DBProxyService dbProxyService, CrewMessagingService crewMessagingService) {
        this.taskDefName = taskDefName;
        this.dbProxyService = dbProxyService;
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


        processCrewAssignmentAcceptance(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void processCrewAssignmentAcceptance(Task task, TaskResult result) {


        logger.info("Running task: " + task.getTaskDefName());

        ArrayList<Map<String, Object>> assignments = (ArrayList<Map<String, Object>>) task.getInputData().get("assignments");
        logger.info("Input: ");
        logger.info("Incidents:   {}", assignments);
        logger.info("Output: ");

        assignments.forEach(assignment -> {
            PameasNotificationTO notificationTO = Wrappers.map2PameasNotificationTO(assignment);
            Optional<Geofence> incidentGeofence =
                    dbProxyService.getAllGeofences().getSimple().stream().filter(geofence -> {
                        return geofence.getGfName().equals(notificationTO.getGeofence());
                    }).findAny();
            String incidentDeck = "";
            if (incidentGeofence.isPresent()) {
                incidentDeck = incidentGeofence.get().getDeck();
            } else {
                incidentGeofence =
                        dbProxyService.getAllGeofences().getMustering().stream().filter(geofence -> {
                            return geofence.getGfName().equals(notificationTO.getGeofence());
                        }).findAny();
                if (incidentGeofence.isPresent()) {
                    incidentDeck = incidentGeofence.get().getDeck();
                }
            }

            String finalIncidentDeck = incidentDeck;
            Arrays.stream(notificationTO.getCrew()).forEach(crew -> {
                //GET Crew Member details
                Optional<PameasPerson> dbCrewMember = this.dbProxyService.getCrewMembers().stream().filter(person -> {

                    person.getId();
                    return person.getId().equals(crew.getId());
                }).findFirst();
                if (dbCrewMember.isPresent()) {
                    //get hashedMacAddress
                    //send notification to CrewMemeber
                    ArrayList<MessageBody> bodies = new ArrayList<>();
                    MessageBody body = new MessageBody();
                    StringBuilder builder = new StringBuilder();
                    //Passenger needs help! Passenger speaking Italian, is stranded.
                    builder.append("ASSIGNMENT REQUEST. \n")
                            .append("PROCEED To DECK ").append(finalIncidentDeck)
                            .append(" \n")
                            .append("Location: ")
                            .append(notificationTO.getGeofence())
                            .append(" \n");

                    if (notificationTO.getHealthIssues() != null || notificationTO.getMobilityIssues() != null
                            || notificationTO.getPregnancyStatus() != null) {
                        builder.append("Passenger health issues: \n");
                        if (notificationTO.getHealthIssues() != null) {
                            builder.append(notificationTO.getHealthIssues()).append(", \n");
                        }
                        if (notificationTO.getMobilityIssues() != null) {
                            builder.append(notificationTO.getMobilityIssues()).append(", \n");
                        }
                        if (notificationTO.getPregnancyStatus() != null) {
                            builder.append(notificationTO.getPregnancyStatus()).append(", \n");
                        }
                    }

                    builder.append("Passenger Name: ").append(notificationTO.getPassengerName())
                            .append(", ")
                            .append(notificationTO.getPassengerSurname())
                            .append(". \n")
                            .append("Passenger speaks ")
                            .append(notificationTO.getPreferredLanguage()[0]);
                    body.setContent(builder.toString());
                    body.setRecipient(dbCrewMember.get().getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
                    bodies.add(body);
                    crewMessagingService.callSendMessages(bodies);


                }
            });

        });


        logger.info("-----\n");
    }


}
