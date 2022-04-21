package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.pojo.Geofence;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.model.pojo.ShipsGeofences;
import gr.aegean.palaemon.conductor.model.pojo.UpdatePersonStatusTO;

import java.util.List;

public interface DBProxyService {

    public void updateGeofenceStatus(Geofence geofence);

    public void updatePassengerAssignedMS(String musteringStation, String hashedMacAddress);

    public void updateCrewInPosition(String hashedMacAddress, boolean inPosition);

    public ShipsGeofences getAllGeofences();

    public String getEvacuationStatus();

    public List<PameasPerson> getPassengerDetails();
    public List<PameasPerson> getCrewMembers();


    public void updatePassengerPath(UpdatePersonStatusTO personStatusTO);
}
