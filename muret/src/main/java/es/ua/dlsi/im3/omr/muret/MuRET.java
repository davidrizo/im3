package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MuRET extends Application {
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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/muret/start.fxml"));
        scene.setRoot(root);
        primaryStage.setMaximized(false);
        primaryStage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }
    public static KeyEventManager getKeyEventManager() {return keyEventManager; }

    public static <ControllerType> ControllerType openWindow(String urlFXML, boolean maximize)  {
        FXMLLoader fxmlLoader = new FXMLLoader(MuRET.class.getResource(urlFXML));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            MuRET.getMainStage().setScene(scene);
            MuRET.getMainStage().setMaximized(maximize);
            return fxmlLoader.getController();
        } catch (IOException e) {
            ShowError.show(MuRET.getMainStage(), "Cannot open window", e);
            return null;
        }
    }
}
