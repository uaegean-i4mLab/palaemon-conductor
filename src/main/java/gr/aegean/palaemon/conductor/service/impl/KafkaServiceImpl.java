package gr.aegean.palaemon.conductor.service.impl;

import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaServiceImpl implements KafkaService {

    static final String NOTIFICATION_TOPIC = "pameas-notification";
    static final String LOCATION_TOPIC = "pameas-location";

    private final KafkaProducer<String, PameasNotificationTO> notificationProducer;


    @Autowired
    public KafkaServiceImpl(KafkaProducer<String, PameasNotificationTO> notificationProducer) {
        this.notificationProducer = notificationProducer;

    }


    @Override
    public void writeToPameasNotification(PameasNotificationTO pameasNotification) {
        try {
            log.info("pushing notification to  kafka {}", pameasNotification);
            this.notificationProducer.send(new ProducerRecord<>(NOTIFICATION_TOPIC, pameasNotification));
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
//            producer.close();
        }
    }

    @KafkaListener(topics = "smart-safety-system", groupId = "uaeg-consumer-group")
    @Override
    public void monitorSmartSafety(String message) {
        //TODO 
        System.out.println("Received Message in group foo: " + message);

    }

    @Override
    @KafkaListener(topics = "evacuation-coordinator", groupId = "uaeg-consumer-group")
    public void monitorEvacuationCoordinator(String message) {
        //TODO
        System.out.println("Received Message in group foo: " + message);
    }

    @Override
    @KafkaListener(topics = "pameas-notification", groupId = "uaeg-consumer-group")
    public void monitorPameasNotification(String message) {
        //TODO
        System.out.println("Received Message in group foo: " + message);
    }

    @Override
    @KafkaListener(topics = "heartbeat-request", groupId = "uaeg-consumer-group")
    public void monitorHeartbeat(String message) {
//heartbeat-request
        //TODO
        System.out.println("Received Message in group foo: " + message);
    }

    @Override
    @KafkaListener(topics = "resource-discovery-request", groupId = "uaeg-consumer-group")
    public void resourceDiscoveryRequest(String message) {
        //TODO
        System.out.println("Received Message in group foo: " + message);
    }

    @Override
    public void writePameasNotification(PameasNotificationTO notification) {
        try {
            log.info("pushing to  kafka {}", notification);
            this.notificationProducer.send(new ProducerRecord<>("pameas-notification", notification));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
