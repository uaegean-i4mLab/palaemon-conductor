package gr.aegean.palaemon.conductor.model.TO;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.palaemon.conductor.model.pojo.DutySchedule;
import gr.aegean.palaemon.conductor.model.pojo.Personalinfo;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonTO {
    /*
    {
      name: 'CLAUDE',
      surname: 'PHIL',
      identifier: 'EL/EL/11111',
      gender: 'Male',
      age: '1965-01-01',
      connectedPassengers: [
        {
          name: 'Nikos',
          surname: 'Triantafyllou',
          gender: "'Male'",
          age: '2007-05-10'
        },

      ],
      embarkation_port: 'pireaus',
      disembarkation_port: 'chios',
      ticketNumber: '123',
      email: 'triantafyllou.ni@gmail.com',
      postal_address: 'Kallistratous 50',
      emergency_contact_details: '6943808730',
      country_of_residence: 'GR',
      medical_condnitions: 'equip_required',
      mobility_issues: 'hearing_impaired',
      pregnency_data: 'normal',
      isCrew: 'false',
      role: 'passenger'


       isCrew: true,
      emergency_duty: 'firefighting_unit',

      preferred_language: [ 'IE' ],
      in_position: false,
      assignment_status: "UNASSIGNED",
      assigned_muster_station: null

    }





      */
    private String name;
    private String surname;
    private String gender;
    private String identifier;
    private String age;
    private List<ConnectedPersonTO> connectedPassengers;
    @JsonProperty("embarkation_port")
    private String  embarkationPort;
    @JsonProperty("disembarkation_port")
    private  String disembarkationPort;
    private String ticketNumber;

    private String email;
    @JsonProperty("postal_address")
    private String postalAddress;
    @JsonProperty("emergency_contact_details")
    private String emergencyContact;
    @JsonProperty("country_of_residence")
    private String countryOfResidence;

    @JsonProperty("medical_condnitions")
    private String medicalCondition;
    @JsonProperty("mobility_issues")
    private String mobilityIssues;
    @JsonProperty("pregnency_data")
    private String prengencyData;
    @JsonProperty("is_crew")
    private boolean isCrew;
    private String role;
    @JsonProperty("emergency_duty")
    private String emergencyDuty;

    @JsonProperty("duty_schedule")
    private List<DutySchedule> dutySchedule;

    @JsonProperty("preferred_language")
    private String[] preferredLanguage;

    // New additions
    @JsonProperty("in_position")
    private boolean inPosition;
    @JsonProperty("assignment_status")
    private Personalinfo.AssignmentStatus assignmentStatus;
    @JsonProperty("assigned_muster_station")
    private String assignedMusteringStation;

    private String heartBeat;
    private String saturation;
    private String bracelet;

}
