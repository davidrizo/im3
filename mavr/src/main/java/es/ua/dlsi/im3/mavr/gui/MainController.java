package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.gui.javafx.ViewLoader;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.mavr.model.Motive;
import es.ua.dlsi.im3.mavr.model.MotiveRepresentation;
import es.ua.dlsi.im3.mavr.model.rhythm.LinearRhythmRepresentation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    AnchorPane rhythmPane; //TODO Llevar a otro controller, como motives
    @FXML
    AnchorPane scoreViewPane;

    @FXML
    Tab tabMotives;

    private Model model;

    MotivesController motivesController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleMotivesOpened() {
        if (tabMotives.getContent() == null) {
            try {
                Pair<MotivesController, Parent> pair = ViewLoader.loadView("motives.fxml");
                motivesController = pair.getX();
                tabMotives.setContent(pair.getY());
            } catch (IOException e) {
                ShowError.show(MAVRApp.getMainStage(), "Cannot load motives view" , e);
            }
        }
    }

    @FXML
    private void handleOpen() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File file = dlg.openFile("Open a music file", "MusicXML", "xml");
        if (file != null) {
            try {
                ScoreSong scoreSong = new ScoreSongImporter().importSong(file, "xml");
                model = new Model(scoreSong);
                motivesController.setModel(model);
            } catch (Exception e) {
                ShowError.show(MAVRApp.getMainStage(), "Cannot import song", e);
            }
            try {
                represent();
            } catch (Exception e) {
                e.printStackTrace();
                ShowError.show(MAVRApp.getMainStage(), "Cannot represent song", e);
            }

        }
    }

    //TODO QUe se elija qué representamos
    //TODO Dibujar partitura y que se elijan los motivos
    private void represent() throws IM3Exception {
        List<Motive> motives = model.getMotives();
        for (Motive motive: motives) {
            MotiveRepresentation motiveRepresentation = new LinearRhythmRepresentation(motive);
            MotiveRepresentationView view = new MotiveRepresentationView(motiveRepresentation);
            rhythmPane.getChildren().add(view);
        }
    }
}
