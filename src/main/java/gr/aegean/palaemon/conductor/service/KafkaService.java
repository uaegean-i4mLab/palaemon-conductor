package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.TO.EvacuationCoordinatorEventTO;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.pojo.BraceletPojo;

public interface KafkaService {

    public void writeToPameasNotification(PameasNotificationTO pameasNotification);

    public void monitorSmartSafety(String message);


    public void monitorPameasNotification(String message);

    public void writePameasNotification(PameasNotificationTO notification);

    public void writeToBracelets(BraceletPojo braceletPojo);

    public void writeToEvacuationCoordinator(EvacuationCoordinatorEventTO eventTO);


    public void monitorEvacuationCoordinator(String message);
    public void monitorHeartbeat(String message);

    public void monitorResourceDiscover(String message);

    public void monitorBraceletSaturation(String message);

    public void monitorBraceletFallEvent(String message);


    public void monitorSRAP(String message);

}
