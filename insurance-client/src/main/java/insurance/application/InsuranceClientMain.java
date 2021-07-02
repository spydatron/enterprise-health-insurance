package main.java.insurance.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class InsuranceClientMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxml = "insurance_client.fxml";
        URL url = getClass().getClassLoader().getResource(fxml);

        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root, 473, 384);
            final InsuranceClientController controller = loader.getController();

            // longrunning operation runs on different thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            //System.err.println("Platform runlater" + fxml);
                            controller.updateListView();
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

            primaryStage.setTitle("Hospital Treatment Client");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(new EventHandler<>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            primaryStage.show();
        } else {
            System.err.println("Error: Could not load frame from " + fxml);
        }

    }


    public static void main(String[] args) {
        launch(args);
    }

}
