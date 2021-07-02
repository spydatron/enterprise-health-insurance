package main.java.model;

import main.java.hospital.model.HospitalCostsReply;
import main.java.hospital.model.HospitalCostsRequest;
import main.java.insurance.model.TreatmentCostsRequest;

public class BrokerListLine {

    private TreatmentCostsRequest treatmentCostsRequest;
    private HospitalCostsRequest hospitalCostsRequest;
    private HospitalCostsReply hospitalCostsReply;

    public BrokerListLine(TreatmentCostsRequest loanRequest) {
        this.setTreatmentCostsRequest(loanRequest);
    }

    public TreatmentCostsRequest getTreatmentCostRequest() {
        return treatmentCostsRequest;
    }

    public void setTreatmentCostsRequest(TreatmentCostsRequest treatmentCostsRequest) {
        this.treatmentCostsRequest = treatmentCostsRequest;
    }

    public HospitalCostsRequest getHospitalCostsRequest() {
        return hospitalCostsRequest;
    }

    public void setHospitalCostsRequest(HospitalCostsRequest bankRequest) {
        this.hospitalCostsRequest = bankRequest;
    }

    public HospitalCostsReply getHospitalCostsReply() {
        return hospitalCostsReply;
    }

    public void setHospitalCostsReply(HospitalCostsReply hospitalCostsReply) {
        this.hospitalCostsReply = hospitalCostsReply;
    }

    @Override
    public String toString() {
        return treatmentCostsRequest.toString() + " || " + ((hospitalCostsReply != null) ? hospitalCostsReply.toString() : "waiting for reply...");
    }
}
