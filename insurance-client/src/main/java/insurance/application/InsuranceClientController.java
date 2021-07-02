package main.java.insurance.application;

import main.java.insurance.gateway.InsuranceBrokerAppGateway;
import main.java.insurance.model.TreatmentCostsReply;
import main.java.insurance.model.TreatmentCostsRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javax.jms.JMSException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class InsuranceClientController implements Initializable {
    @FXML
    private ListView<ClientListLine> lvRequestsReplies;
    @FXML
    private TextField tfSsn;
    @FXML
    private TextField tfAge;
    @FXML
    private TextField tfTreatmentCode;
    @FXML
    private CheckBox cbTransport;
    @FXML
    private TextField tfKilometers;

    private InsuranceBrokerAppGateway gateway;

    private ArrayList<ClientListLine> clientListLines;
//    rivate JList<RequestReply<LoanRequest,LoanReply>> requestReplyList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientListLines = new ArrayList<>();
        tfSsn.setText("123456");
        tfAge.setText("56");
        tfTreatmentCode.setText("ORT125");
        cbTransport.setSelected(false);
        tfKilometers.setDisable(true);

        try {
            gateway = new InsuranceBrokerAppGateway() {
                @Override
                public void onTreatmentCostReplyArrived(TreatmentCostsRequest request, TreatmentCostsReply reply) {
                    try{
                        // TODO Auto-generated method stub
                        System.out.print("[INFO-CLIENT] Received Reply from broker");

                        clientListLines.forEach((e)->{
                            if(e.getRequest() == request)
                                e.setReply(reply);
                        });
//                        ClientListLine listLine = getListLine(request);
//                        if (listLine != null && reply != null){
//                            clientListLines.add(new ClientListLine(request, reply));
//                        }

                        //updateListView();
//                        int nrOfElements = lvRequestsReplies.getItems().size();
//
//                        for (int i = 0; i < nrOfElements; i++) {
//                            ClientListLine requestReply = lvRequestsReplies.getItems().get(i);
//                            if (requestReply.getRequest() == request) {
//                                lvRequestsReplies.getItems().remove(i);
//                                lvRequestsReplies.getItems().add(i, new ClientListLine(request, reply));
//                                return;
//                            }
//                        }
                    }catch (Exception je){je.printStackTrace();}
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addMessageToListView(TreatmentCostsRequest request, TreatmentCostsReply reply) {
        try{
            int nrOfElements = lvRequestsReplies.getItems().size();

            for (int i = 0; i < nrOfElements; i++) {
                ClientListLine requestReply = lvRequestsReplies.getItems().get(i);
                if (requestReply.getRequest() == request) {
                    lvRequestsReplies.getItems().remove(i);
                    lvRequestsReplies.getItems().add(i, new ClientListLine(request, reply));
                    return;
                }
            }
        }catch (Exception je){je.printStackTrace();}
    }

    private ClientListLine getListLine(TreatmentCostsRequest request) {
        for (ClientListLine clientListLine : lvRequestsReplies.getItems()){
            if (clientListLine.getRequest() == request) {
                return clientListLine;
            }
        }
        return null;
    }

    public void transportChanged(){
        System.out.println(cbTransport.isSelected());
        if (!cbTransport.isSelected()){
            tfKilometers.setText("");
        }
        this.tfKilometers.setDisable(!this.cbTransport.isSelected());
    }

    public void btnSendClicked(){
        int ssn = Integer.parseInt(this.tfSsn.getText());
        String treatmentCode = this.tfTreatmentCode.getText();
        int age = Integer.parseInt(this.tfAge.getText());

        int transportDistance = 0;
        if (this.cbTransport.isSelected()) {
            transportDistance = Integer.parseInt(this.tfKilometers.getText());
        }

        TreatmentCostsRequest request = new TreatmentCostsRequest(ssn, age, treatmentCode, transportDistance);
        //... send request
        try {
            gateway.requestForTreatmentCost(request);
        }catch (JMSException ex){
            System.out.println(ex.getStackTrace());
        };
        addToList(request);

    }

    public void addToList(TreatmentCostsRequest request){
        clientListLines.add(new ClientListLine(request, null));
        lvRequestsReplies.getItems().add(new ClientListLine(request, null));
    }

    public void addToList(TreatmentCostsRequest request, TreatmentCostsReply reply){
        ClientListLine listLine = getListLine(request);
        if (listLine != null && reply != null){
            listLine.setReply(reply);
            lvRequestsReplies.refresh();
        }
    }

    public void updateListView(){
        clientListLines.forEach((e)->{
            for (ClientListLine clientListLine : lvRequestsReplies.getItems()){
                if (clientListLine.getRequest() == e.getRequest()) {
                    lvRequestsReplies.getItems().remove(clientListLine);
                    lvRequestsReplies.getItems().add(e);
                    break;
                }
            }
        });
        lvRequestsReplies.refresh();
    }
}
