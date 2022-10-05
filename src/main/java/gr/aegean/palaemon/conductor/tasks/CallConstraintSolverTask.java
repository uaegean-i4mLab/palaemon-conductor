package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.TO.PassengerIncidentSolutionTO;
import gr.aegean.palaemon.conductor.model.pojo.ConstraintSolverIncident;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.ConstraintSolverService;
import gr.aegean.palaemon.conductor.service.KafkaService;
import gr.aegean.palaemon.conductor.utils.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Configurable
public class CallConstraintSolverTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(CallConstraintSolverTask.class);


    /**
     * The task definition name, present in the Workflow Definition.
     */
    private String taskDefName;

    private ConstraintSolverService constraintSolverService;

    private KafkaService kafkaService;

    /**
     * Instantiates a new worker.
     *
     * @param taskDefName the task def name
     */
    public CallConstraintSolverTask(String taskDefName, ConstraintSolverService constraintSolverService, KafkaService kafkaService) {
        this.taskDefName = taskDefName;
        this.constraintSolverService = constraintSolverService;
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


        requestIncidentAssignment(task, result);


        return result;

    }

    /**
     * Process get_starting_params.
     *
     * @param task   the task called from Conductor
     * @param result the result to return to Conductor
     */
    private void requestIncidentAssignment(Task task, TaskResult result) {


        logger.info("Running task: " + task.getTaskDefName());

        ArrayList<Map<String, Object>> incident = (ArrayList<Map<String, Object>>) task.getInputData().get("incidents");

        ArrayList<LinkedHashMap<String, Object>> crewMap =
                (ArrayList<LinkedHashMap<String, Object>>) task.getInputData().get("crew");


        ArrayList<ConstraintSolverIncident> constraintSolverRequestIncidents = new ArrayList<>();
        ArrayList<PameasNotificationTO> pameasNotificationTOs = new ArrayList<>();
        incident.forEach(inc -> {
            constraintSolverRequestIncidents.add(Wrappers.pameasNotificationTO2ConstraintSolverIncident(Wrappers.map2PameasNotificationTO(inc)));
            pameasNotificationTOs.add(Wrappers.map2PameasNotificationTO(inc));
        });

        logger.info("Input: ");
        logger.info("Incidents:   {}", incident);
        logger.info("Output: ");

        ArrayList<PameasPerson> crewMembers = new ArrayList<>();
        crewMap.forEach(crw -> {
            crewMembers.add(Wrappers.hashMap2PameasPerson((LinkedHashMap<String, Object>) crw));
        });

        PassengerIncidentSolutionTO solution = constraintSolverService.makeAssignment(constraintSolverRequestIncidents, crewMembers);
        if(solution != null){
            AtomicInteger i = new AtomicInteger();
            solution.getPassengerIncidentList().forEach(incidentAssignmentTO -> {
                PameasNotificationTO notification = Wrappers.incidentAssignmentTO2PameasNotificationTO(incidentAssignmentTO);
                notification.setHealthIssues(constraintSolverRequestIncidents.get(i.get()).getHealthCondition());
                notification.setMobilityIssues(constraintSolverRequestIncidents.get(i.get()).getMobilityCondition());
                notification.setPregnancyStatus(constraintSolverRequestIncidents.get(i.get()).getPregnancyCondition());
                notification.getIncident().setHealthIssues(constraintSolverRequestIncidents.get(i.get()).getHealthCondition());
                notification.getIncident().setMobilityIssues(constraintSolverRequestIncidents.get(i.get()).getMobilityCondition());
                notification.getIncident().setPregnancyStatus(constraintSolverRequestIncidents.get(i.get()).getPregnancyCondition());
                // TODO Check this works correctly
                notification.setPassengerId(pameasNotificationTOs.get(i.get()).getPassengerId());


                i.getAndIncrement();
                logger.info("-----\n");
                this.kafkaService.writeToPameasNotification(notification);
                logger.info("-----\n");
            });



            logger.info(solution.toString());

            result.getOutputData().put("assignments", solution);
        }else{
            logger.info("NO SOLUTION COULD BE BUILT");
            //TODO maybe here the incident should be visible
            PassengerIncidentSolutionTO errSolution = new PassengerIncidentSolutionTO();
            errSolution.setCrewMemberList(new ArrayList<>());
            errSolution.setPassengerIncidentList(new ArrayList<>());
            result.getOutputData().put("assignments", errSolution);
        }


    }


}
