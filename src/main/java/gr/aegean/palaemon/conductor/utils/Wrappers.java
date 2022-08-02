package gr.aegean.palaemon.conductor.utils;

import gr.aegean.palaemon.conductor.model.TO.*;
import gr.aegean.palaemon.conductor.model.pojo.*;
import gr.aegean.palaemon.conductor.service.DistanceCalculatorService;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
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

    public static PameasPerson hashMap2PameasPerson(LinkedHashMap<String, Object> hashMap) {
        PameasPerson pameasPerson = new PameasPerson();
        pameasPerson.setId((String) hashMap.get("id"));
        pameasPerson.setPersonalInfo(hashMap2PersonalInfo((LinkedHashMap<String, Object>) hashMap.get("personalInfo")));
        pameasPerson.setNetworkInfo(hashMap2NetworkInfo((LinkedHashMap<String, Object>) hashMap.get("networkInfo")));
        pameasPerson.setLocationInfo(hashMap2LocationInfo((LinkedHashMap<String, Object>) hashMap.get("locationInfo")));

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
        if (((List) hashMap.get("locationHistory"))!= null && ((List) hashMap.get("locationHistory")).size() > 0) {
            locations = (List<UserLocationUnit>) ((List) hashMap.get("locationHistory")).stream().map(locationUnit -> {
                return hashMap2UserLocationUnit((LinkedHashMap<String, Object>) locationUnit);
            }).collect(Collectors.toList());

        }
        if (((List) hashMap.get("geofenceHistory"))!= null && ((List) hashMap.get("geofenceHistory")).size() > 0) {
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
            if(hashMap.get("currentGeofences") != null)
                requests.setCurrentGeofences((LinkedHashMap<String, String>) hashMap.get("currentGeofences"));
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


    public static PassengerAssignmentResponse hashmap2PassengerAssignmentResponse(HashMap map) {
        PassengerAssignmentResponse response = new PassengerAssignmentResponse();
        response.setAction((String) map.get("action"));
        response.setMusterStation((String) map.get("musterStation"));
        response.setHashedMacAddress((String) map.get("hashedMacAddress"));
        response.setPathId((String) map.get("pathId"));
        response.setGeofence((String) map.get("geofence"));
        return response;
    }

    public static NotificationIncidentTO notificationTo2NotificationIncidientTO(IncidentTO incidentTO) {
        NotificationIncidentTO result = new NotificationIncidentTO();
        result.setHealthIssues(incidentTO.getHealthIssues());
        result.setStatus(incidentTO.getStatus().toString());
        result.setPassengerName(incidentTO.getPassengerName());
        result.setPassengerSurname(incidentTO.getPassengerSurname());
        result.setGeofence(incidentTO.getGeofence());
        result.setXloc(incidentTO.getXLoc());
        result.setId(incidentTO.getId());
        result.setPregnancyStatus(incidentTO.getPregnancyStatus());
        result.setYloc(incidentTO.getYLoc());
        result.setTimestamp(incidentTO.getTimestamp());
        result.setMobilityIssues(incidentTO.getMobilityIssues());
        result.setDeck(incidentTO.getDeck());

        return result;

    }

    public static PameasNotificationTO map2PameasNotificationTO(Map<String, Object> map) {
       /*
       {
   "assignments": [{
   "type":"PASSENGER_INCIDENT_ASSIGNMENT_ACCEPTENCE",
   "id":"b885e805-2c36-4667-9afe-88fa5500576b",
   "status":"ASSIGNED",
   "timestamp":"2022-06-02 14:32:04.184",
   "macAddress":"",
   "passengerName":"Nikos",
   "passengerSurname":"Test2",
   "preferredLanguage":[
      "en"
   ],
   "mobilityIssues":"",
   "pregnancyStatus":"",
   "assignedCrewMemberId":[
      "25LhI4EBKDLYqDqLHLFJ"
   ],
   "incident":{
      "id":"b885e805-2c36-4667-9afe-88fa5500576b",
      "status":"ASSIGNED",
      "passenger_name":"Nikos",
      "passenger_surname":"Test2",
      "health_issues":"",
      "mobility_issues":"",
      "pregnancy_status":"",
      "xloc":"12314",
      "yloc":"12321312",
      "geofence":"geofence2",
      "timestamp":"2022-06-02 14:32:04.183"
   },
   "crew":[
      {
         "id":"25LhI4EBKDLYqDqLHLFJ",
         "name":"NikosCrew",
         "surname":"TestCrew",
         "hashedMacAddress":"",
         "emergencyRole":"medical_unit",
         "languages":[
            "IE"
         ],
         "assigned":false,
         "geofence":"",
         "xloc":"",
         "yloc":""
      }
   ],
   "health_issues":"",
   "x_loc":"12314",
   "y_loc":"12321312",
   "geofence":"geofence2"
}]
}
        */
        PameasNotificationTO result = new PameasNotificationTO();
        result.setType((String) map.get("type"));
        if (map.get("status") != null && map.get("status").equals("CLOSED")) {
            result.setStatus(Incident.IncidentStatus.valueOf(map.get("status").toString()).toString());
        } else {
            result.setStatus("OPEN");
        }

        if (map.get("incidentId") != null) {
            result.setId((String) map.get("incidentId"));
        } else {
            result.setId((String) map.get("id"));
        }

        result.setTimestamp((String) map.get("timestamp"));
        result.setMacAddress((String) map.get("macAddress"));
        if (map.get("name") != null) {
            result.setPassengerName((String) map.get("name"));
        } else {
            result.setPassengerName((String) map.get("passengerName"));
        }
        if (map.get("surname") != null) {
            result.setPassengerSurname((String) map.get("surname"));
        } else {
            result.setPassengerSurname((String) map.get("passengerSurname"));
        }


        if (map.get("preferredLanguage") != null) {
            String[] languageArray = new String[((ArrayList<String>) map.get("preferredLanguage")).size()];
            result.setPreferredLanguage((((ArrayList<String>) map.get("preferredLanguage")).toArray(languageArray)));
        }
        if (map.get("passengerLanguage") != null) {
            String[] languageArray = new String[1];
            languageArray[0] = (String) map.get("passengerLanguage");
            result.setPreferredLanguage(languageArray);
        }

        result.setMobilityIssues((String) map.get("mobilityIssues"));
        result.setPregnancyStatus((String) map.get("pregnancyStatus"));
        if (map.get("healthCondition") != null) {
            result.setHealthIssues((String) map.get("healthCondition"));
        } else {
            result.setHealthIssues((String) map.get("healthIssues"));

        }


        if (map.get("assignedCrewMemberId") != null) {
            String[] assignedCrewMembers = new String[((ArrayList) map.get("assignedCrewMemberId")).size()];
            result.setAssignedCrewMemberId(((ArrayList<String>) map.get("assignedCrewMemberId")).toArray(assignedCrewMembers));
            result.setIncident(map2NotificationIncidentTO((Map) map.get("incident")));
        }
        //TODO

//        result.setCrew(map2NotificationIncidentCrewTO((Map) map.get("id")));
        if (map.get("crew") != null) {
            NotificationIncidentCrewTO[] crewMembers = new NotificationIncidentCrewTO[((ArrayList<Object>) map.get("crew")).size()];
            ArrayList<NotificationIncidentCrewTO> crewMembersList = new ArrayList<>();
            ((ArrayList<Object>) map.get("crew")).forEach((crewMember) -> {
                crewMembersList.add(hash2NotificationIncidentCrewTO((Map<String, Object>) crewMember));
            });
            result.setCrew(crewMembersList.toArray(crewMembers));
        }
        result.setXloc((String) map.get("x_loc"));
        result.setYloc((String) map.get("y_loc"));
        result.setGeofence((String) map.get("geofence"));

        NotificationIncidentTO notificationIncidentTO = new NotificationIncidentTO();
        notificationIncidentTO.setDeck((String)map.get("deck"));
        notificationIncidentTO.setStatus(result.getStatus());
        notificationIncidentTO.setId(result.getId());
        notificationIncidentTO.setPassengerName(result.getPassengerName());
        notificationIncidentTO.setPassengerSurname(result.getPassengerSurname());
        notificationIncidentTO.setHealthIssues(result.getHealthIssues());
        notificationIncidentTO.setStatus(result.getStatus());
        notificationIncidentTO.setTimestamp(result.getTimestamp());
        notificationIncidentTO.setMobilityIssues(result.getMobilityIssues());
        notificationIncidentTO.setPregnancyStatus(result.getPregnancyStatus());
        notificationIncidentTO.setYloc(result.getYloc());
        notificationIncidentTO.setXloc(result.getXloc());
        notificationIncidentTO.setGeofence(result.getGeofence());
        result.setIncident(notificationIncidentTO);


        return result;
    }

    public static NotificationIncidentCrewTO hash2NotificationIncidentCrewTO(Map<String, Object> map) {
        NotificationIncidentCrewTO crewTO = new NotificationIncidentCrewTO();
        crewTO.setHashedMacAddress((String) map.get("hashedMacAddress"));
        crewTO.setGeofence((String) map.get("geofence"));
        crewTO.setYloc((String) map.get("yloc"));
        crewTO.setXloc((String) map.get("xloc"));
        crewTO.setId((String) map.get("id"));
        crewTO.setName((String) map.get("name"));
        crewTO.setSurname((String) map.get("surname"));
        if (map.get("assigned") != null)
            crewTO.setAssigned((boolean) map.get("assigned"));
        crewTO.setEmergencyRole((String) map.get("emergencyRole"));
        if (map.get("languages") != null) {
            String[] languages = new String[((ArrayList<String>) map.get("languages")).size()];
            crewTO.setLanguages(((ArrayList<String>) map.get("languages")).toArray(languages));
        }

        return crewTO;
    }


    // incident={id=b885e805-2c36-4667-9afe-88fa5500576b, status=OPEN, passenger_name=Nikos,
    // passenger_surname=Test2, health_issues=, mobility_issues=, pregnancy_status=normal,
    // xloc=null, yloc=null, geofence=geofence2, timestamp=2022.06.01.11.36.53}, crew=null, health_issues=,
    // x_loc=null, y_loc=null, geofence=geofence2}]
    public static NotificationIncidentTO map2NotificationIncidentTO(Map<String, String> map) {
        NotificationIncidentTO result = new NotificationIncidentTO();
        result.setId(map.get("id"));
        result.setStatus(map.get("status"));
        result.setPassengerName(map.get("passenger_name"));
        result.setPassengerSurname(map.get("passenger_surname"));
        result.setHealthIssues(map.get("health_issues"));
        result.setMobilityIssues(map.get("mobility_issues"));
        result.setPregnancyStatus(map.get("pregnancy_status"));
        result.setXloc(map.get("xloc"));
        result.setYloc(map.get("yloc"));
        result.setGeofence(map.get("geofence"));
        result.setTimestamp(map.get("timestamp"));
        return result;
    }

    public static NotificationIncidentCrewTO map2NotificationIncidentCrewTO(Map<String, Object> map) {
        NotificationIncidentCrewTO result = new NotificationIncidentCrewTO();
        result.setGeofence((String) map.get("geofence"));
        result.setYloc((String) map.get("yLoc"));
        result.setXloc((String) map.get("xLoc"));
        result.setId((String) map.get("Id"));
        result.setHashedMacAddress((String) map.get("hashedMacAddress"));
        result.setAssigned((boolean) map.get("assigned"));
        result.setEmergencyRole((String) map.get("emergencyRole"));
        result.setLanguages((String[]) map.get("languages"));
        result.setName((String) map.get("name"));
        result.setSurname((String) map.get("surName"));

        return result;
    }

    //ConstraintSolverIncident
    public static ConstraintSolverIncident pameasNotificationTO2ConstraintSolverIncident(PameasNotificationTO notification) {
        ConstraintSolverIncident incident = new ConstraintSolverIncident();
        incident.setIncidentId(notification.getId());
        incident.setHealthCondition(notification.getHealthIssues());
        incident.setPassengerLanguage(notification.getPreferredLanguage()[0]);
        if (notification.getIncident() != null)
            incident.setDeck(notification.getIncident().getDeck());
        incident.setGeofence(notification.getGeofence());
        incident.setPassengerLanguage(notification.getPreferredLanguage()[0]);
        incident.setXLoc(notification.getXloc());
        incident.setYLoc(notification.getYloc());
        incident.setName(notification.getPassengerName());
        incident.setSurname(notification.getPassengerSurname());
        return incident;
    }

    public static IncidentCrewPerson pameasPerson2IncidentCrewPerson(PameasPerson p, List<ConstraintSolverIncident> incidents,
                                                                     DistanceCalculatorService distanceCalculatorService) {
        IncidentCrewPerson result = new IncidentCrewPerson();
        result.setCrewMemberId(p.getId());
        result.setDeck(p.getLocationInfo().getGeofenceHistory().get(p.getLocationInfo().getGeofenceHistory().size() - 1).getDeck());
        String[] languages = new String[p.getPersonalInfo().getPreferredLanguage().size()];
        result.setSpokenLanguages(p.getPersonalInfo().getPreferredLanguage().toArray(languages));
        if (p.getPersonalInfo().getAssignmentStatus() != null) {
            result.setAssignmentStatus(p.getPersonalInfo().getAssignmentStatus().toString());
        } else {
            result.setAssignmentStatus("UNASSIGNED");
        }

        result.setName(p.getPersonalInfo().getName());
        result.setSurname(p.getPersonalInfo().getSurname());
        //TODO
        HashMap<String, Integer> map = new HashMap();
        incidents.forEach(incident -> {
            String xLoc = incident.getXLoc();
            String yLoc = incident.getYLoc();
            String deck = incident.getDeck();

            CalculateDistanceRequest calculateDistanceRequest = new CalculateDistanceRequest();
            calculateDistanceRequest.setDeck(deck);
            calculateDistanceRequest.setMusterStationId("");
            calculateDistanceRequest.setTimestamp("");
            calculateDistanceRequest.setXCoord(xLoc);
            calculateDistanceRequest.setYCoord(yLoc);
            calculateDistanceRequest.setXCoord2(p.getLocationInfo().getLocationHistory().get(p.getLocationInfo().getLocationHistory().size() - 1).getXLocation());
            calculateDistanceRequest.setYCoord2(p.getLocationInfo().getLocationHistory().get(p.getLocationInfo().getLocationHistory().size() - 1).getYLocation());
            map.put(incident.getIncidentId(), distanceCalculatorService.calculateDistance(calculateDistanceRequest));
        });
        result.setDistanceFromIncidents(map);

        result.setEmergencyDuty(p.getPersonalInfo().getEmergencyDuty());


        return result;
    }


    public static PameasNotificationTO incidentAssignmentTO2PameasNotificationTO(IncidentAssignmentTO solution) {
        PameasNotificationTO notificationTO = new PameasNotificationTO();
        notificationTO.setIncident(incidentAssignmentTO2NotificationIncidentTO(solution));
        notificationTO.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
        notificationTO.setCrew(solution.getCrewMembers().stream().map(crewMemberTO ->
                        crewMemberTO2NotificationIncidentCrewTO(crewMemberTO))
                .collect(Collectors.toList()).toArray(new NotificationIncidentCrewTO[solution.getCrewMembers().size()]));
        String[] languages = new String[1];
        languages[0] = solution.getPassengerLanguage();
        notificationTO.setPreferredLanguage(languages);
        String[] crewMemberIds = new String[solution.getCrewMembers().size()];
        notificationTO.setAssignedCrewMemberId(solution.getCrewMembers().stream().map(CrewMemberTO::getCrewMemberId).collect(Collectors.toList()).toArray(crewMemberIds));
        notificationTO.setId(solution.getIncidentId());
        notificationTO.setPassengerSurname(solution.getSurname());
        notificationTO.setPassengerName(solution.getName());
        notificationTO.setStatus("ASSIGNED");
        notificationTO.setMacAddress("");
        notificationTO.setXloc(solution.getXLoc());
        notificationTO.setYloc(solution.getYLoc());
        notificationTO.setGeofence(solution.getGeofence());
        notificationTO.setPregnancyStatus("");
        notificationTO.setMobilityIssues("");
        notificationTO.setHealthIssues("");
        notificationTO.setType("PASSENGER_INCIDENT_ASSIGNMENT_AUTHORIZATION");

        return notificationTO;
    }


    public static NotificationIncidentTO incidentAssignmentTO2NotificationIncidentTO(IncidentAssignmentTO solution) {
        NotificationIncidentTO notificationIncidentTO = new NotificationIncidentTO();
        notificationIncidentTO.setGeofence(solution.getGeofence());
        notificationIncidentTO.setXloc(solution.getXLoc());
        notificationIncidentTO.setYloc(solution.getYLoc());
        notificationIncidentTO.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
        notificationIncidentTO.setPregnancyStatus("");
        notificationIncidentTO.setMobilityIssues("");
        notificationIncidentTO.setPassengerSurname(solution.getSurname());
        notificationIncidentTO.setPassengerName(solution.getName());
        notificationIncidentTO.setId(solution.getIncidentId());
        if (solution.getHealthCondition() != null) {
            notificationIncidentTO.setHealthIssues(solution.getHealthCondition().getCondition());
        } else {
            notificationIncidentTO.setHealthIssues("");
        }
        notificationIncidentTO.setStatus("ASSIGNED");
        notificationIncidentTO.setDeck(solution.getDeck());
        return notificationIncidentTO;
    }

    public static NotificationIncidentCrewTO crewMemberTO2NotificationIncidentCrewTO(CrewMemberTO crewMemberTO) {
        NotificationIncidentCrewTO incidentCrewTO = new NotificationIncidentCrewTO();
        incidentCrewTO.setSurname(crewMemberTO.getSurname());
        incidentCrewTO.setAssigned(crewMemberTO.getAssignmentStatus().getName().equals("ASSIGNED"));
        incidentCrewTO.setEmergencyRole(crewMemberTO.getEmergencyDuty().getEmergencyRole());
        incidentCrewTO.setId(crewMemberTO.getCrewMemberId());
        incidentCrewTO.setLanguages(crewMemberTO.getSpokenLanguages().toArray(new String[1]));
        incidentCrewTO.setGeofence("");
        incidentCrewTO.setName(crewMemberTO.getName());
        incidentCrewTO.setXloc("");
        incidentCrewTO.setYloc("");
        incidentCrewTO.setHashedMacAddress("");
        return incidentCrewTO;
    }

    public static PameasNotificationTO pameasPersonToNotificationTO(PameasPerson person) {

        IncidentTO incident = new IncidentTO();
        incident.setPassengerSurname(person.getPersonalInfo().getSurname());
        incident.setPassengerName(person.getPersonalInfo().getName());
        incident.setPregnancyStatus(person.getPersonalInfo().getPrengencyData());
        incident.setStatus(Incident.IncidentStatus.OPEN);
        incident.setXLoc(person.getLocationInfo().getLocationHistory().get(person.getLocationInfo().getLocationHistory().size() - 1).getXLocation());
        incident.setYLoc(person.getLocationInfo().getLocationHistory().get(person.getLocationInfo().getLocationHistory().size() - 1).getYLocation());
        incident.setDeck(person.getLocationInfo().getGeofenceHistory().get(person.getLocationInfo().getGeofenceHistory().size() - 1).getDeck());
        incident.setMobilityIssues(person.getPersonalInfo().getMobilityIssues());
        incident.setHealthIssues(person.getPersonalInfo().getMedicalCondition());
        Date date = new Date();
        incident.setTimestamp((new Timestamp(date.getTime())).toString());
        incident.setId(UUID.randomUUID().toString());
        int size = person.getPersonalInfo().getPreferredLanguage().size();
        String[] languages = new String[size];
        incident.setPreferredLanguage(person.getPersonalInfo().getPreferredLanguage().toArray(languages));
        incident.setGeofence(person.getLocationInfo().getGeofenceHistory().get(person.getLocationInfo().getGeofenceHistory().size() - 1).getGfName());
        incident.setIncidentId(incident.getId());


        NotificationIncidentTO notificationIncidentTO = Wrappers.notificationTo2NotificationIncidientTO(incident);
        PameasNotificationTO pameasNotificationTO = new PameasNotificationTO();
        pameasNotificationTO.setType("PASSENGER_ISSUE");
        pameasNotificationTO.setHealthIssues(incident.getHealthIssues());
        pameasNotificationTO.setTimestamp(incident.getTimestamp());
        pameasNotificationTO.setId(incident.getId());
        pameasNotificationTO.setPassengerName(incident.getPassengerName());
        pameasNotificationTO.setPassengerSurname(incident.getPassengerSurname());
        pameasNotificationTO.setStatus(incident.getStatus().toString());
        pameasNotificationTO.setMobilityIssues(notificationIncidentTO.getMobilityIssues());
        pameasNotificationTO.setAssignedCrewMemberId(null);
        pameasNotificationTO.setCrew(null);
        pameasNotificationTO.setGeofence(incident.getGeofence());
        pameasNotificationTO.setPregnancyStatus(incident.getPregnancyStatus());
        pameasNotificationTO.setPreferredLanguage(incident.getPreferredLanguage());
        pameasNotificationTO.setPreferredLanguage(incident.getPreferredLanguage());
        pameasNotificationTO.setMacAddress(person.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
        pameasNotificationTO.setIncident(notificationIncidentTO);
        pameasNotificationTO.setXloc(incident.getXLoc());
        pameasNotificationTO.setYloc(incident.getYLoc());
        pameasNotificationTO.setStatus(incident.getStatus().toString());
        pameasNotificationTO.setPreferredLanguage(incident.getPreferredLanguage());


        return pameasNotificationTO;


    }

}
