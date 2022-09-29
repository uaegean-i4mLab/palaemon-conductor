package gr.aegean.palaemon.conductor;

import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;
import gr.aegean.palaemon.conductor.model.TO.EvacuationStatusTO;
import gr.aegean.palaemon.conductor.service.*;
import gr.aegean.palaemon.conductor.tasks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private DBProxyService dbProxyService;

    @Autowired
    private RulesEngineService rulesEngineService;

    @Autowired
    private PassengerMessagingService passengerMessagingService;

    @Autowired
    private CrewMessagingService crewMessagingService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private ConstraintSolverService constraintSolverService;

    @Autowired
    private EvacuationStatusTO evacuationStatusTO;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


    }

    @PostConstruct //this get executed after all beans have been created
    public void listen() {
        Logger logger =
                LoggerFactory.getLogger(Application.class);
        TaskClient taskClient = new TaskClient();
        // The Conductor's API URL
        String CONDUCTOR_URI = !StringUtils.isEmpty(System.getenv("CONDUCTOR_URI")) ?
                System.getenv("CONDUCTOR_URI") : "http://127.0.0.1:8080/api/";
        taskClient.setRootURI(CONDUCTOR_URI);

        // The max. Number of parallel tasks the workers will be executing
        int threadCount = 4;

        // Configuring each Worker to execute all task of the WorkFlow
        Worker updateGeofenceWorker =
                new UpdateGeofenceTask("update_geofence_status", dbProxyService);

        Worker getEvacuationStatusTask =
                new GetEvacuationStatusTask("get_evacuation_status", dbProxyService);

        Worker getPassengerDetailsTask =
                new GetPassengerDetailsTask("get_passenger_details", dbProxyService);

        Worker getGeofencesStatus =
                new GetGeofenceStatusTask("get_geofence_status", dbProxyService);

        Worker getPassengerMSAssignments =
                new GetPassengerMSAssignmentsTask("get_passenger_ms_assignment", rulesEngineService, dbProxyService);

        Worker getMessageBodyRequest =
                new GetMessageBodyTask("get_message_body", rulesEngineService);


        Worker getCrewMembers =
                new GetCrewMembersDetailsTask("get_crew_members", dbProxyService);

        Worker updatedMSMessageTask =
                new SendUpdatedMSMessageTask("send_updated_MS_to_crew", crewMessagingService);

        Worker getMessageObjectTask =
                new GetMessageObjectTask("get_message_object", rulesEngineService);

        Worker makeMessageBodRequest =
                new MakeMessageBodyRequestBasedOnPhaseTask("make_message_body_req");

        Worker updateInPositionTask =
                new UpdateCrewMemberInPositionTask("update_crew_in_position", dbProxyService);

        Worker verifyCrewPositionAndProceed =
                new VerifyAllCrewMemberInPositionAndProceedTask("verify_crew_in_position", dbProxyService, kafkaService);

        Worker sendPassengerNotificationCompleted =
                new SendPassengerNotificationCompletedTask("passenger_notification_completed", kafkaService);

        Worker updatePassengerMSandPath =
                new UpdatePassengersMSandPathTask("update_passenger_ms_and_path", dbProxyService,kafkaService);

        Worker getSinglePassengerDetailsTask =
                new GetSinglePassengerLocationHealthTask("get_single_passenger_location_health", dbProxyService);
        Worker makePassengerIssueTask =
                new MakePassengerIssueTask("make_passenger_issue", dbProxyService, kafkaService, passengerMessagingService);

        Worker callConstraintSolverTask =
                new CallConstraintSolverTask("call_constraint_solver", constraintSolverService, kafkaService);

        Worker crewAssignmentsRequestTask =
                new CrewAssignmentsRequestTask("crew_assignment_request", dbProxyService, crewMessagingService);

        Worker crewAssignmentsAcceptTask =
                new CrewAssignmentsAcceptenceTask("crew_assignment_accept", dbProxyService, passengerMessagingService);

        Worker callCrewMessagingTask =
                new CallCrewMessagingServiceTask("call_crew_messaging_service", crewMessagingService);

        Worker callPassengerMessagingService =
                new CallPassengerMessagingServiceTask("call_messaging_service", passengerMessagingService);

        Worker callBraceletsMessagingService =
                new CallSBMessagingServiceTask("call_bracelets_messaging_service", kafkaService, dbProxyService);


        // Create TaskRunnerConfigurer
        TaskRunnerConfigurer configurer = new TaskRunnerConfigurer.Builder(taskClient,
                Arrays.asList(updateGeofenceWorker,
                        getEvacuationStatusTask,
                        getPassengerDetailsTask,
                        getGeofencesStatus,
                        getPassengerMSAssignments,
                        getMessageBodyRequest,
                        getCrewMembers,
                        updatedMSMessageTask,
                        getMessageObjectTask,
                        makeMessageBodRequest,
                        updateInPositionTask,
                        verifyCrewPositionAndProceed,
                        sendPassengerNotificationCompleted,
                        updatePassengerMSandPath,
                        getSinglePassengerDetailsTask,
                        makePassengerIssueTask,
                        callConstraintSolverTask,
                        crewAssignmentsRequestTask,
                        crewAssignmentsAcceptTask,
                        callCrewMessagingTask,
                        callPassengerMessagingService,
                        callBraceletsMessagingService))
                .withThreadCount(threadCount)
                .build();

        //Start for polling and execution of the tasks
        logger.info("Initiating Worker Manager...");
        // Start the polling and execution of tasks
        configurer.init();

        //initialize the evacuation status
        if (this.evacuationStatusTO == null) {
            this.evacuationStatusTO = new EvacuationStatusTO("1", "0");
        }
    }

}
