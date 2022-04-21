package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;

public interface KafkaService {

    public void writeToPameasNotification(PameasNotificationTO pameasNotification);
}
