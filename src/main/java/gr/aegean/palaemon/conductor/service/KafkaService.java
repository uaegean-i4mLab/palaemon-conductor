package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;

public interface KafkaService {

    public void writeToPameasNotification(PameasNotificationTO pameasNotification);

    public void monitorSmartSafety(String message);

    public void monitorEvacuationCoordinator(String message);

    public void monitorPameasNotification(String message);
    public void monitorHeartbeat(String message);
    //
    public void resourceDiscoveryRequest(String message);

    public void writePameasNotification(PameasNotificationTO notification);
}
