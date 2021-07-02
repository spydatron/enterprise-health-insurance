package main.java.message_routing.gateway;

import common_services.gateway.MessageReceiverGateway;
import common_services.gateway.MessageSenderGateway;
import main.java.insurance.model.TreatmentCostsReply;
import main.java.insurance.model.TreatmentCostsRequest;
import main.java.insurance.serializer.TreatmentCostsSerializer;

import javax.jms.*;
import java.util.HashMap;

public abstract class InsuranceClientAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private TreatmentCostsSerializer serializer;
    private HashMap<String, TreatmentCostsRequest> requestHashMap;
    private HashMap<String, Message> hashMapMessages;
    private HashMap<String, Destination> destinationHashMap;

    public InsuranceClientAppGateway(){
        sender              = new MessageSenderGateway(DestinationQueue.INSURANCE_CLIENT_REPLY);
        receiver            = new MessageReceiverGateway(DestinationQueue.INSURANCE_CLIENT_REQUEST);
        serializer          = new TreatmentCostsSerializer();
        requestHashMap      = new HashMap<>();
        destinationHashMap  = new HashMap<>();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try{
                    String messageID = msg.getJMSMessageID();
                    msg.setJMSCorrelationID(messageID);
                    TextMessage textMsg = ((TextMessage) msg);
                    TreatmentCostsRequest request = serializer.requestFromString(textMsg.getText());

                    if (!requestHashMap.containsKey(messageID)) {
                        requestHashMap.put(messageID, request);
                        destinationHashMap.put(messageID, msg.getJMSReplyTo());
                    }

                    onTreatmentRequestArrived(request, messageID);
                    System.out.println("[INFO] Received Message from Client with id: " + msg.getJMSMessageID() + msg.getJMSCorrelationID() + ", and Correlation ID: " + msg.getJMSCorrelationID());

                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    public abstract void onTreatmentRequestArrived(TreatmentCostsRequest request, String messageID);

    public void sendTreatmentReply(TreatmentCostsReply reply, String correlationID){
        try{
            // get request with passed correlationID
            if (getRequest(correlationID) != null) {
                Message msg = sender.createTextMessage(serializer.replyToString(reply));
                msg.setJMSCorrelationID(correlationID);

                // get the return address
//                if(destinationHashMap.get(correlationID) != null)
//                    sender.send(destinationHashMap.get(correlationID), msg);
//                else
                    sender.send(msg);

                System.out.println("[INFO-BROKER] Booking Client Application Gateway Message: " + msg);
                System.out.println("------------------------------------------------------------");
            }
        }catch (JMSException e){e.printStackTrace();}
    }

    public TreatmentCostsRequest getRequest(String correlationID){
        TreatmentCostsRequest request =  requestHashMap.get(correlationID);
        System.out.println("[INFO] Request: " + request);
        return request;
    }

}
