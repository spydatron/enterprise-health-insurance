package main.java.insurance.application;

import main.java.insurance.model.TreatmentCostsReply;
import main.java.insurance.model.TreatmentCostsRequest;

public class ClientListLine {

    private TreatmentCostsRequest request;
    private TreatmentCostsReply reply;

    public ClientListLine(TreatmentCostsRequest request, TreatmentCostsReply reply) {
        this.request = request;
        this.reply = reply;
    }

    public TreatmentCostsRequest getRequest() {
        return request;
    }

    private void setRequest(TreatmentCostsRequest request) {
        this.request = request;
    }

    public TreatmentCostsReply getReply() {
        return reply;
    }

    public void setReply(TreatmentCostsReply reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return request.toString() + "  --->  " + ((reply != null) ? reply.toString() : "waiting...");
    }

}
