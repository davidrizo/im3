package es.ua.dlsi.im3.gui.score.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScoreEditApp extends Application {
    private static Stage mainStage;
    ScoreEditController controller;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        mainStage = primaryStage;
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scoredit.fxml"));
        scene.setRoot(root);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
