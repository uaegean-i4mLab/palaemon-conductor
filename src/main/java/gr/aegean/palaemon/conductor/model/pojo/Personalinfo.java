package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Personalinfo implements Serializable {

    @Field(type = Text)
    private String name;
    @Field(type = Text)
    private String surname;
    @Field(type = Text)
    private String dateOfBirth;
    @Field(type = Text)
    private String gender;
    @Field(type = Text)
    private String personalId;
    @Field(type = Text)
    private String ticketNumber;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<TicketInfo> ticketInfo;
    @Field(type = Text)
    private boolean crew;


    @Field(type = Text)
    private String embarkationPort;
    @Field(type = Text)
    private String disembarkationPort;

    @Field(type = Text)
    private String email;
    @Field(type = Text)
    private String postalAddress;
    @Field(type = Text)
    private String emergencyContact;
    @Field(type = Text)
    private String countryOfResidence;

    @Field(type = Text)
    private String medicalCondition;
    @Field(type = Text)
    private String mobilityIssues;
    @Field(type = Text)
    private String prengencyData;

    @Field(type = Text)
    private String role;
    @Field(type = Text)
    private String emergencyDuty;



    @Field(type = FieldType.Text)
    private List<String> preferredLanguage;


    // New additions

    @Field(type = Text)
    private boolean inPosition;

    @Field(type = Text)
    private AssignmentStatus assignmentStatus;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<DutySchedule> dutyScheduleList;

    @Field(type = Text)
    private String assignedMusteringStation;

    @Field(type = Text)
    private String assignedPath;

    @Field(type = Text)
    private String oxygenSaturation;

    @Field(type = Text)
    private String hasFallen;

    @Field(type=Text)
    private String heartBeat;

    public enum AssignmentStatus {
        ASSIGNED("ASSIGNED"),
        UNASSIGNED("UNASSIGNED");
        private final String name;
        private AssignmentStatus(String s) {
            name = s;
        }
        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }
    }



}