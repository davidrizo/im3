package es.easda.virema.HCL;

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

public class HarmonyHCLPaletteFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        Parent root = FXMLLoader.load(getClass().getResource("/hcl/harmonyhclpalette.fxml"));
        primaryStage.setTitle("Visual Representation of Music Analyses - Harmony HCL Palette");
        scene.setRoot(root);
        primaryStage.show();


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit(); // close all threads
                System.exit(0);
            }
        });
    }
}
