package es.ua.dlsi.im3.mavr.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MAVRApp extends Application {
    private static Stage mainStage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/omrmainmensuraltagger.fxml"));

        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/omrmain.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setTitle("Music Visual Analysis Representation");
        scene.setRoot(root);
        //primaryStage.setMaximized(true);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit(); // close all threads
                System.exit(0);
            }
        });
    }

    public static Stage getMainStage() {
        return mainStage;
    }


}
