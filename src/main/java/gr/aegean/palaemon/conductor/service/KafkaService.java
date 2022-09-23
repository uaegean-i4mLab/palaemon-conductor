package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.TO.EvacuationCoordinatorEventTO;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.TO.SmartSafetySystemEventTO;
import gr.aegean.palaemon.conductor.model.TO.SrapTO;
import gr.aegean.palaemon.conductor.model.pojo.BraceletPojo;
import gr.aegean.palaemon.conductor.model.pojo.LegacySystemTO;

public interface KafkaService {

    public void writeToPameasNotification(PameasNotificationTO pameasNotification);

    public void monitorSmartSafety(String message);


    public void monitorPameasNotification(String message);

    public void writePameasNotification(PameasNotificationTO notification);

    public void writeToBracelets(BraceletPojo braceletPojo);

    public void writeToEvacuationCoordinator(EvacuationCoordinatorEventTO eventTO);

    public void writeToSmartSafetySystem(SmartSafetySystemEventTO smartSafetySystemEventTO);


    public void writeToLegacySystem(LegacySystemTO legacySystemTO);


    public void writeToSRAP(SrapTO SrapTO);

    public void monitorEvacuationCoordinator(String message);
    public void monitorHeartbeat(String message);

    public void monitorResourceDiscover(String message);

    public void monitorBraceletSaturation(String message);

    public void monitorBraceletFallEvent(String message);


    public void monitorSRAP(String message);

    public void monitorLegacy(String message);
    public void monitorShmAlarmLegacy(String message);


    public void monitorStabilityToolkit(String message)  ;

    public void monitorCameras(String message)  ;
    public void monitorWeather(String message)  ;
}
