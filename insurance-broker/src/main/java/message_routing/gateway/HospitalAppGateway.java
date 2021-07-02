package main.java.message_routing.gateway;

import main.java.hospital.model.HospitalCostsReply;
import main.java.hospital.model.HospitalCostsRequest;
import main.java.hospital.serializer.HospitalCostsSerializer;
import main.java.message_routing.aggregator.HospitalCostsAggregate;
import common_services.gateway.MessageReceiverGateway;
import common_services.gateway.MessageSenderGateway;
import main.java.message_routing.recipient_list.HospitalConnectionManager;
import main.java.message_routing.recipient_list.MessageRouter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;

public abstract class HospitalAppGateway {
    private MessageSenderGateway sender;
    private MessageReceiverGateway receiver;
    private HospitalCostsSerializer serializer;
    private HashMap<String, HospitalCostsRequest> requestHashMap;
    private HashMap<Integer, String> requestIdHashMap;
    private HashMap<HospitalCostsReply, Integer> replyHashMap;
    private HashMap<Integer, HospitalCostsAggregate> aggregatorHashMap;
    private HospitalConnectionManager recipientManager;
    protected int aggregationCorrelationID;

    public HospitalAppGateway() throws Exception {
        sender = new MessageSenderGateway("hospitalRequestQueue");
        receiver = new MessageReceiverGateway("hospitalReplyQueue");
        serializer = new HospitalCostsSerializer();
        requestHashMap = new HashMap<>();
        requestIdHashMap = new HashMap<>();
        replyHashMap = new HashMap<>();
        aggregatorHashMap = new HashMap<>();
        recipientManager = new HospitalConnectionManager();

        receiver.setListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try{
//                    String correlationID = message.getJMSCorrelationID();

                    HospitalCostsReply reply = serializer.replyFromString(((TextMessage) message).getText());
                    int aggregationCorrelationID = message.getIntProperty("aggregationID");
                    replyHashMap.put(reply, aggregationCorrelationID);

                    // print the received message to the console

                    System.out.println("[INFO] Hospital Application Gateway Message: " + message);
                    System.out.println("------------------------------------------------------------");

                    if (aggregatorHashMap.containsKey(aggregationCorrelationID))
                    {
                        HospitalCostsAggregate aggregator = aggregatorHashMap.get(aggregationCorrelationID);

                        aggregator.addMessage(reply);

                        if (aggregator.IsComplete())
                        {
                            HospitalCostsReply bestReply = aggregator.getBestReply();
                            System.out.println("[INFO] Hospital Application Gateway Message: Best Reply Received");
                            Message msg = sender.createTextMessage(serializer.replyToString(bestReply));
                            System.out.println("[INFO] Best reply: " + msg);
                            System.out.println("------------------------------------------------------------");

                            //aggregate.NotifyBestResult();
//                            String messageID = replyHashMap.get(bestReply);
                            int id = replyHashMap.get(bestReply);
                            String messageID = requestIdHashMap.get(id);
                            System.out.println("[INFO] Best reply CID: " + messageID);
                            onHospitalCostsReplyArrived(messageID, bestReply);
                            aggregatorHashMap.remove(aggregationCorrelationID);
                        }
                    }
                    else
                        System.out.println("Incoming bank response does not match any aggregate");

                }catch (Exception e){e.printStackTrace();}
            }
        });
    }
// ID:DESKTOP-IA92G6F-55605-1547436181151-1:1:1:1:4
    public void sendHospitalCostsRequest(HospitalCostsRequest request, String messageID){
        try{
            Message msg = null;
            msg = sender.createTextMessage(serializer.requestToString(request)); // store the request in the request hashmap

            msg.setIntProperty("aggregationID", aggregationCorrelationID);
            msg.setJMSReplyTo(receiver.getTemporaryQueue());
            requestIdHashMap.put(aggregationCorrelationID, messageID);

            MessageSenderGateway[] eligibleHospitals = recipientManager.getEligibleHospitalRequestQueues(request.getTreatmentCode(), request.getAge());

            aggregatorHashMap.put(aggregationCorrelationID, new HospitalCostsAggregate(aggregationCorrelationID, eligibleHospitals.length));

            aggregationCorrelationID = aggregatorHashMap.size() + 1;

            MessageRouter.sendToRecipientList(msg, eligibleHospitals);

            if (!requestHashMap.containsKey(request)) {
                requestHashMap.put(msg.getJMSMessageID(), request);
            }
            //sender.send(msg);   // send the request
            // print the received message to the console
            System.out.println("[INFO] Agency Application Gateway Message: " + msg);
            System.out.println("------------------------------------------------------------");
        }catch (JMSException e){e.printStackTrace();}
    }

    public abstract void onHospitalCostsReplyArrived(String correlationID, HospitalCostsReply reply);
}
