package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PassengerSpecialConditions {

    GAIT("assisted_gait"),
    WALKING_DISABILITY("walking_disability"),
    SEVER_WALKING_DISABILITY("severe_walking_disability"),
    UNABLE_TO_WALT("unable_to_walk"),
    VISUALLY_IMPAIRED("visually_impaired"),
    HEARING_IMPAIRED("hearing_impaired"),
    COGNITIVE_IMPAIRED("cognitive_impaired"),
    MEDICAL_EQUIP_NEEDED("equip_required"),
    STRETCHER("stretcher"),
    HEAVE_DOSES("heavy_doses"),
    COMPLICATED_PREGNANCY("complicated"),
    NORMAL_PREGNANCY("normal"),
    NO_CONDITION(""),
    NONE("none");


    private final String condition;

    PassengerSpecialConditions(String condition) {
        this.condition = condition;
    }

    @JsonValue
    public String getCondition() {
        return condition;
    }


}