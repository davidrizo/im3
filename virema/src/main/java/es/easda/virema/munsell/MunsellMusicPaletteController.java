package es.easda.virema.munsell;

import es.easda.virema.ColorCombinationCanvas;
import es.easda.virema.Synth;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Intervals;
import es.ua.dlsi.im3.core.score.PitchClass;
import es.ua.dlsi.im3.core.score.PitchClasses;
import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javax.sound.midi.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MunsellMusicPaletteController implements Initializable {
    @FXML
    VBox vBox;

    @FXML
    Slider sliderC;

    @FXML
    CheckBox cbMaximumPossibleC;

    @FXML
    VBox vboxIntervals;

    @FXML
    ToolBar toolbarOctaves;

    @FXML
    Slider sliderWidth;

    ArrayList<CheckBox> checkBoxes;

    MunsellTreeModel munsellTreeModel;
    MunsellColorGenerator munsellColorGenerator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            munsellTreeModel = new MunsellTreeModel();
            sliderC.valueProperty().addListener((observable, oldValue, newValue) -> initPalette());
            initMunsellMusicPalette();
            initPalette();
            initOctavesCheckBoxes();
            initIntervals();
        } catch (Throwable e) {
            e.printStackTrace();
            ShowError.show(null, "Cannot load Munsell tree data", e);
        }
    }

    private void initPalette() {
        int C = (int) sliderC.getValue();
        vBox.getChildren().clear();
        Font font = Font.font(8);
        for (int octave = 0; octave < 9; octave++) {
            vBox.getChildren().add(new Label("Octave #" + octave));
            HBox hBox = new HBox(10);
            vBox.getChildren().add(hBox);
            for (PitchClasses pitchClasses: PitchClasses.values()) {
                ScientificPitch scientificPitch = new ScientificPitch(pitchClasses.getPitchClass(), octave);
                int base40 = scientificPitch.computeBase40().getBase40Chroma();

                System.out.println(scientificPitch + " -> " + base40);
                String hueName = munsellTreeModel.getHues()[base40];

                VBox vBoxPitch = new VBox(3);
                hBox.getChildren().add(vBoxPitch);
                Label labelPitch = new Label(scientificPitch.toString());
                labelPitch.setFont(font);
                Label labelHue = new Label(hueName);
                labelHue.setFont(font);
                vBoxPitch.getChildren().add(labelPitch);
                vBoxPitch.getChildren().add(labelHue);

                Color color;
                if (cbMaximumPossibleC.isSelected()) {
                    color = getColorFromMaximumCPalette(octave, pitchClasses.getPitchClass());
                } else {
                    color = getColorForC(C, octave, hueName);
                }
                Rectangle rectangle = new Rectangle(30, 30, color);
                vBoxPitch.getChildren().add(0, rectangle);
            }
        }
    }

    private Color getColorFromMaximumCPalette(int octave, PitchClass pitchClass) {
        return munsellColorGenerator.getColor(octave, pitchClass);
    }

    private Color getColorForC(int C, int octave, String hueName) {
        List<MunsellColor> colorsHue = munsellTreeModel.getColors(hueName);
        final int v = octave;
        List<MunsellColor> lColors = colorsHue.stream().filter(munsellColor -> munsellColor.getV() == v && munsellColor.getC() == C).collect(Collectors.toList());
        Color color;
        if (lColors.size() > 0) {
            color = lColors.get(0).toColor();
        } else {
            color = Color.WHITE;
        }

        return color;
    }

    private void initMunsellMusicPalette() throws Exception {
        munsellColorGenerator = new MunsellColorGenerator(9);
    }


    @FXML
    private void handleChangeMaximumC() {
        initPalette();
    }

    private void initIntervals()  {
        vboxIntervals.getChildren().clear();
        Font font = Font.font(8);
        Font fontItv = Font.font("Helvetica", FontWeight.BLACK, 8);

        TreeSet<Intervals> intervals = new TreeSet<>((o1, o2) -> o1.getBase40Difference() - o2.getBase40Difference());

        for (Intervals itv: Intervals.values()) {
            intervals.add(itv);
        }
        for (int octave = 0; octave < 9; octave++) {
            if (checkBoxes.get(octave).isSelected()) {
                for (PitchClasses pitchClasses : PitchClasses.values()) {
                    if (Math.abs(pitchClasses.getAccidental().getAlteration()) <=1) {
                        ScientificPitch scientificPitch = new ScientificPitch(pitchClasses.getPitchClass(), octave);
                        HBox hBox = new HBox(5);
                        vboxIntervals.getChildren().add(hBox);
                        Text labelSP = new Text(scientificPitch.toString());
                        hBox.getChildren().add(labelSP);

                        Color color = this.munsellColorGenerator.getColor(scientificPitch);
                        for (Intervals itv: intervals) {
                            VBox vBoxInterval = new VBox(3);
                            hBox.getChildren().add(vBoxInterval);
                            Text textInterval = new Text(itv.getShortName());
                            textInterval.setFont(fontItv);
                            vBoxInterval.getChildren().add(textInterval);
                            try {
                                ScientificPitch scientificPitchTo = itv.createInterval().computeScientificPitchFrom(scientificPitch);
                                final int mp1 = scientificPitch.computeMidiPitch();
                                final int mp2 = scientificPitchTo.computeMidiPitch();
                                vBoxInterval.setOnMouseClicked(event -> {
                                    try {
                                        play(mp1, mp2);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                });

                                Text labelSPTo = new Text(scientificPitchTo.toString());
                                labelSPTo.setFont(font);

                                vBoxInterval.getChildren().add(labelSPTo);
                                Color colorTo = this.munsellColorGenerator.getColor(scientificPitchTo);

                                Rectangle rectangle = new Rectangle();
                                rectangle.setFill(color);
                                rectangle.widthProperty().bind(sliderWidth.valueProperty());
                                rectangle.heightProperty().bind(sliderWidth.valueProperty());

                                Rectangle rectangleTo = new Rectangle();
                                rectangleTo.setFill(colorTo);
                                rectangleTo.widthProperty().bind(sliderWidth.valueProperty());
                                rectangleTo.heightProperty().bind(sliderWidth.valueProperty());

                                ColorCombinationCanvas colorCombinationCanvas = new ColorCombinationCanvas(true, 1, false);
                                colorCombinationCanvas.setWidth(sliderWidth.getValue());
                                colorCombinationCanvas.setHeight(sliderWidth.getValue());
                                TreeMap<Integer, Color> colorTreeMap = new TreeMap<>();
                                colorTreeMap.put(scientificPitch.computeBase40().getBase40(), color);
                                colorTreeMap.put(scientificPitchTo.computeBase40().getBase40(), colorTo);
                                colorCombinationCanvas.setColors(colorTreeMap);

                                vBoxInterval.getChildren().add(rectangle);
                                vBoxInterval.getChildren().add(rectangleTo);
                                vBoxInterval.getChildren().add(colorCombinationCanvas);
                            } catch (Throwable t) {
                                // not used (e.g. interval descending from C0
                            }
                        }
                    }
                }
            }
        }
    }

    private void play(int midiPitch1, int midiPitch2) throws MidiUnavailableException, InterruptedException {
        ArrayList<Integer> midiNotes = new ArrayList<>();
        midiNotes.add(midiPitch1);
        midiNotes.add(midiPitch2);

        Synth synth = Synth.getInstance();
        for (Integer midiNote: midiNotes) {
            synth.noteOn(midiNote);
        }

        Thread.sleep(2000);
        for (Integer midiNote: midiNotes) {
            synth.noteOff(midiNote);
        }
    }

    private void initOctavesCheckBoxes() {
        checkBoxes = new ArrayList<>();
        for (int octave = 0; octave < 9; octave++) {
            CheckBox checkBox = new CheckBox("Octave " + octave);
            checkBox.setUserData(octave);

            toolbarOctaves.getItems().add(checkBox);
            checkBoxes.add(checkBox);

            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    initIntervals();
                }
            });


        }
    }




    private void initOctaveCheckbox(int octave) {
    }

}
