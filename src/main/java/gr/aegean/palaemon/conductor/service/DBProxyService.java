package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.pojo.*;

import java.util.List;
import java.util.Optional;

public interface DBProxyService {

    public void updateGeofenceStatus(Geofence geofence);

    public void updatePassengerAssignedMS(String musteringStation, String hashedMacAddress);

    public void updateCrewInPosition(String hashedMacAddress, boolean inPosition);

    public ShipsGeofences getAllGeofences();

    public String getEvacuationStatus();

    public List<PameasPerson> getPassengerDetails();
    public List<PameasPerson> getCrewMembers();

    public Optional<PameasPerson> getSinglePassengerDetails(String hashedMacAddress);


    public void updatePassengerPath(UpdatePersonStatusTO personStatusTO);

    public void declarePassengerIncident(IncidentTO incidentTO);
}
