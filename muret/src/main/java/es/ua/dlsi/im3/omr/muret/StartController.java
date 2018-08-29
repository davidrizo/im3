package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class StartController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleOpenProject() {
        openWindow("/fxml/muret/openproject.fxml", false);
    }

    private void openWindow(String urlFXML, boolean maximize)  {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource(urlFXML));
            Scene scene = new Scene(root);
            MuRET.getMainStage().setScene(scene);
            MuRET.getMainStage().setMaximized(maximize);
        } catch (IOException e) {
            ShowError.show(MuRET.getMainStage(), "Cannot open window", e);
        }
    }
}
