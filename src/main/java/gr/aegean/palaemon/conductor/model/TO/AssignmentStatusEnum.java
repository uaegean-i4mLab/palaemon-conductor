package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AssignmentStatusEnum {
    ASSIGNED("ASSIGNED"),
    FREE("UNASSIGNED");
    private final String name;

    private AssignmentStatusEnum(String s) {
        name = s;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}