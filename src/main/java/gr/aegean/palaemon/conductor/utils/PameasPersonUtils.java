package gr.aegean.palaemon.conductor.utils;

import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;

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
}
