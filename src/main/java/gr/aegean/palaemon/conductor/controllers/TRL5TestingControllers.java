package gr.aegean.palaemon.conductor.controllers;

import gr.aegean.palaemon.conductor.model.TO.ChangePaxLocTO;
import gr.aegean.palaemon.conductor.model.TO.LocationTO;
import gr.aegean.palaemon.conductor.model.location.UserGeofenceUnit;
import gr.aegean.palaemon.conductor.model.location.UserLocationUnit;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import gr.aegean.palaemon.conductor.service.DBProxyService;
import gr.aegean.palaemon.conductor.service.ElasticService;
import gr.aegean.palaemon.conductor.utils.TestingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class TRL5TestingControllers {


    @Autowired
    ElasticService elasticService;

    @Autowired
    DBProxyService dbProxyService;

    @PostMapping("/trl5/movePax")
    public @ResponseBody String changeGeofenceOfPassenger(@RequestBody ChangePaxLocTO changePaxLocTO) {
        List<PameasPerson> allPrsons = this.elasticService.getAllPassengersDecrypted();
        Optional<PameasPerson> pax = allPrsons.stream().filter(pameasPerson -> {
                    return pameasPerson.getNetworkInfo().getMessagingAppClientId() != null &&
                            pameasPerson.getNetworkInfo().getMessagingAppClientId().equals(changePaxLocTO.getMessagingId());
                })
                .findFirst();
        if (pax.isPresent()) {
            PameasPerson passengerMoving = pax.get();
            UserGeofenceUnit userGeofenceUnit = new UserGeofenceUnit();
            userGeofenceUnit.setIsAssociated("true");
            userGeofenceUnit.setGfName(changePaxLocTO.getGeofence());
            userGeofenceUnit.setGfEvent("GEOFENCE EXIT");
            userGeofenceUnit.setGfId("12");
            userGeofenceUnit.setDeck("9");
            userGeofenceUnit.setHashedMacAddress(passengerMoving.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
            userGeofenceUnit.setDwellTime("1");
            userGeofenceUnit.setTimestamp("2022-11-21 07:17:37");
            userGeofenceUnit.setMacAddress(passengerMoving.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());


            UserLocationUnit userLocationUnit = new UserLocationUnit();
            userLocationUnit.setGeofenceNames(Arrays.asList(changePaxLocTO.getGeofence()));
            userLocationUnit.setErrorLevel("0");
            userLocationUnit.setXLocation(passengerMoving.getLocationInfo().getLocationHistory().get(0).getXLocation());
            userLocationUnit.setYLocation(passengerMoving.getLocationInfo().getLocationHistory().get(0).getYLocation());
            userLocationUnit.setCampusId("1");
            userLocationUnit.setFloorId("9");
            userLocationUnit.setTimestamp("2022-11-21 07:17:37");
            userLocationUnit.setBuildingId("1");
            userLocationUnit.setIsAssociated("true");
            userLocationUnit.setHashedMacAddress(passengerMoving.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
            userLocationUnit.setGeofenceId("1");

            passengerMoving.getLocationInfo().getLocationHistory().add(userLocationUnit);
            passengerMoving.getLocationInfo().getGeofenceHistory().add(userGeofenceUnit);

//        this.elasticService.updatePerson(personToLeave.getPersonalInfo().getPersonalId(),personToLeave);
//        this.dbProxyService.addLocation()
            LocationTO location = new LocationTO();
            location.setLocation(userLocationUnit);
            location.setGeofence(userGeofenceUnit);
            location.setHashedMacAddress(passengerMoving.getNetworkInfo().getDeviceInfoList().get(0).getHashedMacAddress());
            location.setMacAddress(passengerMoving.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress());
            this.dbProxyService.addLocationToPassenger(location);
            return "updating person with macAddress {}" + passengerMoving.getNetworkInfo().getDeviceInfoList().get(0).getMacAddress();
        }

        return "Passenger not found with messaging id " + changePaxLocTO.getMessagingId();
    }


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


    @GetMapping("/trl5/testPAX")
    public @ResponseBody String testPAX() {


        TestingUtils.addTestPerson("", "99", "102", "Aggeliki",
                "Souraiti", "pax1", "female", "20", new ArrayList<>(), "PIRAEUS",
                "CHANIA", "A862050", "gelly.st@hotmail.com", "Address 3", "306943808730",
                "GR", "", "", "", false, Personalinfo.AssignmentStatus.UNASSIGNED,
                new String[]{"EN"}, "passenger", "", "28:37:8B:DE:42:P1",
                "502130123456789", "919825098250", "", "SB000P1", "9", "1665427687",
                "1", "event", "1231", "9BG4", "true", "9", "15.80",
                "60.50", "1", "0", List.of("9BG4"));

        return "OK";
    }


}
