package main.java.broker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class InsuranceBrokerFrame extends Application {
    @Override
    public void start(Stage primaryStageBroker) throws Exception {
        String fxml = "insurance-broker.fxml";
        URL url  = getClass().getClassLoader().getResource( fxml );
//        System.out.println(url);
        if (url != null) {
            Parent root = FXMLLoader.load(url);
            primaryStageBroker.setTitle("Insurance Broker");
            primaryStageBroker.setScene(new Scene(root, 473, 384));
            primaryStageBroker.setOnCloseRequest(new EventHandler<>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });

            primaryStageBroker.show();
        } else {
            System.err.println("Error: Could not load frame from "+ fxml);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}
