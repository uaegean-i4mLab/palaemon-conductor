package gr.aegean.palaemon.conductor.service.impl;

import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.service.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
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
}
