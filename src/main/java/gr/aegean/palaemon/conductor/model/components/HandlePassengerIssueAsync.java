package gr.aegean.palaemon.conductor.model.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.ProposeAssignmentRequestTO;
import gr.aegean.palaemon.conductor.model.pojo.ConstraintSolverIncident;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.*;


@Component
@Slf4j
public class HandlePassengerIssueAsync {

    ArrayList<ConstraintSolverIncident> incidentArrayList;
    private boolean isRunning;

    private final ScheduledExecutorService executorService;

    @Autowired
    private TaskScheduler taskScheduler;


    public HandlePassengerIssueAsync() {
        this.incidentArrayList = new ArrayList<>();
        this.isRunning = false;
       this.executorService = Executors
                .newSingleThreadScheduledExecutor();    }

    public void addIncident(ConstraintSolverIncident incident) {
        log.info("adding incident {}" , this.incidentArrayList.size() +1);

        this.incidentArrayList.add(incident);
        if (!isRunning) {
            this.isRunning = true;
            Callable<String> callableTask = this::handleIssues;
            Future<String> resultFuture =
                    executorService.schedule(callableTask, 20, TimeUnit.SECONDS);
        }
    }

//    @Async
    public String handleIssues() {
        String conductorUrl = System.getenv("CONDUCTOR_URI");
        ObjectMapper mapper = new ObjectMapper();
//        try {
//            log.info("will sleep for 10 sec");
//            Thread.sleep(20000);    // Let me sleep for 10 sec, to wait for other passenger incidents
//            log.info("ok wake up now...");
//            log.info("i have found {}} incidents while sleeping", incidentArrayList.size());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.started = true;
        ProposeAssignmentRequestTO requestTO = new ProposeAssignmentRequestTO(this.incidentArrayList);

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(conductorUrl + "workflow/porpose_assignment?priority=0"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestTO)))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("made a call to {} porpose_assignment", conductorUrl);
            log.info("response {}", response.body());
            this.isRunning
                    = false;
            this.incidentArrayList = new ArrayList<>();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return "error";

        }

        return  "OK";

    }
}