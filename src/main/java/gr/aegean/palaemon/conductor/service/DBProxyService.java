package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.TO.IncidentTO;
import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.TO.UpdatePersonStatusTO;
import gr.aegean.palaemon.conductor.model.pojo.*;

import java.util.List;
import java.util.Optional;

public interface DBProxyService {

    public void updateGeofenceStatus(Geofence geofence);

    public void updatePassengerAssignedMS(String musteringStation, String hashedMacAddress);

    public void updatePassengerAssignedMSBulk(String[] musteringStation, String[] hashedMacAddress);

    public void updateCrewInPosition(String hashedMacAddress, boolean inPosition);

    public ShipsGeofences getAllGeofences();

    public String getEvacuationStatus();

    public void setEvacuationStatus(String status);

    public List<PameasPerson> getPassengerDetails();
    public List<PameasPerson> getCrewMembers();

    public Optional<PameasPerson> getSinglePassengerDetails(String hashedMacAddress);


    public void updatePassengerPath(UpdatePersonStatusTO personStatusTO);

    public void declarePassengerIncident(IncidentTO incidentTO);

    public void updateCrewMemberStatus(String hashAddress, String id, Personalinfo.AssignmentStatus status);


    public void savePassengerIncident(IncidentTO incidentTO);
    public void updatePassengerIncident(IncidentTO incidentTO);
    public Optional<IncidentTO> getIncidentFromId(String incidentId);


    public void addLocationToPassenger(LocationTO locationTO);

}
