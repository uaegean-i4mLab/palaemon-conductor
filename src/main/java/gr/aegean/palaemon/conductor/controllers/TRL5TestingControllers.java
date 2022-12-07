package gr.aegean.palaemon.conductor.controllers;

import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class TRL5TestingControllers {


    @Autowired
    ElasticService elasticService;

    @Autowired
    DBProxyService dbProxyService;

    @GetMapping("/trl5/movePersonFromMSWithTicket123")
    public @ResponseBody String movePersonFromMSWithTicket123() {
        List<PameasPerson> allPrsons = this.elasticService.getAllPassengersDecrypted();

        List<PameasPerson> pplInMS = allPrsons.stream().filter(pameasPerson -> {
            int locationsSize = pameasPerson.getLocationInfo().getLocationHistory().size();
            String assignedMS = pameasPerson.getPersonalInfo().getAssignedMusteringStation();
            return pameasPerson.getPersonalInfo().getRole().equals("passenger")
                    && pameasPerson.getLocationInfo().getGeofenceHistory().get(locationsSize - 1).getGfName().equals(assignedMS);
        }).filter(pameasPerson -> pameasPerson.getNetworkInfo().getMessagingAppClientId().equals("123"))
                .collect(Collectors.toList());

        List<PameasPerson> pplNotInMS = allPrsons.stream().filter(pameasPerson -> {
            int locationsSize = pameasPerson.getLocationInfo().getLocationHistory().size();
            String assignedMS = pameasPerson.getPersonalInfo().getAssignedMusteringStation();
            return !(pameasPerson.getPersonalInfo().getRole().equals("passenger")
                    && pameasPerson.getLocationInfo().getGeofenceHistory().get(locationsSize - 1).getGfName().equals(assignedMS));
        }).collect(Collectors.toList());


        PameasPerson personToLeave = pplInMS.get(0);


        UserGeofenceUnit userGeofenceUnit = new UserGeofenceUnit();
        userGeofenceUnit.setIsAssociated("true");
        userGeofenceUnit.setGfName("7BG9");
        userGeofenceUnit.setGfEvent("GEOFENCE EXIT");
        userGeofenceUnit.setGfId("12");
        userGeofenceUnit.setDeck("7");
        userGeofenceUnit.setHashedMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
        userGeofenceUnit.setDwellTime("1");
        userGeofenceUnit.setTimestamp("2022-11-21 07:17:37");
        userGeofenceUnit.setMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());


        UserLocationUnit userLocationUnit = new UserLocationUnit();
        userLocationUnit.setGeofenceNames(personToLeave.getLocationInfo().getLocationHistory().get(0).getGeofenceNames());
        userLocationUnit.setErrorLevel("0");
        userLocationUnit.setXLocation(personToLeave.getLocationInfo().getLocationHistory().get(0).getXLocation());
        userLocationUnit.setYLocation(personToLeave.getLocationInfo().getLocationHistory().get(0).getYLocation());
        userLocationUnit.setCampusId("1");
        userLocationUnit.setFloorId("7");
        userLocationUnit.setTimestamp("2022-11-21 07:17:37");
        userLocationUnit.setBuildingId("1");
        userLocationUnit.setIsAssociated("true");
        userLocationUnit.setHashedMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
        userLocationUnit.setGeofenceId("1");

        personToLeave.getLocationInfo().getLocationHistory().add(userLocationUnit);
        personToLeave.getLocationInfo().getGeofenceHistory().add(userGeofenceUnit);

//        this.elasticService.updatePerson(personToLeave.getPersonalInfo().getPersonalId(),personToLeave);
//        this.dbProxyService.addLocation()
        LocationTO location = new LocationTO();
        location.setLocation(userLocationUnit);
        location.setGeofence(userGeofenceUnit);
        location.setHashedMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
        location.setMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());
        this.dbProxyService.addLocationToPassenger(location);

        log.info("updating person with macAddress {}", personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());


        return "OK";
    }


    @GetMapping("/trl5/movePersonToMSWithTicket123")
    public @ResponseBody String movePersonToMSWithTicket123() {
        List<PameasPerson> allPrsons = this.elasticService.getAllPassengersDecrypted();

        List<PameasPerson> pplInMS = allPrsons.stream().filter(pameasPerson -> {
                    int locationsSize = pameasPerson.getLocationInfo().getLocationHistory().size();
                    String assignedMS = pameasPerson.getPersonalInfo().getAssignedMusteringStation();
                    return pameasPerson.getPersonalInfo().getRole().equals("passenger")
                            && pameasPerson.getLocationInfo().getGeofenceHistory().get(locationsSize - 1).getGfName().equals(assignedMS);
                })
                .collect(Collectors.toList());

        List<PameasPerson> pplNotInMS = allPrsons.stream().filter(pameasPerson -> {
            int locationsSize = pameasPerson.getLocationInfo().getLocationHistory().size();
            String assignedMS = pameasPerson.getPersonalInfo().getAssignedMusteringStation();
            return !(pameasPerson.getPersonalInfo().getRole().equals("passenger")
                    && pameasPerson.getLocationInfo().getGeofenceHistory().get(locationsSize - 1).getGfName().equals(assignedMS));
        }).filter(pameasPerson -> pameasPerson.getNetworkInfo().getMessagingAppClientId().equals("123")).collect(Collectors.toList());


        PameasPerson personToLeave = pplNotInMS.get(0);


        UserGeofenceUnit userGeofenceUnit = new UserGeofenceUnit();
        userGeofenceUnit.setIsAssociated("true");
        userGeofenceUnit.setGfName("7BG6");
        userGeofenceUnit.setGfEvent("GEOFENCE EXIT");
        userGeofenceUnit.setGfId("12");
        userGeofenceUnit.setDeck("7");
        userGeofenceUnit.setHashedMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
        userGeofenceUnit.setDwellTime("1");
        userGeofenceUnit.setTimestamp("2022-11-21 07:17:37");
        userGeofenceUnit.setMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());


        UserLocationUnit userLocationUnit = new UserLocationUnit();
        userLocationUnit.setGeofenceNames(personToLeave.getLocationInfo().getLocationHistory().get(0).getGeofenceNames());
        userLocationUnit.setErrorLevel("0");
        userLocationUnit.setXLocation(personToLeave.getLocationInfo().getLocationHistory().get(0).getXLocation());
        userLocationUnit.setYLocation(personToLeave.getLocationInfo().getLocationHistory().get(0).getYLocation());
        userLocationUnit.setCampusId("1");
        userLocationUnit.setFloorId("7");
        userLocationUnit.setTimestamp("2022-11-21 07:17:37");
        userLocationUnit.setBuildingId("1");
        userLocationUnit.setIsAssociated("true");
        userLocationUnit.setHashedMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
        userLocationUnit.setGeofenceId("1");

        personToLeave.getLocationInfo().getLocationHistory().add(userLocationUnit);
        personToLeave.getLocationInfo().getGeofenceHistory().add(userGeofenceUnit);

//        this.elasticService.updatePerson(personToLeave.getPersonalInfo().getPersonalId(),personToLeave);
//        this.dbProxyService.addLocation()
        LocationTO location = new LocationTO();
        location.setLocation(userLocationUnit);
        location.setGeofence(userGeofenceUnit);
        location.setHashedMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
        location.setMacAddress(personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());
        this.dbProxyService.addLocationToPassenger(location);

        log.info("updating person with macAddress {}", personToLeave.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());


        return "OK";
    }


}
