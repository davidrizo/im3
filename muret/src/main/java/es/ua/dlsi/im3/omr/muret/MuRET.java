package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.useractionlogger.ActionLogger;
import es.ua.dlsi.im3.gui.useractionlogger.frontend.FXMLController;
import es.ua.dlsi.im3.omr.muret.old.KeyEventManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class MuRET extends Application {
    private static MuRET instance;
    private Stage mainStage;
    private KeyEventManager keyEventManager;
    private Model model;
    private Navigation navigation;
    private AgnosticSymbolFonts agnosticSymbolFonts;

    public static void main(String[] args) {
        launch(args);
    }

    public static final MuRET getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ActionLogger.init();
        agnosticSymbolFonts = new AgnosticSymbolFonts();
        navigation = new Navigation(primaryStage);
        instance = this;
        mainStage = primaryStage;
        Scene scene = new Scene(new Group());
        primaryStage.setScene(scene);

        keyEventManager = new KeyEventManager(scene);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/muret/start.fxml"));
        scene.setRoot(root);
        primaryStage.setMaximized(false);
        primaryStage.show();

        model = new Model();
    }


    public Stage getMainStage() {
        return mainStage;
    }

    public void openMrtFile(File mrtFile) throws IM3Exception {
        model.openProject(mrtFile.getParentFile());
    }

    public Model getModel() {
        return model;
    }

    public <ControllerType> ControllerType openWindow(String urlFXML, boolean maximize, boolean modal) {
        return navigation.openWindow(urlFXML, maximize, modal);
    }

    public void closeCurrentWindow() {
        navigation.closeCurrentWindow();
    }

    public AgnosticSymbolFonts getAgnosticSymbolFonts() {
        return agnosticSymbolFonts;
    }

    public void showUserInteractionLogs() {
        FXMLController fxmlController = openWindow("/fxml/useractionlogger/SceneUserActionLogger.fxml", true, true);
        fxmlController.readFolder(new File("actionlogs"));
    }
}
