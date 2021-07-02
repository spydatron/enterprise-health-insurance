package main.java.hospital.gateway;


import main.java.hospital.model.HospitalCostsReply;
import main.java.hospital.model.HospitalCostsRequest;
import main.java.hospital.serializer.HospitalCostsSerializer;
import common_services.gateway.MessageReceiverGateway;
import common_services.gateway.MessageSenderGateway;

import javax.jms.*;
import java.util.HashMap;

public abstract class InsuranceBrokerAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private HospitalCostsSerializer serializer;
    private HashMap<HospitalCostsRequest, Integer> requestHashMap;
    private HashMap<HospitalCostsRequest, String> requestIdHashMap;
    private HashMap<Integer, Destination> destinationHashMap;

    public InsuranceBrokerAppGateway(String requestQueue) throws Exception {
        System.out.println("[INFO-HOSPITAL]Insurance Broker Application Gateway =============");
        sender              = new MessageSenderGateway("hospitalReplyQueue");
        receiver            = new MessageReceiverGateway(requestQueue);
        serializer          = new HospitalCostsSerializer();
        requestHashMap      = new HashMap<>();
        requestIdHashMap    = new HashMap<>();
        destinationHashMap  = new HashMap<>();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    int aggregateCorrelationID = message.getIntProperty("aggregationID");
                    TextMessage textMsg = ((TextMessage) message);
                    HospitalCostsRequest request = serializer.requestFromString(textMsg.getText());

                    if (!requestHashMap.containsValue(aggregateCorrelationID)) {
                        requestHashMap.put(request, aggregateCorrelationID);
                        requestIdHashMap.put(request, message.getJMSMessageID());
                        destinationHashMap.put(aggregateCorrelationID, message.getJMSReplyTo());
                        System.out.println("[INFO-HOSPITAL] Booking Broker Application Gateway Message: " + message);
                        System.out.println("[INFO-HOSPITAL] Added to Hash Maps " + requestHashMap.get(request));
                        System.out.println("------------------------------------------------------------");
                    }

                    onHospitalCostReplyArrived(request);

                } catch (JMSException e) {
                    System.out.println(e.getStackTrace());
                }

            }
        });
    }

    public void sendHospitalCostsReply(HospitalCostsReply reply, HospitalCostsRequest request){
        try{
            Message msg = sender.createTextMessage(serializer.replyToString(reply));
            int aggregateCorrelationID = requestHashMap.get(request);
            String messageID = requestIdHashMap.get(request);
            System.out.println("[INFO-HOSPITAL] AID: " + aggregateCorrelationID + ", CID: " + messageID);
            msg.setIntProperty("aggregationID", aggregateCorrelationID);
            msg.setJMSCorrelationID(messageID);

            // get the return address
            if(destinationHashMap.get(aggregateCorrelationID) != null)
                sender.send(destinationHashMap.get(aggregateCorrelationID), msg);
            else
                sender.send(msg);

            System.out.println("[INFO-HOSPITAL] Booking Broker Application Gateway Message: " + msg);
            System.out.println("------------------------------------------------------------");

        }catch (JMSException e){e.printStackTrace();}
    }

    public abstract void onHospitalCostReplyArrived(HospitalCostsRequest request);

}
// ID:DESKTOP-IA92G6F-55519-1547433771623-1:1:1:1:2