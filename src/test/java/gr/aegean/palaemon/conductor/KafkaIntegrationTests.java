package gr.aegean.palaemon.conductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aegean.palaemon.conductor.model.TO.PameasNotificationTO;
import gr.aegean.palaemon.conductor.model.pojo.BraceletPojo;
import gr.aegean.palaemon.conductor.service.KafkaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.UUID;

@SpringBootTest
public class KafkaIntegrationTests {

    @Autowired
    KafkaService kafkaService;


    @Test
    public void mockAssignmentApprovalMessage() throws JsonProcessingException {
        String dashboardReceivedNotification = "{\"type\":\"PASSENGER_INCIDENT_ASSIGNMENT_AUTHORIZATION\",\"id\":\"705ec83e-f3f0-4687-8dc0-0306685d5fd9\",\"status\":\"ASSIGNED\",\"timestamp\":\"2022-06-08 10:14:58.553\",\"macAddress\":\"\",\"passengerName\":\"test1\",\"passengerSurname\":\"testSurname1\",\"preferredLanguage\":[\"en\"],\"mobilityIssues\":\"\",\"pregnancyStatus\":\"\",\"assignedCrewMemberId\":[\"7oUUQoEBJnVO1rPQzTkV\"],\"incident\":{\"id\":\"705ec83e-f3f0-4687-8dc0-0306685d5fd9\",\"status\":\"ASSIGNED\",\"deck\":\"7\",\"passenger_name\":\"test1\",\"passenger_surname\":\"testSurname1\",\"health_issues\":\"equip_required\",\"mobility_issues\":\"\",\"pregnancy_status\":\"\",\"xloc\":\"72.5425\",\"yloc\":\"34.2354\",\"geofence\":\"geofence 1\",\"timestamp\":\"2022-06-08 10:14:58.553\"},\"crew\":[{\"id\":\"7oUUQoEBJnVO1rPQzTkV\",\"name\":\"NikosCrew\",\"surname\":\"TestCrew\",\"hashedMacAddress\":\"\",\"emergencyRole\":\"medical_unit\",\"languages\":[\"IE\"],\"assigned\":false,\"geofence\":\"\",\"xloc\":\"\",\"yloc\":\"\"}],\"health_issues\":\"\",\"x_loc\":\"72.5425\",\"y_loc\":\"34.2354\",\"geofence\":\"geofence 1\"}";
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        PameasNotificationTO pameasNotificationTO = mapper.readValue(dashboardReceivedNotification, PameasNotificationTO.class);
        pameasNotificationTO.setType("PASSENGER_INCIDENT_ASSIGNMENT_ACCEPTANCE");
        kafkaService.writeToPameasNotification(pameasNotificationTO);
    }


    @Test
    public void mockAssignmentACK() throws JsonProcessingException {
        String dashboardReceivedNotification = "{\"type\":\"PASSENGER_INCIDENT_ASSIGNMENT_ACK\",\"id\":\"705ec83e-f3f0-4687-8dc0-0306685d5fd9\",\"status\":\"ASSIGNED\",\"timestamp\":\"2022-06-08 10:14:58.553\",\"macAddress\":\"\",\"passengerName\":\"test1\",\"passengerSurname\":\"testSurname1\",\"preferredLanguage\":[\"en\"],\"mobilityIssues\":\"\",\"pregnancyStatus\":\"\",\"assignedCrewMemberId\":[\"7oUUQoEBJnVO1rPQzTkV\"],\"incident\":{\"id\":\"705ec83e-f3f0-4687-8dc0-0306685d5fd9\",\"status\":\"ASSIGNED\",\"deck\":\"7\",\"passenger_name\":\"test1\",\"passenger_surname\":\"testSurname1\",\"health_issues\":\"equip_required\",\"mobility_issues\":\"\",\"pregnancy_status\":\"\",\"xloc\":\"72.5425\",\"yloc\":\"34.2354\",\"geofence\":\"geofence 1\",\"timestamp\":\"2022-06-08 10:14:58.553\"},\"crew\":[{\"id\":\"7oUUQoEBJnVO1rPQzTkV\",\"name\":\"NikosCrew\",\"surname\":\"TestCrew\",\"hashedMacAddress\":\"\",\"emergencyRole\":\"medical_unit\",\"languages\":[\"IE\"],\"assigned\":false,\"geofence\":\"\",\"xloc\":\"\",\"yloc\":\"\"}],\"health_issues\":\"\",\"x_loc\":\"72.5425\",\"y_loc\":\"34.2354\",\"geofence\":\"geofence 1\"}";
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        PameasNotificationTO pameasNotificationTO = mapper.readValue(dashboardReceivedNotification, PameasNotificationTO.class);
        pameasNotificationTO.setType("PASSENGER_INCIDENT_ASSIGNMENT_ACK");
        kafkaService.writeToPameasNotification(pameasNotificationTO);
    }


    @Test
    public void mockBracelet() throws JsonProcessingException {

        BraceletPojo braceletPojo = new BraceletPojo();
        braceletPojo.setBraceletId("id1");
        braceletPojo.setBroadcast("true");
        braceletPojo.setMessageCode("code1");
        braceletPojo.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
        kafkaService.writeToBracelets(braceletPojo);
    }


    @Test
    public void mockCrewMemberInPosition() throws JsonProcessingException {

        PameasNotificationTO pameasNotificationTO = new PameasNotificationTO();
        pameasNotificationTO.setType("crew_member_position_update");
        pameasNotificationTO.setId("b356d0ea840b0550a2acb26acea468a80b895607d55db030f0b12abf5e8ce759"); //passenger hashmac
        kafkaService.writeToPameasNotification(pameasNotificationTO);
    }

    @Test
    public void mockDashBoardInitPassengerAlerting() throws JsonProcessingException {

        PameasNotificationTO pameasNotification = new PameasNotificationTO();
        pameasNotification.setStatus("");
        pameasNotification.setId(UUID.randomUUID().toString());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        pameasNotification.setTimestamp(timestamp.toString());
        pameasNotification.setType("INIT_PASSENGER_ALERTING");
        kafkaService.writeToPameasNotification(pameasNotification);
    }





}
