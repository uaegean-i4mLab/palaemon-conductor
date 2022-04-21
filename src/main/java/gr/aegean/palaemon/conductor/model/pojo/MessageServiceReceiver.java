package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageServiceReceiver {

    private String sender;
    private String recipient;
    private String target;
    private String language;
    private String textMsg;
    private String imageFile;
    private String videoFile;
    private String sessionId;
    private String msgType;
    private String assignmentType;
    private String pathId;
    private String assignedMusterStation;
    private String visualAid;
    private String global;

}
