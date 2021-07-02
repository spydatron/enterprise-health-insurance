package main.java.hospital.application;

import main.java.hospital.gateway.InsuranceBrokerAppGateway;
import main.java.hospital.model.Address;
import main.java.hospital.model.HospitalCostsReply;
import main.java.hospital.model.HospitalCostsRequest;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HospitalController implements Initializable {

    @FXML
    private Label lbHospital;
    @FXML
    private Label lbAddress;
    @FXML
    private TextField tfPrice;
    @FXML
    private ListView<HospitalListLine> lvRequestReply;
    @FXML
    private Button btnSendReply;

    private final String hospitalName;
    private final Address address;
    private final String hospitalRequestQueue;
    private InsuranceBrokerAppGateway gateway;
    private ArrayList<HospitalListLine> hospitalListLines;



    public HospitalController(String hospitalName, Address address, String hospitalRequestQueue) {
        this.address                = address;
        this.hospitalName           = hospitalName;
        this.hospitalRequestQueue   = hospitalRequestQueue;
        hospitalListLines = new ArrayList<>();
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String fullAddress = this.address.getStreet() + " " + this.address.getNumber() + ", " + this.address.getCity();
        this.lbAddress.setText(fullAddress );
        this.lbHospital.setText(this.hospitalName);

        try {
            gateway = new InsuranceBrokerAppGateway(this.hospitalRequestQueue) {
                @Override
                public void onHospitalCostReplyArrived(HospitalCostsRequest request) {
                    System.out.println("[INFO-HOSPITAL <" + hospitalName + "> ] " + " Notification =============");
                    addToList(request);
                }
            };
        } catch (Exception e) { e.printStackTrace();}

        btnSendReply.setOnAction(event -> {
            sendHospitalReply();
        });
    }

    @FXML
    public void sendHospitalReply(){
        HospitalListLine listLine = this.lvRequestReply.getSelectionModel().getSelectedItem();
        if (listLine != null) {
            double price = Double.parseDouble(tfPrice.getText());
            HospitalCostsReply reply = new HospitalCostsReply(price, this.hospitalName, this.address);
            listLine.setReply(reply);
            lvRequestReply.refresh();
            // send the reply ...
            gateway.sendHospitalCostsReply(reply, listLine.getRequest());
        }
    }

    public void addToList(HospitalCostsRequest request){
        hospitalListLines.add(new HospitalListLine(request, null));
        //lvRequestReply.getItems().add(new HospitalListLine(request, null));
    }


    public void updateListView(){
        hospitalListLines.forEach((e)->{
            if (!lvRequestReply.getItems().contains(e)) {
                lvRequestReply.getItems().add(e);
            }
        });
        lvRequestReply.refresh();
    }
}
