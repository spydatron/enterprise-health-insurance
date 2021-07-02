package main.java.broker;

import main.java.message_routing.gateway.HospitalAppGateway;
import main.java.message_routing.gateway.InsuranceClientAppGateway;
import main.java.hospital.model.HospitalCostsReply;
import main.java.hospital.model.HospitalCostsRequest;
import main.java.insurance.model.TreatmentCostsReply;
import main.java.insurance.model.TreatmentCostsRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import main.java.model.BrokerListLine;

import javax.jms.JMSException;
import java.net.URL;
import java.util.ResourceBundle;


public class InsuranceBrokerController  implements Initializable {
    @FXML
    private ListView<BrokerListLine> lvRequestsReplies;

    private InsuranceClientAppGateway gatewayClient;
    private HospitalAppGateway gatewayHospital;
    private TreatmentCostsRequest treatmentRequest, treatmentReply;
    private HospitalCostsRequest hospitalRequest, hospitalReply;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            gatewayClient = new InsuranceClientAppGateway() {
                @Override
                public void onTreatmentRequestArrived(TreatmentCostsRequest request, String messageID) {
                    try{
                        // TODO Auto-generated method stub
                        System.out.print("[INFO-BROKER] Received Reply from broker");
                        treatmentRequest = request;
                        addToList(request);
                        HospitalCostsRequest hospitalCostsRequest;
                        hospitalCostsRequest = new HospitalCostsRequest(request.getSsn(), request.getTreatmentCode(), request.getAge());

                        gatewayHospital.sendHospitalCostsRequest(hospitalCostsRequest, messageID);

                    }catch (Exception je){je.printStackTrace();}
                }
            };
            gatewayHospital = new HospitalAppGateway() {
                @Override
                public void onHospitalCostsReplyArrived(String correlationID, HospitalCostsReply hospitalCostsReply) {
                    TreatmentCostsRequest request = gatewayClient.getRequest(correlationID);
                    addToList(request, hospitalCostsReply);

                    TreatmentCostsReply treatmentCostsReply = new TreatmentCostsReply(hospitalCostsReply.getPrice(),
                                                                                      hospitalCostsReply.getPrice(),
                                                                                      hospitalCostsReply.getHospitalName());
//                        gatewayClient.sendLoanReply(loanRequest, loanReply);
                    gatewayClient.sendTreatmentReply(treatmentCostsReply, correlationID);

                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BrokerListLine getRequestReply(TreatmentCostsRequest request){
        int size;
        size = lvRequestsReplies.getItems().size();

        for (int i = 0; i < size; i++){
            BrokerListLine bll = lvRequestsReplies.getItems().get(i);
            if (bll.getTreatmentCostRequest() == request){
                return bll;
            }
        }

        return null;
    }

    public void addToList(TreatmentCostsRequest request){
        lvRequestsReplies.getItems().add(new BrokerListLine(request));
    }

    public void addToList(TreatmentCostsRequest request, HospitalCostsReply reply){
        BrokerListLine bll = getRequestReply(request);
        if (bll != null && reply != null){
            bll.setHospitalCostsReply(reply);;
            lvRequestsReplies.refresh();
        }
    }
}
