package es.ua.dlsi.grfia.im3.typographydesignhelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Used to help in the design of music typographies to be used in imcore
 */
public class TypographyDesignHelperApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(root);
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
