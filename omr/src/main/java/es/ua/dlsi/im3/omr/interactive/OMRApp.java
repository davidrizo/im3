package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/omrmain.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("MURET");

        mainStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
