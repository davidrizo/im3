package es.easda.virema.HCL;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class HarmonyHCLPaletteController implements Initializable {
    @FXML
    VBox vbox;

    @FXML
    TextField tfDivisions;

    @FXML
    TextField tfInitialH;

    @FXML
    TextField tfC;

    @FXML
    TextField tfL;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handleRecompute();
    }

    private void initPalette(int divisions, double initialH, double c, double l) {
        vbox.getChildren().clear();

        HCLColor [] colors = new HCLColor[divisions];

        for (int i=0; i<divisions; i++) {
            colors[i] = generateColor(i, divisions, initialH, c, l);
        }

        for (int i=0; i<divisions; i++) {
            Color color = colors[i].toColor();
            HBox hbox = new HBox(5);
            vbox.getChildren().add(hbox);
            Rectangle rectangle = new Rectangle(70, 70, color);

            double distance = colors[i].computeDistance(colors[(i+1)%divisions]);

            Text distanceText = new Text( "#" + i + "h=" + colors[i].getH() +
                    ", distance to next= " + distance + ", brighness=" + color.getBrightness());

            Color grayColor = color.grayscale();
            Rectangle grayRectangle = new Rectangle(70, 70, grayColor);

            hbox.getChildren().add(rectangle);
            hbox.getChildren().add(grayRectangle);
            hbox.getChildren().add(distanceText);

        }
    }

    private HCLColor generateColor(int index, int count, double initialH, double c, double l) {
        double h = initialH + (double)index * 360.0 / (double) count;

        return new HCLColor(h, c, l);
    }


    @FXML
    private void handleRecompute() {
        int divisions = Integer.parseInt(this.tfDivisions.getText());
        double initialH = Double.parseDouble(this.tfInitialH.getText());
        double c = Double.parseDouble(this.tfC.getText());
        double l = Double.parseDouble(this.tfL.getText());

        initPalette(divisions, initialH, c, l);
    }

}
