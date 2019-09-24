package es.easda.virema;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class HCLController implements Initializable {
    @FXML
    Slider sliderH;
    @FXML
    Slider sliderC;
    @FXML
    Slider sliderL;
    @FXML
    Label labelValueH;
    @FXML
    Label labelValueC;
    @FXML
    Label labelValueL;

    @FXML
    Rectangle rectColor;
    @FXML
    Rectangle rectHue;
    @FXML
    Rectangle rectSaturation;
    @FXML
    Rectangle rectBright;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelValueH.textProperty().bind(sliderH.valueProperty().asString());
        labelValueC.textProperty().bind(sliderC.valueProperty().asString());
        labelValueL.textProperty().bind(sliderL.valueProperty().asString());

        ChangeListener<Number> changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                changeColor();
            }
        };

        sliderH.valueProperty().addListener(changeListener);
        sliderC.valueProperty().addListener(changeListener);
        sliderL.valueProperty().addListener(changeListener);
    }

    private Color grayscaleToColor(double grayscale, double max) {
        int gray = (int) (grayscale/max*255.0);
        return Color.rgb(gray, gray, gray );
    }

    private void changeColor() {
        Color color = ColorUtils.hcl2RGB(sliderL.getValue(), sliderC.getValue(), sliderH.getValue());
        rectColor.setFill(color);

        rectHue.setFill(grayscaleToColor(color.getHue(), 360.0));
        rectSaturation.setFill(grayscaleToColor(color.getSaturation(), 1.0));
        rectBright.setFill(grayscaleToColor(color.getBrightness(), 1.0));

    }
}
