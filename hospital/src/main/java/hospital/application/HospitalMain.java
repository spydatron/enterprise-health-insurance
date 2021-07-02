package main.java.hospital.application;

import main.java.hospital.model.Address;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import main.java.hospital.application.HospitalController;
import main.java.hospital.application.HospitalController;

public class HospitalMain  extends Application {

    private Address address;
    private String hospitalName;
    private String hospitalRequestQueue;

    public HospitalMain(Address address, String hospitalName, String hospitalRequestQueue) throws IllegalArgumentException{
        if (address == null){
            throw new IllegalArgumentException("Hospital address is empty.");
        }
        if (hospitalName == null){
            throw new IllegalArgumentException("Hospital name is empty.");
        }
        if (hospitalRequestQueue == null){
            throw new IllegalArgumentException("Hospital request queue name is empty.");
        }
        this.address = address;
        this.hospitalName = hospitalName;
        this.hospitalRequestQueue = hospitalRequestQueue;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxml = "hospital.fxml";
        URL url  = getClass().getClassLoader().getResource( fxml );
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            HospitalController hospitalController =new HospitalController(this.hospitalName,this.address, this.hospitalRequestQueue);
            loader.setController(hospitalController);
            Parent root = loader.load();

            // longrunning operation runs on different thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            hospitalController.updateListView();
                        }
                    };

                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                        }
                        // UI update is run on the Application thread
                        Platform.runLater(updater);
                    }
                }
            });
            // don't let thread prevent JVM shutdown
            thread.setDaemon(true);
            thread.start();

            primaryStage.setTitle(hospitalName);
            primaryStage.setScene(new Scene(root, 388, 295));
            primaryStage.setOnCloseRequest(new EventHandler<>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            primaryStage.show();
        } else {
            System.err.println("Error: Could not load frame from "+ fxml);
        }
    }
}
