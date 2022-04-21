package gr.aegean.palaemon.conductor.service;

import gr.aegean.palaemon.conductor.model.pojo.MessageBody;

import java.util.List;

public interface MessagingServiceCaller {

    public void callSendMessages(List<MessageBody> bodies);
}
