package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OMRApp extends Application {
    private static Stage mainStage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        // TODO: 9/10/17 Created in order to have the init in OMRMainController a scene to attach key handlers
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/omrmainmensuraltagger.fxml"));

        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/omrmain.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/pages.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
        scene.setRoot(root);
        primaryStage.setTitle("MURET");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
