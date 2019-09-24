package es.easda.virema.HCL;

import es.easda.virema.ColorCombinationCanvas;
import es.easda.virema.Sonorities;
import es.easda.virema.SonoritiesGenerator;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Segment;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

public class NoHarmonicsController implements Initializable {
    private static final int RECTANGLE_HEIGHT = 400;
    @FXML
    BorderPane borderPane;

    @FXML
    HBox hboxNotes;

    @FXML
    HBox hboxChords;

    @FXML
    CheckBox cbPatternColors;

    @FXML
    CheckBox cbRegularPattern;

    @FXML
    Slider sliderPatternSize;

    @FXML
    Slider widthSlider;

    Font chordFont = Font.font(8);

    HCLColorGenerator colorGenerator;
    private NavigableMap<Integer, Color> colors;
    private Sonorities sonorities;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            colorGenerator = new HCLColorGenerator();
            colors = this.colorGenerator.generateColors();
            initNotes();
            sliderPatternSize.disableProperty().bind(cbPatternColors.selectedProperty().not());
            cbRegularPattern.disableProperty().bind(cbPatternColors.selectedProperty().not());
            sliderPatternSize.valueProperty().addListener((observable, oldValue, newValue) -> paint());
            widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> paint());
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(null, "Cannot build palette", e);
        }
    }

    private void initNotes() {
        Font font = Font.font(7);
        NavigableMap<Integer, Color> colors = colorGenerator.generateColors();
        for (Map.Entry<Integer, Color> colorEntry: colors.entrySet()) {
            VBox vBox = new VBox(1);
            vBox.setAlignment(Pos.TOP_CENTER);
            hboxNotes.getChildren().add(vBox);

            Rectangle rectangle = new Rectangle(20, 60, colorEntry.getValue());
            vBox.getChildren().add(rectangle);

            Label label = new Label(colorEntry.getKey().toString());
            label.setFont(font);
            vBox.getChildren().add(label);

            if (colorEntry.getKey() % 12 == 0) {
                Label labelOctave = new Label("O-"+(colorEntry.getKey()/12));
                labelOctave.setFont(font);
                vBox.getChildren().add(labelOctave);
            }
        }
    }

    @FXML
    private void handleOpenMusicXML() {
        try {
            SonoritiesGenerator sonoritiesGenerator = new SonoritiesGenerator();
            sonorities = sonoritiesGenerator.generateSonorities();
            paint();
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(null, "Cannot open file", e);
        }

    }

    @FXML
    private void handlePatternColorsChanged() {
        this.paint();
    }

    @FXML
    private void handleOnRegularPatternChange() {
        this.paint();
    }

    private void paint() {
        if (sonorities != null) {
            hboxChords.getChildren().clear();
            List<Segment> segments = sonorities.getSegments();
            ScoreSong scoreSong = sonorities.getScoreSong();
            for (Segment segment : segments) {
                addRectangle(scoreSong, segment);
            }
        }
    }

    private void addRectangle(ScoreSong scoreSong, Segment segment) {
        VBox vBox = new VBox();
        hboxChords.getChildren().add(vBox);


        List<AtomPitch> pitches = scoreSong.getAtomPitchesWithOnsetWithin(segment);
        int width = (int) (segment.getDuration().getExactTime().doubleValue() * widthSlider.getValue());

        if (pitches.isEmpty()) {
            Rectangle rectangle = new Rectangle(width, RECTANGLE_HEIGHT, Color.TRANSPARENT);
            vBox.getChildren().add(rectangle);
        } else {
            ColorCombinationCanvas colorCombinationCanvas = new ColorCombinationCanvas(cbPatternColors.isSelected(), (int) sliderPatternSize.getValue(), cbRegularPattern.isSelected());
            colorCombinationCanvas.setWidth(width);
            colorCombinationCanvas.setHeight(RECTANGLE_HEIGHT);
            TreeMap<Integer, Color> segmentColors = new TreeMap<>();
            for (AtomPitch atomPitch : pitches) {
                int pitch = atomPitch.getScientificPitch().computeMidiPitch();
                segmentColors.put(pitch, colors.get(pitch));
            }
            colorCombinationCanvas.setColors(segmentColors);
            vBox.getChildren().add(colorCombinationCanvas);

            VBox vBoxLabels = new VBox();
            vBoxLabels.setAlignment(Pos.TOP_CENTER);
            vBox.getChildren().add(vBoxLabels);

            for (AtomPitch atomPitch : pitches) {
                Label label = new Label(atomPitch.getScientificPitch().toString());
                label.setFont(chordFont);
                vBoxLabels.getChildren().add(0, label); // apilo abajo arriba
            }
                /*int rectangleHeight = RECTANGLE_HEIGHT / pitches.size();
                for (AtomPitch atomPitch : pitches) {
                    Rectangle rectangle = new Rectangle(width, rectangleHeight,
                            colors.get(atomPitch.getScientificPitch().computeMidiPitch()));
                    vBox.getChildren().add(0, rectangle); // apilo abajo arriba
                }*/
        }

    }
}
