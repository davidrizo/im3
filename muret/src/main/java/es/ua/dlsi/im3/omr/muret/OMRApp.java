package es.ua.dlsi.im3.omr.muret;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class OMRApp extends Application {
    private static Stage mainStage;
    private static KeyEventManager keyEventManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        keyEventManager = new KeyEventManager(scene);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/muret/dashboard.fxml"));
        scene.setRoot(root);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }
    public static KeyEventManager getKeyEventManager() {return keyEventManager; }
}
