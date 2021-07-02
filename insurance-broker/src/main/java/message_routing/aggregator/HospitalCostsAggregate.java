package main.java.message_routing.aggregator;

import main.java.event.CustomEventListener;
import main.java.event.CustomEventSource;
import main.java.hospital.model.HospitalCostsReply;

import java.util.ArrayList;
import java.util.EventObject;

//public abstract class HospitalCostsAggregate {
public class HospitalCostsAggregate {
    protected int aggregationCorrelationID;
    protected int expectedMessages;

    CustomEventSource eventSource;
    protected double bestRate = 0.0;
    protected ArrayList receivedMessages = new ArrayList();
    protected HospitalCostsReply bestReply = null;
    public HospitalCostsAggregate(int aggregationCorrelationID, int expectedMessages)
    {
        this.aggregationCorrelationID = aggregationCorrelationID;
        this.expectedMessages = expectedMessages;

        eventSource = new CustomEventSource();

// Register for MyEvents from c
        eventSource.addEventListener(new CustomEventListener() {
            @Override
            public void eventHandler(EventObject e) {
//                onBestHospitalCostsReply(bestReply);
            }
        });
    }
    public void addMessage(HospitalCostsReply reply){
        if (reply.getPrice() > 0)
        {
            if (bestReply == null)
            {
                bestReply = reply;
            }
            else
            {
                if (reply.getPrice() < bestReply.getPrice())
                {
                    bestReply = reply;
                }
            }
        }
        receivedMessages.add(reply);
    }

    public boolean IsComplete() {
        return receivedMessages.size() == expectedMessages;
    }

    public HospitalCostsReply getBestReply() {
        return bestReply;
    }

//    public abstract void onBestHospitalCostsReply(HospitalCostsReply bestReply);
}
