package main.java.message_routing.recipient_list;

import common_services.gateway.MessageSenderGateway;
import main.java.message_routing.gateway.DestinationQueue;

import java.util.ArrayList;

public class HospitalConnectionManager {
    protected Hospital[] hospitals = {
            new Hospital("Catharina Ziekenhuis", Rule.CATHARINA, DestinationQueue.CATHARINA_REQUEST),
            new Hospital("Máxima Medisch Centrum", Rule.MAXIMA, DestinationQueue.MAXIMA_REQUEST),
            new Hospital("University Medical Center (UMC)", Rule.UMC, DestinationQueue.UMC_REQUEST)
    };

    public HospitalConnectionManager() {

    }


    public MessageSenderGateway[] getEligibleHospitalRequestQueues(String treatmentCode, int age) {
        ArrayList hospitalRequestList = new ArrayList();
        for (int i = 0; i < hospitals.length; i++) {
            if (hospitals[i].canHandleHospitalCostsRequest(treatmentCode, age)) {
                hospitalRequestList.add(hospitals[i].getQueue());
                System.out.println("[INFO] " + hospitals[i].getHospitalName() + " Agency, is eligible for this booking!");
            }
        }

        MessageSenderGateway[] hospitalRequestArray = new MessageSenderGateway[hospitalRequestList.size()];
        hospitalRequestList.toArray(hospitalRequestArray);

        return hospitalRequestArray;
    }
}
