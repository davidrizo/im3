package es.easda.virema.munsell;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class LogosFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        Parent root = FXMLLoader.load(getClass().getResource("/munsell/logos.fxml"));
        primaryStage.setTitle("Visual Representation of Music Analyses - Munsell Tree");
        scene.setRoot(root);
        primaryStage.show();
        primaryStage.setMaximized(true);


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit(); // close all threads
                System.exit(0);
            }
        });
    }
}
