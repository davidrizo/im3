package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

/**
 * This class is in charge of the navigation of the application
 * @autor drizo
 */
public class Navigation {
    Stack<Screen> screenStack;
    Stage mainStage;

    public Navigation(Stage mainStage) {
        this.mainStage = mainStage;
        this.screenStack = new Stack<>();
    }

    public <ControllerType> ControllerType openWindow(String urlFXML, boolean maximize, boolean modal)  {
        FXMLLoader fxmlLoader = new FXMLLoader(MuRET.class.getResource(urlFXML));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            Screen screen = new Screen(stage, fxmlLoader.getController());
            stage.setScene(scene);
            screenStack.push(screen);
            if (maximize) {
                stage.setMaximized(true);
            }
            stage.initOwner(mainStage.getOwner());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            return (ControllerType) screen.getController();
        } catch (IOException e) {
            ShowError.show(mainStage, "Cannot open window", e);
            return null;
        }
    }

    public void closeCurrentWindow() {
        if (screenStack.isEmpty()) {
            ShowError.show(mainStage, "Empty screen stack");
        } else {
            Screen screen = screenStack.pop();
            screen.getStage().close();
        }
    }
}
