package es.ua.dlsi.im3.mavr.gui;

import edu.stanford.vis.color.LAB;
import edu.stanford.vis.color.Util;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.algorithms.CombinationGenerator;
import es.ua.dlsi.im3.core.algorithms.ICombinationGeneratorPrunner;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.Key;
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
import es.ua.dlsi.im3.mavr.model.harmony.HarmonyColors;
import es.ua.dlsi.im3.mavr.model.harmony.NodeChordLabel;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.Executor;

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
    @FXML
    AnchorPane spiralArrayPane;
    @FXML
    AnchorPane colorWheelPane;
    @FXML
    FlowPane flowPaneColorWheels;
    @FXML
    AnchorPane colorSumPane;
    @FXML
    AnchorPane noteColorsPane;
    @FXML
    HBox keyColorsHBox;
    @FXML
    HBox hboxRatios;
    @FXML
    HBox hboxChords;
    @FXML
    HBox hboxProgression;
    @FXML
    BorderPane borderPaneDistances;
    @FXML
    TextField inputFromChord;
    @FXML
    TextField inputToChord;
    @FXML
    TextField inputFromKey;
    @FXML
    TextField inputToKey;
    @FXML
    Label labelTPSDistance;
    @FXML
    ColorPicker colorPickerFromChord;
    @FXML
    CheckBox cbFixHue;
    @FXML
    CheckBox cbFixSaturation;
    @FXML
    CheckBox cbFixBrightness;
    @FXML
    ColorPicker colorPickerToChord;
    @FXML
    BorderPane borderPaneGenetics;
    @FXML
    Button btnRunGenetics;
    @FXML
    LineChart<Number, Number> chartGenetics;
    private XYChart.Series geneticsFitnessSeries;

    Group circleColorsOfSum;

    private ObjectProperty<Model> modelProperty;

    MotivesController motivesController;
    private ScoreSongView scoreView;
    private XYChart.Series chordDistancesSeries;
    private XYChart.Series colorDistancesSeries;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        modelProperty = new SimpleObjectProperty<>();
        mainPane.prefHeightProperty().bind(mainScrollPane.heightProperty());
        mainPane.setPrefWidth(100000); // TODO
        mainScrollPane.prefWidthProperty().bind(mainBorderPane.widthProperty());
        mainScrollPane.prefHeightProperty().bind(mainBorderPane.heightProperty());

        drawColorWheels();
        drawSumColors();
        drawFrequencyColors();

        try {
            drawCOLORS();
            drawRatios();
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(null, "Cannot draw COLORS", e);
        }


        colorPickerFromChord.setValue(Color.RED);

    }

    //https://pages.mtu.edu/~suits/chords.html
    private void drawRatios() throws IM3Exception {
        int size = 150;
        ChordRatioColor chordRatioColor = new ChordRatioColor();
        double hue = 120;
        hboxRatios.getChildren().add(createRectangle("Maj 4:5:6", chordRatioColor.computeColor(hue,4,5,6), size));
        hboxRatios.getChildren().add(createRectangle("7th 20:25:30:36", chordRatioColor.computeColor(hue,20,25,30,36), size));
        hboxRatios.getChildren().add(createRectangle("Maj7 8:10:12:15", chordRatioColor.computeColor(hue,8,10,12,15), size));
        hboxRatios.getChildren().add(createRectangle("min 10:12:15", chordRatioColor.computeColor(hue,10,12,15), size));
        hboxRatios.getChildren().add(createRectangle("min7 10:12:15:18", chordRatioColor.computeColor(hue,10,12,15,18), size));
        hboxRatios.getChildren().add(createRectangle("dim 160:192:231", chordRatioColor.computeColor(hue, 160,192,231), size));
    }

    PitchClass [] pitchClasses = new PitchClass[] {
            PitchClasses.C.getPitchClass(),
            PitchClasses.D_FLAT.getPitchClass(),
            PitchClasses.D.getPitchClass(),
            PitchClasses.E_FLAT.getPitchClass(),
            PitchClasses.E.getPitchClass(),
            PitchClasses.F.getPitchClass(),
            PitchClasses.G_FLAT.getPitchClass(),
            PitchClasses.G.getPitchClass(),
            PitchClasses.A_FLAT.getPitchClass(),
            PitchClasses.A.getPitchClass(),
            PitchClasses.B_FLAT.getPitchClass(),
            PitchClasses.B.getPitchClass()
    };

    private Group createRectangle(String text, Color color, double size) {
        Rectangle rectangleMajor = new Rectangle(size, size, color);
        Text label = new Text(text);
        label.setX(rectangleMajor.getWidth()/2-label.getLayoutBounds().getWidth()/2);
        label.setY(label.getLayoutBounds().getHeight()+5);
        Group group = new Group(rectangleMajor, label);
        return group;
    }
    private Group createRectangle(Key key, KeyColorMapping keyColorMapping) throws IM3Exception {
        return createRectangle(key.getAbbreviationString(), keyColorMapping.getColor(key), 50);
    }

    private void drawCOLORS() throws IM3Exception {
        KeyColorMapping keyColorMapping = new KeyColorMapping();
        int pitch = 0;
        for (int i=0; i<12; i++) {
            Key majorKey = new Key(pitchClasses[pitch], Mode.MAJOR);
            Key minorKey = majorKey.computeRelativeMinor();
            System.out.println(majorKey + " " + minorKey);
            Group rectangleMajor = createRectangle(majorKey, keyColorMapping);
            Group rectangleMinor = createRectangle(minorKey, keyColorMapping);
            VBox vBox = new VBox(rectangleMajor, rectangleMinor);
            keyColorsHBox.getChildren().add(vBox);
            pitch = (pitch + 7) % 12; // circle of fifths
        }

        // draw chords based on the key color mapping
        drawChord(hboxChords, PitchClasses.C, PitchClasses.E);
        drawChord(hboxChords, PitchClasses.C, PitchClasses.F);
        drawChord(hboxChords, PitchClasses.C, PitchClasses.G);
        drawChord(hboxChords, PitchClasses.A, PitchClasses.C, PitchClasses.E);
        drawChord(hboxChords, PitchClasses.C, PitchClasses.E, PitchClasses.G);
        drawChord(hboxChords, PitchClasses.C, PitchClasses.E_FLAT, PitchClasses.G);
        drawChord(hboxChords, PitchClasses.C, PitchClasses.E, PitchClasses.G, PitchClasses.B_FLAT);
        drawChord(hboxChords, PitchClasses.F, PitchClasses.A, PitchClasses.C);
        drawChord(hboxChords, PitchClasses.G, PitchClasses.B, PitchClasses.D);
        drawChord(hboxChords, PitchClasses.G, PitchClasses.B, PitchClasses.D, PitchClasses.F);

        // draw progression
        drawChord(hboxProgression, PitchClasses.C, PitchClasses.E, PitchClasses.G);
        drawChord(hboxProgression, PitchClasses.F, PitchClasses.A, PitchClasses.C);
        drawChord(hboxProgression, PitchClasses.C, PitchClasses.E, PitchClasses.G);
        drawChord(hboxProgression, PitchClasses.C, PitchClasses.E, PitchClasses.G);
        drawChord(hboxProgression, PitchClasses.G, PitchClasses.B, PitchClasses.D);
        drawChord(hboxProgression, PitchClasses.C, PitchClasses.E, PitchClasses.G);
    }

    private void drawChord(HBox hbox, PitchClasses ... pitchClasses) throws IM3Exception {
        KeyColorMapping keyColorMapping = new KeyColorMapping();
        ArrayList<Color> colors = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<pitchClasses.length; i++) {
            Key key = new Key(pitchClasses[i].getPitchClass(), Mode.MAJOR);
            colors.add(keyColorMapping.getColor(key));
            if (i>0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(pitchClasses[i].getPitchClass().toString());
        }

        VBox vBox = new VBox(5);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().add(new Label(stringBuilder.toString()));
        vBox.getChildren().add(createCircleColors(colors, 30, 30, 60, 60));
        hbox.getChildren().add(vBox);
    }


    private void drawFrequencyColors() {
        double x=0;
        double width=20;
        for (int octave=0; octave<=8; octave++) {
            double brightness = (double)octave / 8.0;
            ColorWheel notesColorWheel = new ColorWheel(12, 1, brightness);
            for (int i=0; i<12; i++){
                ScientificPitch scientificPitch = new ScientificPitch(pitchClasses[i], octave);

                Rectangle rectangle = new Rectangle(x, 0, width, 200);
                rectangle.setFill(notesColorWheel.getColors()[i]);
                x += width;
                noteColorsPane.getChildren().add(rectangle);
            }
        }
    }

    private void drawSumColors() {
        ColorWheel colorWheel = new ColorWheel(12, 1, 1);
        ObjectProperty<Color> colorObjectProperty = new SimpleObjectProperty<>();
        Rectangle rectangle = new Rectangle(50, 50, 200, 200);
        rectangle.fillProperty().bind(colorObjectProperty);
        colorSumPane.getChildren().add(rectangle);

        Rectangle rectangleKey = new Rectangle(120, 50, 200, 200);
        colorSumPane.getChildren().add(rectangleKey);


        circleColorsOfSum = new Group();
        circleColorsOfSum.setTranslateX(300);
        circleColorsOfSum.setTranslateY(300);
        colorSumPane.getChildren().add(circleColorsOfSum);

        HBox hBoxNotes = new HBox(15);
        colorSumPane.getChildren().add(hBoxNotes);
        HBox hBoxKeys = new HBox(15);
        hBoxKeys.setTranslateY(20);
        colorSumPane.getChildren().add(hBoxKeys);
        TreeSet<Integer> selectedColors = new TreeSet<>();
        hBoxNotes.getChildren().add(new Label("Individual notes"));
        hBoxKeys.getChildren().add(new Label("Keys"));

        ToggleGroup keysToggleGroup = new ToggleGroup();

        for (int i=0; i<12; i++) {
            final int ii=i;
            CheckBox checkBox = new CheckBox(pitchClasses[i].toString());
            checkBox.setTextFill(colorWheel.getColors()[i]);
            hBoxNotes.getChildren().add(checkBox);
            checkBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // sum all selected colors
                    if (checkBox.isSelected()) {
                        selectedColors.add(ii);
                    } else {
                        selectedColors.remove(ii);
                    }

                    // create color
                    ArrayList<Color> colors = new ArrayList<>();
                    for (Integer icolor: selectedColors) {
                        colors.add(colorWheel.getColors()[icolor]);
                    }

                    colorObjectProperty.setValue(sumColors(colors));
                    try {
                        circleColorsOfSum.getChildren().clear();
                        circleColorsOfSum.getChildren().add(createCircleColors(colors, 100, 100, 200, 200));
                    } catch (IM3Exception e) {
                        e.printStackTrace();
                        //TODO
                    }
                }
            });

            RadioButton rbKey = new RadioButton(pitchClasses[i].toString());
            if (i==0) {
                rbKey.setSelected(true);
            }
            rbKey.setToggleGroup(keysToggleGroup);
            rbKey.setTextFill(colorWheel.getColors()[i]);
            rbKey.setUserData(colorWheel.getColors()[i]);
            hBoxKeys.getChildren().add(rbKey);
            rbKey.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Color color = (Color) keysToggleGroup.getSelectedToggle().getUserData();
                    rectangleKey.setFill(color);
                }
            });
        }


    }

    private Group createCircleColors(ArrayList<Color> colors, double centerX, double centerY, double radiusX, double radiusY) throws IM3Exception {
        Group result = new Group();

        ICombinationGeneratorPrunner prunner = new ICombinationGeneratorPrunner(){

            // check if any of the indexes is repeated
            public boolean isValid(int[] currentSolution, int ielement) {
                for (int i=0; i<ielement; i++) {
                    if (currentSolution[i] == currentSolution[ielement]) {
                        return false;
                    }
                }
                return true;
            }
        };

        // intentamos generar batidos combinando todas las secuencias de colores
        int [] elements = new int[colors.size()];
        for (int i=0; i<elements.length; i++) {
            elements[i] = colors.size();
        }
        CombinationGenerator cg = new CombinationGenerator();
        ArrayList<int[]> solutions =  cg.generateAllCombinations(elements, prunner);

        ArrayList<Color> mixedColors = new ArrayList<>();
        for (int [] solution: solutions) {
            for (int i=0; i<solution.length; i++) {
                Color color = colors.get(i);
                if (mixedColors.isEmpty() || !mixedColors.get(mixedColors.size()-1).equals(color)) {
                    mixedColors.add(color);
                }
            }
        }

        if (colors.size() > 0) {
            double angle = 360.0 / (double)mixedColors.size();
            for (int i = 0; i < mixedColors.size(); i++) {
                Arc arc = new Arc(centerX, centerY, radiusX, radiusY, angle * i, angle);
                Tooltip tooltip = new Tooltip("" + i);
                Tooltip.install(arc, tooltip);
                arc.setType(ArcType.ROUND);
                arc.setFill(mixedColors.get(i));
                result.getChildren().add(arc);
            }
        }
        return result;
    }

    private Color sumColors(ArrayList<Color> colors) {
        double r=0, g=0, b=0;
        for (Color color: colors) {
            r += color.getRed();
            g += color.getGreen();
            b += color.getBlue();
        }
        Color color = Color.rgb(
                (int)(255*(r/colors.size())),
                (int)(255*(g/colors.size())),
                (int)(255*(b/colors.size())));
        return color;
    }

    private void drawColorWheels() {
        flowPaneColorWheels.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        flowPaneColorWheels.prefWrapLengthProperty().bind(flowPaneColorWheels.widthProperty());

        for (double brightness = 1.0; brightness > 0; brightness -= 0.25) {
            for (double saturation = 1; saturation >= 0; saturation -= 0.25) {
                drawColorWheel(saturation, brightness);
            }
        }
    }

    private void drawColorWheel(double saturation, double brightness) {
        VBox vBox = new VBox();
        Group colorWheelGroup = new Group();
        vBox.getChildren().add(new Text("sat=" + saturation + ", bright=" + brightness));
        vBox.getChildren().add(colorWheelGroup);
        int divisions = 12;
        ColorWheel colorWheel = new ColorWheel(divisions, saturation, brightness);
        double angle = 360/divisions;
        for (int i=0; i<divisions; i++) {
            Arc arc = new Arc(100, 100, 200, 200, angle*i, angle);
            Tooltip tooltip = new Tooltip("" + i);
            Tooltip.install(arc, tooltip);
            arc.setType(ArcType.ROUND);
            arc.setFill(colorWheel.getColors()[i]);
            colorWheelGroup.getChildren().add(arc);
        }

        flowPaneColorWheels.getChildren().add(vBox);
    }

    @FXML
    private void handleMotivesOpened() {
        if (tabMotives != null) {
            if (tabMotives.getContent() == null) {
                try {
                    Pair<MotivesController, Parent> pair = ViewLoader.loadView("motives.fxml");
                    motivesController = pair.getX();
                    tabMotives.setContent(pair.getY());
                } catch (IOException e) {
                    ShowError.show(MAVRApp.getMainStage(), "Cannot load motives view", e);
                }
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
        modelProperty.setValue(new Model(scoreSong));
        //motivesController.setModel(model);
        HorizontalLayout horizontalLayout = new HorizontalLayout(modelProperty.get().getScoreSong(), new CoordinateComponent(100000), new CoordinateComponent(500)); // TODO: 1/5/18
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

        KeyView lastKey = new KeyView(modelProperty.get().getScoreSong().getUniqueKeyActiveAtTime(Time.TIME_ZERO));
        keySignatureGroup.getChildren().add(lastKey.getRoot());
        TonalFunctionView lastTonalFunctionView = null;

        for (Harm harm: modelProperty.get().getScoreSong().getOrderedHarms()) {
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
            double toX = modelProperty.get().getScoreSong().getSongDuration().getComputedTime() * X_SCALE_FACTOR;
            lastKey.setEndX(toX);
        }

        if (lastTonalFunctionView != null) {
            double toX = modelProperty.get().getScoreSong().getSongDuration().getComputedTime() * X_SCALE_FACTOR;
            lastTonalFunctionView.setEndX(toX);
        }

        /*if (lastTonalFunctionRectangle != null) {
            double toX = modelProperty.get().getScoreSong().getSongDuration().getComputedTime() * X_SCALE_FACTOR;
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
        for (Staff staff: modelProperty.get().getScoreSong().getStaves()) {
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
        for (Staff staff: modelProperty.get().getScoreSong().getStaves()) {
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

        drawSpiralArray();
    }


    //draw tonic in the center, dominant in the corner, subdominant in the middle between both
    private void finishTonalFunctionRectangle(TonalFunction tonalFunction, Text lastTonalFunctionText, double lastKeyFromX, Rectangle lastTonalFunctionRectangle, double toX) {
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

    private void drawSpiralArray() throws IM3Exception {
        /*subScene.widthProperty().bind(this.spiralArrayPane.widthProperty());
        subScene.heightProperty().bind(this.spiralArrayPane.heightProperty());

        double r = 50; // radius of the spiral, and h is the "rise" of the spiral.
        double h = 50; // h is the "rise" of the spiral

        Group spiralArrayGroup = new Group();
        this.spiralArrayPane.getChildren().clear();
        this.spiralArrayPane.getChildren().add(spiralArrayGroup);

        for (int pc=0; pc<12; pc++) {
            Sphere sphere1 = new Sphere();
            sphere1.setRadius(10.0);
            sphere1.setCullFace(CullFace.FRONT);

            // see https://en.wikipedia.org/wiki/Spiral_array_model
            double x = r * Math.sin(pc * Math.PI/2);
            double y = r * Math.cos(pc * Math.PI/2);
            double z = pc * h;

            sphere1.setTranslateX(x);
            sphere1.setTranslateY(y);
            sphere1.setTranslateZ(z);

            spiralArrayGroup.getChildren().add(sphere1);
        }
        spiralArrayGroup.setTranslateX(400);
        spiralArrayGroup.setTranslateY(400);

        Camera camera = new PerspectiveCamera(true);
        subScene.setCamera(camera);
        */
        SpiralArrayView spiralArrayView = new SpiralArrayView(this.spiralArrayPane.widthProperty(), this.spiralArrayPane.heightProperty());
        spiralArrayPane.getChildren().add(spiralArrayView.getRoot());

    }

    LAB createLABColor(Color fromColor) {
        LAB result = LAB.fromRGB((int) (fromColor.getRed() * 255.0), (int) (fromColor.getGreen() * 255.0), (int) (fromColor.getBlue() * 255.0), 0);
        System.out.println("From color = " + fromColor + ", LAB=" + result);
        return result;
    }

    private void F() {
        String [] pitches = new String[] {"C", "D", "E", "F", "G", "A", "B"};
        String [] modes = new String[] {"major", "minor"};

    }

    @FXML
    private void handleComputeChordDistance() {
        Chord chord1 = Chordanal.createHarmonyFromRelativeTones(inputFromChord.getText()); // C major chord
        Chord chord2 = Chordanal.createHarmonyFromRelativeTones(inputToChord.getText()); // C major chord
        Tone root1 = Chordanal.getRootTone(chord1);
        Tone root2 = Chordanal.getRootTone(chord2);
        org.harmony_analyser.jharmonyanalyser.chord_analyser.Key key1 = Chordanal.createKeyFromName(inputFromKey.getText());
        org.harmony_analyser.jharmonyanalyser.chord_analyser.Key key2 = Chordanal.createKeyFromName(inputToKey.getText());
        float tps = TonalPitchSpace.getTPSDistance(chord1, root1, key1, chord2, root2, key2, true);
        labelTPSDistance.setText("TPS distance=" + Float.toString(tps));

        // find a color
        //TODO fix an element
        Color fromColor = colorPickerFromChord.getValue();
        LAB fromLAB = createLABColor(fromColor);
        // loop, search a color, keep the one with distance closer to tps

        Color best = null;
        double bestDistance = 0;
        for (int hue = 0; hue < 360; hue++) {
            Color toColor = Color.hsb(hue, fromColor.getSaturation(), fromColor.getBrightness());
            LAB toLAB = createLABColor(toColor);
            double colorDistance = LAB.ciede2000(fromLAB, toLAB);
            //System.out.println("Distance from " + fromColor + " to " + toColor + " = " + colorDistance);
        }
    }

    private void drawGenetics() {
        btnRunGenetics.disableProperty().bind(modelProperty.isNull());

        // generation and fitness
        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        xAxis1.setLabel("Generation");
        yAxis1.setLabel("Fitness error value (log)");

        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis1,yAxis1);
        borderPaneGenetics.setTop(lineChart);
        lineChart.setTitle("Genetic algorithm evolution");
        //defining a series
        geneticsFitnessSeries = new XYChart.Series();
        geneticsFitnessSeries.setName("Fitness error value");
        lineChart.getData().add(geneticsFitnessSeries);
        lineChart.setPrefHeight(250);


        // draw distances between successive colors and chords
        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        xAxis2.setLabel("Harmonic sequence");
        yAxis2.setLabel("Chord and color distances");

        final LineChart<Number,Number> lineChart2 = new LineChart<>(xAxis2,yAxis2);
        borderPaneGenetics.setCenter(lineChart2);
        lineChart2.setTitle("Distances");
        lineChart2.setPrefHeight(250);
        //defining a series
        chordDistancesSeries = new XYChart.Series();
        chordDistancesSeries.setName("Chord distances");
        lineChart2.getData().add(chordDistancesSeries);

        colorDistancesSeries = new XYChart.Series();
        colorDistancesSeries.setName("Color distances");
        lineChart2.getData().add(colorDistancesSeries);

    }


    @FXML
    private void handleComputeGenetics() {
        drawGenetics();

        HBox hBox = new HBox(10);
        hBox.setPrefWidth(Double.MAX_VALUE);
        ScrollPane scrollPane = new ScrollPane(hBox);
        //scrollPane.setFitToWidth(true);
        scrollPane.prefViewportWidthProperty().bind(borderPaneGenetics.widthProperty());
        borderPaneGenetics.setBottom(scrollPane);
        ArrayList<ObjectProperty<Color>> chordColors = new ArrayList<>();

        try {
            HarmonyColors harmonyColors = new HarmonyColors(modelProperty.get().getScoreSong());

            for (NodeChordLabel nodeChordLabel: harmonyColors.getSonorityNodes()) {
                VBox vBox = new VBox(5);
                vBox.setFillWidth(true);
                vBox.setPrefWidth(150);
                vBox.setPrefHeight(300);
                hBox.getChildren().add(vBox);

                vBox.getChildren().add(new Label("#" + nodeChordLabel.getIndex()));
                vBox.getChildren().add(new Label("s:" + nodeChordLabel.getSonority().getScientificPitches()));
                vBox.getChildren().add(new Label("k:" + nodeChordLabel.getKey().getIM3Name()));
                vBox.getChildren().add(new Label("r:" + nodeChordLabel.getRoot().getNameMapped()));
                vBox.getChildren().add(new Label("c:" + nodeChordLabel.getChord().getToneNamesMapped()));

                SimpleObjectProperty<Color> color = new SimpleObjectProperty<>(Color.BLACK);
                chordColors.add(color);
                Rectangle rectangle = new Rectangle();
                rectangle.fillProperty().bind(color);
                rectangle.widthProperty().bind(vBox.widthProperty());
                rectangle.heightProperty().bind(rectangle.widthProperty());
                vBox.getChildren().add(rectangle);
            }

            SimpleDoubleProperty currentFitness = new SimpleDoubleProperty();
            SimpleIntegerProperty currentGeneration = new SimpleIntegerProperty();
            currentFitness.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    double logValue;
                    if (newValue.doubleValue() == 0.0) {
                        logValue = 0;
                    } else {
                        logValue = Math.log(newValue.doubleValue());
                    }
                    geneticsFitnessSeries.getData().add(new XYChart.Data(geneticsFitnessSeries.getData().size(), logValue));
                }
            });


            ArrayList<DoubleProperty> colorDistances = new ArrayList<>();
            ArrayList<DoubleProperty> chordDistances = new ArrayList<>();
            for (int i=1; i<chordColors.size(); i++) {
                DoubleProperty colorDistanceProperty = new SimpleDoubleProperty();
                XYChart.Data colorDistancePoint = new XYChart.Data(i-1, 0);
                colorDistancePoint.YValueProperty().bind(colorDistanceProperty);
                colorDistancesSeries.getData().add(colorDistancePoint);
                colorDistances.add(colorDistanceProperty);

                DoubleProperty chordDistanceProperty = new SimpleDoubleProperty();
                XYChart.Data chordDistancePoint = new XYChart.Data(i-1, 0);
                chordDistancePoint.YValueProperty().bind(chordDistanceProperty);
                chordDistancesSeries.getData().add(chordDistancePoint);
                chordDistances.add(chordDistanceProperty);

            }

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    harmonyColors.computeColors(chordColors, currentFitness, currentGeneration, colorDistances, chordDistances);
                    return null;
                }
            };
            new Thread(task).start();

        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(null, "Cannot compute genetics", e);
        }
    }

}
