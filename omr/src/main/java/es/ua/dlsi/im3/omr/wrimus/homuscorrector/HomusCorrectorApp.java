package es.ua.dlsi.im3.omr.wrimus.homuscorrector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomusCorrectorApp extends Application {
    private static Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        Parent root = FXMLLoader.load(getClass().getResource("/homuscorrector/homuscorrector.fxml"));
        scene.setRoot(root);
        primaryStage.setMaximized(false);
        primaryStage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}
