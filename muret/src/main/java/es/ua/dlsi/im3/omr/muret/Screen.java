package es.ua.dlsi.im3.omr.muret;

import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @autor drizo
 */
public class Screen {
    Stage stage;
    Initializable controller;

    public Screen(Stage stage, Initializable controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public Stage getStage() {
        return stage;
    }

    public Initializable getController() {
        return controller;
    }
}
