package gr.aegean.palaemon.conductor.tasks;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Optional;

@Configurable
public class GetSinglePassengerLocationHealthTask implements Worker {

    /**
     * The logger.
     */
    private final Logger logger =
            LoggerFactory.getLogger(GetSinglePassengerLocationHealthTask.class);


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
    public GetSinglePassengerLocationHealthTask(String taskDefName, DBProxyService dbProxyService) {
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
        logger.info("Executing3 {}.", taskDefName);

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
        String hashedMacAddress = (String) task.getInputData().get("passenger_id");

        Optional<PameasPerson> passenger = dbProxyService.getSinglePassengerDetails(hashedMacAddress);

        logger.info("Running task: " + task.getTaskDefName());
        logger.info("Input: ");
        logger.info("Output: ");

        if (passenger.isPresent()) {
//            logger.info("Passenger: {}", passenger.get().getPersonalInfo().getPersonalId());
//            logger.info("-----\n");
            if (passenger.get().getLocationInfo().getGeofenceHistory() != null && passenger.get().getLocationInfo().getGeofenceHistory().size() > 0) {
                String geofence = passenger.get().getLocationInfo().getGeofenceHistory()
                        .get(passenger.get().getLocationInfo().getGeofenceHistory().size() - 1)
                        .getGfName();
                String healthIssues = passenger.get().getPersonalInfo().getMedicalCondition();
                String name = passenger.get().getPersonalInfo().getName();
                String surname = passenger.get().getPersonalInfo().getSurname();
                String mobilityIssues = passenger.get().getPersonalInfo().getMobilityIssues();
                String pregnancyIssues = passenger.get().getPersonalInfo().getPrengencyData();

                if (passenger.get().getLocationInfo().getLocationHistory() != null && passenger.get().getLocationInfo().getLocationHistory().size() > 0) {
                    String deck =  passenger.get().getLocationInfo().getGeofenceHistory()
                            .get(passenger.get().getLocationInfo().getGeofenceHistory().size() - 1).getDeck();
                    String xLoc = passenger.get().getLocationInfo().getLocationHistory()
                            .get(passenger.get().getLocationInfo().getLocationHistory().size() - 1).getXLocation();
                    String yLoc = passenger.get().getLocationInfo().getLocationHistory()
                            .get(passenger.get().getLocationInfo().getLocationHistory().size() - 1).getYLocation();
                    result.getOutputData().put("xLoc", xLoc);
                    result.getOutputData().put("yLoc", yLoc);
                    result.getOutputData().put("deck", deck);

                }
                //Register the output of the task
                result.getOutputData().put("geofence", geofence);
                result.getOutputData().put("healthIssues", healthIssues);
                result.getOutputData().put("name", name);
                result.getOutputData().put("surname", surname);
                result.getOutputData().put("mobilityIssues", mobilityIssues);
                result.getOutputData().put("pregnancyIssues", pregnancyIssues);

            }


        }


    }
}
