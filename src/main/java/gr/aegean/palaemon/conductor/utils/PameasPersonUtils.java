package gr.aegean.palaemon.conductor.utils;

import gr.aegean.palaemon.conductor.model.TO.AddDevicePersonTO;
import gr.aegean.palaemon.conductor.model.TO.ConnectedPersonTO;
import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.TO.PersonTO;
import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PameasPersonUtils {


    // keeps the database (ES) id of the object and pushes all the rest
    public static PameasPerson updatePerson(PameasPerson originalPerson, PameasPerson nPerson) {
        //update personal info
        originalPerson.getPersonalInfo().setName(nPerson.getPersonalInfo().getName());
        originalPerson.getPersonalInfo().setSurname(nPerson.getPersonalInfo().getSurname());
        originalPerson.getPersonalInfo().setPersonalId(nPerson.getPersonalInfo().getPersonalId());
        originalPerson.getPersonalInfo().setGender(nPerson.getPersonalInfo().getGender());
        originalPerson.getPersonalInfo().setDateOfBirth(nPerson.getPersonalInfo().getDateOfBirth());
        originalPerson.getPersonalInfo().setTicketNumber(nPerson.getPersonalInfo().getTicketNumber());

        originalPerson.getPersonalInfo().setTicketInfo(nPerson.getPersonalInfo().getTicketInfo());
        originalPerson.getPersonalInfo().setDateOfBirth(nPerson.getPersonalInfo().getDateOfBirth());
        originalPerson.getPersonalInfo().setEmbarkationPort(nPerson.getPersonalInfo().getEmbarkationPort());
        originalPerson.getPersonalInfo().setDisembarkationPort(nPerson.getPersonalInfo().getDisembarkationPort());
        originalPerson.getPersonalInfo().setEmail(nPerson.getPersonalInfo().getEmail());
        originalPerson.getPersonalInfo().setCountryOfResidence(nPerson.getPersonalInfo().getCountryOfResidence());
        originalPerson.getPersonalInfo().setPreferredLanguage(nPerson.getPersonalInfo().getPreferredLanguage());
        originalPerson.getPersonalInfo().setMedicalCondition(nPerson.getPersonalInfo().getMedicalCondition());
        originalPerson.getPersonalInfo().setMobilityIssues(nPerson.getPersonalInfo().getMobilityIssues());
        originalPerson.getPersonalInfo().setPrengencyData(nPerson.getPersonalInfo().getPrengencyData());
        originalPerson.getPersonalInfo().setEmergencyContact(nPerson.getPersonalInfo().getEmergencyContact());
        originalPerson.getPersonalInfo().setCrew(nPerson.getPersonalInfo().isCrew());
        originalPerson.getPersonalInfo().setRole(nPerson.getPersonalInfo().getRole());
        originalPerson.getPersonalInfo().setEmergencyDuty(nPerson.getPersonalInfo().getEmergencyDuty());
        originalPerson.getPersonalInfo().setHeartBeat(nPerson.getPersonalInfo().getHeartBeat());
        originalPerson.getPersonalInfo().setOxygenSaturation(  nPerson.getPersonalInfo().getOxygenSaturation());



        //update device info
        if (originalPerson.getNetworkInfo() == null) {
            originalPerson.setNetworkInfo(nPerson.getNetworkInfo());
        } else {
            originalPerson.getNetworkInfo().setDeviceInfoList(nPerson.getNetworkInfo().getDeviceInfoList());
            originalPerson.getNetworkInfo().setMessagingAppClientId(nPerson.getNetworkInfo().getMessagingAppClientId());
        }
        if (originalPerson.getLocationInfo() == null) {
            originalPerson.setLocationInfo(nPerson.getLocationInfo());
        } else {
            originalPerson.getLocationInfo().setLocationHistory(nPerson.getLocationInfo().getLocationHistory());
            originalPerson.getLocationInfo().setGeofenceHistory(nPerson.getLocationInfo().getGeofenceHistory());
            originalPerson.getLocationInfo().setSpeed(nPerson.getLocationInfo().getSpeed());
        }
        return originalPerson;

    }


    public static PersonTO buildPersonTO(String saturation, String heartBeat, String name, String surname, String identifier,
                                  String gender, String age, ArrayList<ConnectedPersonTO> connectedPassengers, String embarkation,
                                  String disembarkation, String ticketNumber, String email, String postalAddress,
                                  String emergencyContact, String countryOfResidence, String medicalCondition,
                                  String mobilityIssues, String pregnencyData, boolean isCrew, Personalinfo.AssignmentStatus assigmentStatus,
                                  String[] prefLanguage, String role, String musterStation){
        PersonTO p = new PersonTO();
        p.setSaturation(saturation);
        p.setHeartBeat(heartBeat);
        p.setName(name);
        p.setSurname(surname);
        p.setIdentifier(identifier);
        p.setGender(gender);
        p.setAge(age);
        p.setConnectedPassengers(connectedPassengers);
        p.setEmbarkationPort(embarkation);
        p.setDisembarkationPort(disembarkation);
        p.setTicketNumber(ticketNumber);
        p.setEmail(email);
        p.setPostalAddress(postalAddress);
        p.setEmergencyContact(emergencyContact);
        p.setCountryOfResidence(countryOfResidence);
        p.setMedicalCondition(medicalCondition);
        p.setMobilityIssues(mobilityIssues);
        p.setPrengencyData(pregnencyData);
        p.setCrew(isCrew);
        p.setAssignmentStatus(assigmentStatus);
        p.setAssignedMusteringStation(musterStation);
        p.setPreferredLanguage(prefLanguage);
        p.setRole(role);
        return p;
    }

    public static AddDevicePersonTO addDevicePersonTO(String identifier, String macAddress, String imsi, String imei,
                                               String messagingAppClientId, String braceletId){
        AddDevicePersonTO devicePersonTO = new AddDevicePersonTO();
        devicePersonTO.setIdentifier(identifier);
        devicePersonTO.setMacAddress(macAddress);
        devicePersonTO.setImsi(imsi);
        devicePersonTO.setImei(imei);
        devicePersonTO.setMessagingAppClientId(messagingAppClientId);
        devicePersonTO.setBraceletId(braceletId);
        return  devicePersonTO;
    }

    public static LocationTO addLocationTO(String deck, String timestamp, String macAddress, String gfId,
                                           String gfEvent, String dwellTime, String gfName, String isAsosciated,
                                           String floorId, String yLocation, String xLocation, String campusId,
                                           String errorLevel, List<String> geofenceNames){

        LocationTO location = new LocationTO();
        UserGeofenceUnit geofenceUnit = new UserGeofenceUnit();
        geofenceUnit.setDeck(deck);
        geofenceUnit.setTimestamp(timestamp);
        geofenceUnit.setMacAddress(macAddress);
        geofenceUnit.setGfId(gfId);
        geofenceUnit.setGfEvent(gfEvent);
        geofenceUnit.setDwellTime(dwellTime);
        geofenceUnit.setGfName(gfName);
        geofenceUnit.setHashedMacAddress(DigestUtils.sha256Hex(macAddress));
        geofenceUnit.setIsAssociated(isAsosciated);
        location.setGeofence(geofenceUnit);

        UserLocationUnit locationUnit = new UserLocationUnit();
        locationUnit.setTimestamp(timestamp);
        locationUnit.setIsAssociated(isAsosciated);
        locationUnit.setBuildingId(deck);
        locationUnit.setFloorId(floorId);
        locationUnit.setHashedMacAddress(DigestUtils.sha256Hex(macAddress));
        locationUnit.setYLocation(yLocation);
        locationUnit.setXLocation(xLocation);
        locationUnit.setCampusId(campusId);
        locationUnit.setErrorLevel(errorLevel);
        locationUnit.setGeofenceNames(geofenceNames);
        location.setLocation(locationUnit);

        location.setMacAddress(macAddress);
        location.setHashedMacAddress(DigestUtils.sha256Hex(macAddress));

        return location;
    }

}
