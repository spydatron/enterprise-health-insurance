package main.java.message_routing.recipient_list;

import common_services.gateway.MessageSenderGateway;

public abstract class HospitalConnection {
    protected MessageSenderGateway queue;
    protected String hospitalName = "";

    public HospitalConnection(String queueName) {
        this.queue = new MessageSenderGateway(queueName);
    }

    public MessageSenderGateway getQueue() {
        return queue;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public abstract boolean canHandleHospitalCostsRequest(String treatmentCode, int age);
}
