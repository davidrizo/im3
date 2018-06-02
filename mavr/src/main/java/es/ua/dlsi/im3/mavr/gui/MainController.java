package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.utils.FileUtils;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
        File file = dlg.openFile("Open a music file", new String [] {"MEI", "MusicXML"}, new String [] {"mei", "xml"});
        if (file != null) {
            try {
                ScoreSong scoreSong = new ScoreSongImporter().importSong(file, FileUtils.getExtension(file));
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
        final double X_SCALE_FACTOR = 50;
        final double Y_SCALE_FACTOR = 3;

        Group keySignatureGroup = new Group();

        KeyView lastKey = new KeyView(model.getScoreSong().getUniqueKeyActiveAtTime(Time.TIME_ZERO));
        keySignatureGroup.getChildren().add(lastKey.getRoot());
        TonalFunctionView lastTonalFunctionView = null;

        for (Harm harm: model.getScoreSong().getOrderedHarms()) {
            double x = harm.getTime().getComputedTime()*X_SCALE_FACTOR;
            // draw key
            if (lastKey == null || !lastKey.getKey().equals(harm.getKey())) {
                if (lastKey != null) {
                    lastKey.setEndX(x);
                }
                lastKey = new KeyView(harm.getKey());
                lastKey.setX(x);
                keySignatureGroup.getChildren().add(lastKey.getRoot());
            }

            if (lastTonalFunctionView == null || !lastTonalFunctionView.getTonalFunction().equals(harm.getTonalFunction())) {
                if (lastTonalFunctionView != null) {
                    lastTonalFunctionView.setEndX(x);
                }
                lastTonalFunctionView = new TonalFunctionView(harm.getTonalFunction(), lastKey);
                lastTonalFunctionView.setX(x);
                keySignatureGroup.getChildren().add(lastTonalFunctionView.getRoot());

            }


            // draw tonal function
            /*if (lastTonalFunction == null || !lastTonalFunction.equals(harm.getTonalFunction())) {
                lastTonalFunction = harm.getTonalFunction();
                if (lastTonalFunctionRectangle != null) {
                    double toX = harm.getTime().getComputedTime()*X_SCALE_FACTOR;
                    finishTonalFunctionRectangle(lastTonalFunction, lastTonalFunctionText, lastKeyFromX, lastTonalFunctionRectangle, toX);
                }
                Rectangle rectangle = new Rectangle();
                lastTonalFunctionText = new Text(lastTonalFunction.getAbbr());
                keySignatureGroup.getChildren().add(rectangle);
                keySignatureGroup.getChildren().add(lastTonalFunctionText);
                lastTonalFunctionFromX = harm.getTime().getComputedTime()*X_SCALE_FACTOR;
                rectangle.setX(lastTonalFunctionFromX);
                rectangle.setY(0);
                rectangle.setHeight(50);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                lastTonalFunctionRectangle = rectangle;
            }*/

        }
        if (lastKey != null) {
            double toX = model.getScoreSong().getSongDuration().getComputedTime() * X_SCALE_FACTOR;
            lastKey.setEndX(toX);
        }

        if (lastTonalFunctionView != null) {
            double toX = model.getScoreSong().getSongDuration().getComputedTime() * X_SCALE_FACTOR;
            lastTonalFunctionView.setEndX(toX);
        }

        /*if (lastTonalFunctionRectangle != null) {
            double toX = model.getScoreSong().getSongDuration().getComputedTime() * X_SCALE_FACTOR;
            finishTonalFunctionRectangle(lastTonalFunction, lastTonalFunctionText, lastKeyFromX, lastTonalFunctionRectangle, toX);
        }*/

        mainPane.getChildren().add(keySignatureGroup);
        keySignatureGroup.setTranslateY(500);

        /*List<Motive> motives = model.getMotives();
        for (Motive motive: motives) {
            MotiveRepresentation motiveRepresentation = new LinearRhythmRepresentation(motive);
            MotiveRepresentationView view = new MotiveRepresentationView(motiveRepresentation);
            rhythmPane.getChildren().add(view);
        }*/

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
        rhythmGroup.setTranslateY(600);
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
        melodyGroup.setTranslateY(700);
        mainPane.getChildren().add(melodyGroup);
    }

    //draw tonic in the center, dominant in the corner, subdominant in the middle between both
    private void finishTonalFunctionRectangle(TonalFunction tonalFunction, Text lastTonalFunctionText, double lastKeyFromX, Rectangle lastTonalFunctionRectangle, double toX) throws IM3Exception {
        lastTonalFunctionRectangle.setWidth(toX - lastKeyFromX);
        /*switch (tonalFunction) {
            case TONIC:
                lastTonalFunctionText.xProperty().bind(lastTonalFunctionRectangle.xProperty().add(lastTonalFunctionRectangle.widthProperty().multiply(0.5)));
                lastTonalFunctionText.yProperty().bind(lastTonalFunctionRectangle.yProperty().add(lastTonalFunctionRectangle.heightProperty().multiply(0.5)));
                break;
            case DOMINANT:
                lastTonalFunctionText.xProperty().bind(lastTonalFunctionRectangle.xProperty().add(lastTonalFunctionRectangle.widthProperty().multiply(0.9)));
                lastTonalFunctionText.yProperty().bind(lastTonalFunctionRectangle.yProperty().add(lastTonalFunctionRectangle.heightProperty().multiply(0.1)));
                break;
            case SUBDOMINANT:
                lastTonalFunctionText.xProperty().bind(lastTonalFunctionRectangle.xProperty().add(lastTonalFunctionRectangle.widthProperty().multiply(0.7)));
                lastTonalFunctionText.yProperty().bind(lastTonalFunctionRectangle.yProperty().add(lastTonalFunctionRectangle.heightProperty().multiply(0.3)));
                break;
            default:
                throw new IM3Exception("Invalid tonal function: " + tonalFunction);
        }*/
        lastTonalFunctionText.xProperty().bind(lastTonalFunctionRectangle.xProperty());
        lastTonalFunctionText.yProperty().bind(lastTonalFunctionRectangle.yProperty().add(30));
    }

}
