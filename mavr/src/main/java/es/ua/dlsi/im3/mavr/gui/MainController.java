package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.javafx.ViewLoader;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    BorderPane mainBorderPane;
    @FXML
    ScrollPane mainScrollPane;
    @FXML
    Pane mainPane;
    @FXML
    AnchorPane rhythmPane; //TODO Llevar a otro controller, como motives
    @FXML
    Tab tabMotives;

    private Model model;

    MotivesController motivesController;
    private ScoreSongView scoreView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.prefHeightProperty().bind(mainScrollPane.heightProperty());
        mainPane.setPrefWidth(100000); // TODO
        mainScrollPane.prefWidthProperty().bind(mainBorderPane.widthProperty());
        mainScrollPane.prefHeightProperty().bind(mainBorderPane.heightProperty());
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
                addDocument(scoreSong);
            } catch (Exception e) {
                e.printStackTrace();
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

    private void addDocument(ScoreSong scoreSong) throws IM3Exception {
        mainPane.getChildren().clear();
        model = new Model(scoreSong);
        //motivesController.setModel(model);
        HorizontalLayout horizontalLayout = new HorizontalLayout(model.getScoreSong(), LayoutFonts.bravura, new CoordinateComponent(100000), new CoordinateComponent(500)); // TODO: 1/5/18
        scoreView = new ScoreSongView(horizontalLayout);
        mainPane.getChildren().add(scoreView.getMainPanel());
    }


    //TODO QUe se elija qué representamos
    //TODO Dibujar partitura y que se elijan los motivos
    //TODO No hacerlo todo aquí - además, que se exporte a SVG, PDF
    private void represent() throws IM3Exception {
        /*List<Motive> motives = model.getMotives();
        for (Motive motive: motives) {
            MotiveRepresentation motiveRepresentation = new LinearRhythmRepresentation(motive);
            MotiveRepresentationView view = new MotiveRepresentationView(motiveRepresentation);
            rhythmPane.getChildren().add(view);
        }*/

        final double X_SCALE_FACTOR = 50;
        final double Y_SCALE_FACTOR = 3;
        // rhythm representation
        Group rhythmGroup = new Group();
        for (Staff staff: model.getScoreSong().getStaves()) {
            for (ScoreLayer layer: staff.getLayers()) {
                for (Atom atom: layer.getAtomsSortedByTime()) {
                    Line line = new Line();
                    line.setStartX(atom.getTime().getComputedTime()*X_SCALE_FACTOR);
                    line.endXProperty().bind(line.startXProperty());
                    line.setStartY(0);
                    line.setEndY(25);
                    line.setStroke(Color.BLACK);
                    line.setStrokeWidth(1);
                    rhythmGroup.getChildren().add(line);
                }
            }
        }
        rhythmGroup.setTranslateY(300);
        mainPane.getChildren().add(rhythmGroup);


        // melody representation
        // skyline
        Group melodyGroup = new Group();
        for (Staff staff: model.getScoreSong().getStaves()) {
            for (ScoreLayer layer: staff.getLayers()) {
                for (Atom atom: layer.getAtomsSortedByTime()) {
                    if (atom.getAtomPitches() != null) {
                        for (AtomPitch pitch : atom.getAtomPitches()) {
                            double startX = pitch.getTime().getComputedTime() * X_SCALE_FACTOR;
                            double startY = Y_SCALE_FACTOR*(128 - pitch.getScientificPitch().computeMidiPitch());

                            double endX = pitch.getEndTime().getComputedTime() * X_SCALE_FACTOR;
                            double endY = startY;
                            Line line = new Line(startX, startY, endX, endY);
                            line.setStroke(Color.BLACK);
                            line.setStrokeWidth(1);
                            melodyGroup.getChildren().add(line);
                        }
                    }
                }
            }
        }
        melodyGroup.setTranslateY(600);
        mainPane.getChildren().add(melodyGroup);
    }
}
