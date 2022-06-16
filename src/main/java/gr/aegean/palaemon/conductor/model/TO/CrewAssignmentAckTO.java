package gr.aegean.palaemon.conductor.model.TO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CrewAssignmentAckTO {


    private AssignmentACK assignment;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentACK {
        private String type;
        private String id;
        private CrewMemberId[] crew;

    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CrewMemberId {
        private String id;
    }

}
