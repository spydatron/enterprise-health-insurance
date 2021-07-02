package main.java.insurance.gateway;


import main.java.insurance.model.TreatmentCostsReply;
import main.java.insurance.model.TreatmentCostsRequest;
import main.java.insurance.serializer.TreatmentCostsSerializer;
import common_services.gateway.MessageReceiverGateway;
import common_services.gateway.MessageSenderGateway;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class InsuranceBrokerAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private TreatmentCostsSerializer serializer;
    private HashMap<String, TreatmentCostsRequest> requestHashMap;
    public int counter;

    public InsuranceBrokerAppGateway() throws Exception {
        sender = new MessageSenderGateway("insuranceClientRequestQueue");
        receiver = new MessageReceiverGateway("insuranceClientReplyQueue");
        serializer = new TreatmentCostsSerializer();
        requestHashMap = new HashMap<>();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {
                    TextMessage textMsg = ((TextMessage) msg);
                    TreatmentCostsReply reply = serializer.replyFromString(textMsg.getText());
                    onTreatmentCostReplyArrived(requestHashMap.get(msg.getJMSCorrelationID()), reply);

                } catch (JMSException e) {
                    System.out.println(e.getStackTrace());
                }

            }
        });
    }

    public void requestForTreatmentCost(TreatmentCostsRequest request) throws JMSException{
        Message msg = sender.createTextMessage(serializer.requestToString(request));
        sender.send(msg);
        String msgId = msg.getJMSMessageID();
        requestHashMap.put(msgId, request);
        System.out.println("[INFO] Request Message ID: " + msgId);
    }

    public abstract void onTreatmentCostReplyArrived(TreatmentCostsRequest request, TreatmentCostsReply reply);

}
