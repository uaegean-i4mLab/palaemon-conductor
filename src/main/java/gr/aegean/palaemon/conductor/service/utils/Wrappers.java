package gr.aegean.palaemon.conductor.service.utils;

import gr.aegean.palaemon.conductor.model.pojo.*;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Wrappers {


    public static Passenger paemasPerson2Passenger(PameasPerson pameasPerson) {

        Passenger passenger = new Passenger();

        if (pameasPerson.getLocationInfo() != null &&
                pameasPerson.getLocationInfo().getGeofenceHistory() != null && pameasPerson.getLocationInfo().getGeofenceHistory().size() > 0) {
            if (pameasPerson.getLocationInfo() != null) {
                String deck =
                        StringUtils.isEmpty(pameasPerson.getLocationInfo().getGeofenceHistory().get(pameasPerson.getLocationInfo().getGeofenceHistory().size() - 1).getDeck()) ?
                        "Deck1" : pameasPerson.getLocationInfo().getGeofenceHistory().get(pameasPerson.getLocationInfo().getGeofenceHistory().size() - 1).getDeck();
                passenger.setDeck(deck);
                passenger.setGeofence(pameasPerson.getLocationInfo().getGeofenceHistory().get(pameasPerson.getLocationInfo().getGeofenceHistory().size() - 1).
                        getGfName());
            }
        }

        if (pameasPerson.getNetworkInfo().getDeviceInfoList() != null && pameasPerson.getNetworkInfo().getDeviceInfoList().size() > 0)
            passenger.setHashedMacAddress(pameasPerson.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());

        passenger.setDistances(new ArrayList<>());
        List<String> medicalConditions = new ArrayList<>();
        medicalConditions.add(pameasPerson.getPersonalInfo().getMedicalCondition());
        passenger.setHealthConditions(medicalConditions);

        return passenger;
    }


    public static MusterStation geofences2MusterStation(Geofence geofence) {
        MusterStation ms = new MusterStation();
        ms.setGeofence(geofence.getId());
        ms.setDeck(StringUtils.isEmpty((String) geofence.getDeck()) ? "Deck1" : (String) geofence.getDeck());

        ms.setName(geofence.getGfName());

        return ms;
    }

    public static PameasPerson hashMap2PameasPerson(LinkedHashMap<String, LinkedHashMap<String, Object>> hashMap) {
        PameasPerson pameasPerson = new PameasPerson();
        pameasPerson.setPersonalInfo(hashMap2PersonalInfo(hashMap.get("personalInfo")));
        pameasPerson.setNetworkInfo(hashMap2NetworkInfo(hashMap.get("networkInfo")));
        pameasPerson.setLocationInfo(hashMap2LocationInfo(hashMap.get("locationInfo")));

        return pameasPerson;

    }

    public static Personalinfo hashMap2PersonalInfo(LinkedHashMap<String, Object> hashMap) {
        Personalinfo personalinfo = new Personalinfo();
        personalinfo.setPersonalId((String) hashMap.get("personalId"));
        personalinfo.setAssignedMusteringStation((String) hashMap.get("assignedMusteringStation"));
        personalinfo.setName((String) hashMap.get("name"));
        personalinfo.setCrew((Boolean) hashMap.get("crew"));
        if (hashMap.get("assignmentStatus") != null)
            personalinfo.setAssignmentStatus(Personalinfo.AssignmentStatus.valueOf((String) hashMap.get("assignmentStatus")));
        personalinfo.setCountryOfResidence((String) hashMap.get("countryOfResidence"));
        personalinfo.setDateOfBirth((String) hashMap.get("dateOfBirth"));
        personalinfo.setDisembarkationPort((String) hashMap.get("disembarkationPort"));
        // TODO personalinfo.setDutyScheduleList();
        personalinfo.setEmail((String) hashMap.get("email"));
        personalinfo.setEmbarkationPort((String) hashMap.get("embarkationPort"));
        personalinfo.setEmergencyContact((String) hashMap.get("emergencyContact"));
        personalinfo.setGender((String) hashMap.get("gender"));
        personalinfo.setEmergencyDuty((String) hashMap.get("emergencyDuty"));
        personalinfo.setMedicalCondition((String) hashMap.get("medicalCondition"));
        personalinfo.setInPosition((Boolean) hashMap.get("inPosition"));
        personalinfo.setMobilityIssues((String) hashMap.get("mobilityIssues"));
        personalinfo.setPrengencyData((String) hashMap.get("prengencyData"));
        personalinfo.setPostalAddress((String) hashMap.get("postalAddress"));
        personalinfo.setPreferredLanguage(((List) hashMap.get("preferredLanguage")));
        personalinfo.setSurname((String) hashMap.get("surname"));
        personalinfo.setRole((String) hashMap.get("role"));
        personalinfo.setTicketNumber((String) hashMap.get("ticketNumber"));
        //TODO personalinfo.setTicketInfo();

        return personalinfo;

    }

    public static NetworkInfo hashMap2NetworkInfo(LinkedHashMap<String, Object> hashMap) {
        NetworkInfo networkInfo = new NetworkInfo();
        networkInfo.setMessagingAppClientId((String) hashMap.get("messagingAppClientId"));
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        if (((List) hashMap.get("deviceInfoList")).size() > 0) {
            deviceInfoList = (List<DeviceInfo>) ((List) hashMap.get("deviceInfoList")).stream().map(deviceInfo -> {
                return hashMap2DeviceInfo((LinkedHashMap<String, Object>) deviceInfo);
            }).collect(Collectors.toList());

        }

        networkInfo.setDeviceInfoList(deviceInfoList);
        return networkInfo;
    }

    public static DeviceInfo hashMap2DeviceInfo(LinkedHashMap<String, Object> hashMap) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setHashedMacAddress((String) hashMap.get("hashedMacAddress"));
        deviceInfo.setImei((String) hashMap.get("imei"));
        deviceInfo.setImsi((String) hashMap.get("imsi"));
        deviceInfo.setMsisdn((String) hashMap.get("msisdn"));
        deviceInfo.setMacAddress((String) hashMap.get("macAddress"));
        return deviceInfo;
    }

    public static LocationInfo hashMap2LocationInfo(LinkedHashMap<String, Object> hashMap) {
        LocationInfo locationInfo = new LocationInfo();
        List<UserLocationUnit> locations = new ArrayList<>();
        List<UserGeofenceUnit> geofenceUnits = new ArrayList<>();
        if (((List) hashMap.get("locationHistory")).size() > 0) {
            locations = (List<UserLocationUnit>) ((List) hashMap.get("locationHistory")).stream().map(locationUnit -> {
                return hashMap2UserLocationUnit((LinkedHashMap<String, Object>) locationUnit);
            }).collect(Collectors.toList());

        }
        if (((List) hashMap.get("geofenceHistory")).size() > 0) {
            geofenceUnits = (List<UserGeofenceUnit>) ((List) hashMap.get("geofenceHistory")).stream().map(geofenceUnit -> {
                return hashMap2UserGeofenceUnit((LinkedHashMap<String, Object>) geofenceUnit);
            }).collect(Collectors.toList());
        }

        locationInfo.setLocationHistory(locations);
        locationInfo.setGeofenceHistory(geofenceUnits);
//        hashMap.get("locationHistory"); //List
        //        hashMap.get("geofenceHistory"); //List


        return locationInfo;
    }

    public static UserLocationUnit hashMap2UserLocationUnit(LinkedHashMap<String, Object> hashMap) {
        UserLocationUnit locationUnit = new UserLocationUnit();
        locationUnit.setXLocation((String) hashMap.get("xLocation"));
        locationUnit.setYLocation((String) hashMap.get("yLocation"));
        locationUnit.setBuildingId((String) hashMap.get("buildingId"));
        locationUnit.setHashedMacAddress((String) hashMap.get("hashedMacAddress"));
        locationUnit.setCampusId((String) hashMap.get("campusId"));
        locationUnit.setErrorLevel((String) hashMap.get("errorLevel"));
        locationUnit.setFloorId((String) hashMap.get("floorId"));
        locationUnit.setGeofenceId((String) hashMap.get("geofenceId"));
        //TODO locationUnit.setGeofenceNames((String) hashMap.get("geofenceNames"));
        locationUnit.setIsAssociated((String) hashMap.get("isAssociated"));
        locationUnit.setTimestamp((String) hashMap.get("timestamp"));

        return locationUnit;

    }

    public static UserGeofenceUnit hashMap2UserGeofenceUnit(LinkedHashMap<String, Object> hashMap) {
        UserGeofenceUnit geofenceUnit = new UserGeofenceUnit();
        geofenceUnit.setDeck(StringUtils.isEmpty((String) hashMap.get("deck")) ? "Deck1" : (String) hashMap.get("deck"));
        geofenceUnit.setHashedMacAddress((String) hashMap.get("hashedMacAddress"));
        geofenceUnit.setIsAssociated((String) hashMap.get("isAssociated"));
        geofenceUnit.setTimestamp((String) hashMap.get("timeStamp"));
        geofenceUnit.setDwellTime((String) hashMap.get("dwellTime"));
        geofenceUnit.setGfEvent((String) hashMap.get("gfEvent"));
        geofenceUnit.setGfId((String) hashMap.get("gfId"));
        geofenceUnit.setGfName((String) hashMap.get("gfName"));
        geofenceUnit.setMacAddress((String) hashMap.get("macAddress"));
        return geofenceUnit;
    }

    public static Geofence hashMap2Geofence(LinkedHashMap<String, Object> hashMap) {
        Geofence geofence = new Geofence();
        geofence.setStatus((String) hashMap.get("status"));
        String deck = StringUtils.isEmpty((String) hashMap.get("deck")) ? "Deck1" : (String) hashMap.get("deck");
        geofence.setDeck(deck);
        geofence.setGfName((String) hashMap.get("gfName"));
        geofence.setId((String) hashMap.get("id"));
        geofence.setMusteringStation((String) hashMap.get("mustering"));
        return geofence;
    }

    public static PassengerMessageBodyRequests hashMap2MessageBodyRequest(LinkedHashMap<String, Object> hashMap) {
        PassengerMessageBodyRequests requests = new PassengerMessageBodyRequests();
        if (hashMap != null) {
            if (hashMap.get("musterStation") != null)
                requests.setMusterStation((LinkedHashMap<String, String>) hashMap.get("musterStation"));
            if (hashMap.get("actions") != null)
                requests.setActions((LinkedHashMap<String, String>) hashMap.get("actions"));
            if (hashMap.get("assignedPathIDs") != null)
                requests.setAssignedPathIDs((LinkedHashMap<String, String>) hashMap.get("assignedPathIDs"));
            if (hashMap.get("passengerLanguages") != null)
                requests.setPassengerLanguages((LinkedHashMap<String, String>) hashMap.get("passengerLanguages"));
            if (hashMap.get("blockedGeofences") != null)
                requests.setBlockedGeofences((ArrayList<String>) hashMap.get("blockedGeofences"));
            if (hashMap.get("messageCodes") != null)
                requests.setMessageCodes((LinkedHashMap<String, String>) hashMap.get("messageCodes"));

        }

        if (requests.getMessageCodes() == null) {
            requests.setMessageCodes(new HashMap<>());
        }

        if (requests.getMusterStation() == null) {
            requests.setMusterStation(new HashMap<>());
        }

        if (requests.getActions() == null) {
            requests.setActions(new HashMap<>());
        }
        if (requests.getAssignedPathIDs() == null) {
            requests.setAssignedPathIDs(new HashMap<>());
        }
        if (requests.getPassengerLanguages() == null) {
            requests.setPassengerLanguages(new HashMap<>());
        }
        if (requests.getBlockedGeofences() == null) {
            requests.setBlockedGeofences(new ArrayList<>());
        }


        return requests;
    }

    public static MessageBody hashmap2MessageBody(Map<String, String> input) {
        MessageBody mb = new MessageBody();
        mb.setHashedMacAddress(input.get("hashedMacAddress"));
        mb.setContent(input.get("content"));
        mb.setVisualAid(input.get("visualAid"));
        return mb;
    }

    public static MessageServiceReceiver messageBody2Receiver(MessageBody messageBody) {
        MessageServiceReceiver receiver = new MessageServiceReceiver();
        receiver.setGlobal("false");
        receiver.setVisualAid(messageBody.getVisualAid());
        receiver.setMsgType("NOTIFICATION");
        receiver.setRecipient(messageBody.getHashedMacAddress());
        receiver.setTextMsg(messageBody.getContent());
        return receiver;
    }


    public static PassengerAssignmentResponse hashmap2PassengerAssignmentResponse(HashMap map){
        PassengerAssignmentResponse response = new PassengerAssignmentResponse();
        response.setAction((String) map.get("action"));
        response.setMusterStation((String)map.get("musterStation"));
        response.setHashedMacAddress((String) map.get("hashedMacAddress"));
        response.setPathId((String) map.get("pathId"));
        return response;
    }

}
