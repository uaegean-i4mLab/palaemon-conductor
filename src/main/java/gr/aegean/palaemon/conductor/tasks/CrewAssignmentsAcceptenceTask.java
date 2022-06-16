package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.TO.IncidentTO;
import gr.aegean.palaemon.conductor.model.TO.NotificationIncidentCrewTO;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.MessagingServiceCaller;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.*;
import java.util.stream.Collectors;

@Configurable
public class CrewAssignmentsAcceptenceTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(CrewAssignmentsAcceptenceTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private DBProxyService dbProxyService;

    private MessagingServiceCaller messagingServiceCaller;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public CrewAssignmentsAcceptenceTask(String taskDefName, DBProxyService dbProxyService, MessagingServiceCaller messagingServiceCaller) {
        this.taskDefName = taskDefName;
        this.dbProxyService = dbProxyService;
        this.messagingServiceCaller = messagingServiceCaller;
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

        LinkedHashMap<String, Object> assignmentAccepted = (LinkedHashMap<String, Object>) task.getInputData().get("assignment");
        logger.info("Input: ");
        logger.info("Incidents:   {}", assignmentAccepted);
        logger.info("Output: ");

        PameasNotificationTO notificationTO = Wrappers.map2PameasNotificationTO(assignmentAccepted);
        Optional<IncidentTO> incidentTO = dbProxyService.getIncidentFromId(notificationTO.getId());
        if (incidentTO.isPresent()) {
            ArrayList<String> assignedCrewMemberId = (ArrayList<String>) Arrays.stream(notificationTO.getCrew()).map(NotificationIncidentCrewTO::getId).collect(Collectors.toList());
            // update the profiles of all crew members to ASSIGNED
            assignedCrewMemberId.forEach(id -> {
                dbProxyService.getCrewMembers().stream().filter(pameasPerson -> pameasPerson.getId().equals(id)).findFirst().ifPresent(pameasPerson -> {

                    dbProxyService.updateCrewMemberStatus(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress(),
                            pameasPerson.getPersonalInfo().getPersonalId(), Personalinfo.AssignmentStatus.ASSIGNED);
                });
            });
            //update the incident
            incidentTO.get().setStatus(Incident.IncidentStatus.ASSIGNED);
            dbProxyService.updatePassengerIncident(incidentTO.get());
        } else {
            logger.error("no incident found with id " + notificationTO.getId());
        }



        logger.info("-----\n");
    }


}
