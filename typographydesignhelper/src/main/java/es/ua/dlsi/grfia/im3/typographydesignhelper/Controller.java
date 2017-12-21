package es.ua.dlsi.grfia.im3.typographydesignhelper;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Label labelCurrentFontFileName;
    @FXML
    Label labelJSONFilename;

    @FXML
    RadioButton cbModernNotation;
    @FXML
    RadioButton cbMensuralNotation;
    @FXML
    Spinner<Integer> spinnerKerning;
    @FXML
    Spinner<Integer> spinnerFontSize;
    @FXML
    Spinner<Integer> spinnerStaffLineThickness;
    @FXML
    TableColumn<Symbol, String> colSymbol;
    @FXML
    TableColumn<Symbol, String> colUnicode;
    @FXML
    TableView<Symbol> tbSymbols;
    @FXML
    AnchorPane scorePane;

    IntegerProperty kerning;
    IntegerProperty fontSize;

    Line[] lines;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelCurrentFontFileName.setVisible(false);
        labelJSONFilename.visibleProperty().bind(labelCurrentFontFileName.visibleProperty());
        spinnerKerning.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200));
        spinnerKerning.getValueFactory().setValue(0);

        spinnerFontSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 200));
        spinnerFontSize.getValueFactory().setValue(36);

        spinnerStaffLineThickness.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20));
        spinnerStaffLineThickness.getValueFactory().setValue(1);

        kerning  = new SimpleIntegerProperty();
        fontSize  = new SimpleIntegerProperty();
        kerning.bind(spinnerKerning.valueProperty());
        fontSize.bind(spinnerFontSize.valueProperty());

        initScorePane();
    }

    private void initScorePane() {
        drawStaff();
    }

    private void drawStaff() {
        lines = new Line[5];
        for (int i=0; i<lines.length; i++) {
            Line line = new Line();
            line.setStroke(Color.BLACK);
            line.strokeWidthProperty().bind(spinnerStaffLineThickness.valueProperty());
            line.setStartX(0);
            line.endXProperty().bind(scorePane.widthProperty());

            line.startYProperty().bind(fontSize.multiply(i));
            line.endYProperty().bind(line.startYProperty());
            scorePane.getChildren().add(line);
            lines[i] = line;
        }
    }
}
