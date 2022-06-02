package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CrewEmergencyRolesEnum {

    COMMAND_TEAM("command_team"),
    FIRST_RESPONSE_UNIT("first_response_unit"),
    BOAT_PREPARATION_UNIT("boat_preparation_unit"),
    PASSENGER_MUSTERING_UNIT("passenger_mustering_unit"),
    FIREFIGHTING_UNIT("firefighting_unit"),
    DAMAGE_CONTROL_UNIT("damage_control_unit"),
    FIRST_AID_UNIT("first_aid_unit"),
    MEDICAL_UNIT("medical_unit"),
    PASSENGER_ASSISTANCE_UNIT("passenger_assistance_units"),
    CABIN_SEARCH_UNIT("cabin_searching_unit");


    private final String emergencyRole;

    CrewEmergencyRolesEnum(String emergencyRole) {
        this.emergencyRole = emergencyRole;
    }


    @JsonValue
    public String getEmergencyRole() {
        return emergencyRole;
    }

}
