package es.easda.virema.HCL;

import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class MusicHCLPaletteController implements Initializable {
    @FXML
    FlowPane flowPane;

    HCLColorGenerator hclColorGenerator;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            hclColorGenerator = new HCLColorGenerator();
            createPalettes();
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(null, "Error creating colors", e);
        }
    }

    private void createPalettes()  {
        VBox vBox = new VBox(5);
        VBox vBoxGrayscale = new VBox(5);
        VBox vBoxValues = new VBox(10);

        DecimalFormat df = new DecimalFormat("###.###");
        df.setMinimumFractionDigits(3);
        df.setMaximumFractionDigits(3);
        df.setMinimumIntegerDigits(3);
        df.setMaximumIntegerDigits(3);

        for (int lIndex = 0; lIndex < hclColorGenerator.getlCount(); lIndex++) {
            HBox hBox = new HBox(10);
            HBox hBoxGrayscale = new HBox(10);
            HBox hBoxValues = new HBox(15);
            vBox.getChildren().add(hBox);
            vBoxGrayscale.getChildren().add(hBoxGrayscale);
            vBoxValues.getChildren().add(hBoxValues);
            for (int hIndex = 0; hIndex < hclColorGenerator.gethCount(); hIndex++) {
                Rectangle rectangle = new Rectangle(40, 40, hclColorGenerator.getMusicPalette()[lIndex][hIndex]);
                hBox.getChildren().add(rectangle);


                Rectangle rectangleGrayscale = new Rectangle(40, 40, hclColorGenerator.getMusicPalette()[lIndex][hIndex].grayscale());
                hBoxGrayscale.getChildren().add(rectangleGrayscale);

                VBox vvalues = new VBox(3);
                hBoxValues.getChildren().add(vvalues);
                vvalues.getChildren().add(new Label("H=" + df.format(hclColorGenerator.getMusicPalette()[lIndex][hIndex].getHue())));
                vvalues.getChildren().add(new Label("S=" + df.format(hclColorGenerator.getMusicPalette()[lIndex][hIndex].getSaturation())));
                vvalues.getChildren().add(new Label("B=" + df.format(hclColorGenerator.getMusicPalette()[lIndex][hIndex].getBrightness())));
            }
        }
        this.flowPane.getChildren().add(0, vBoxGrayscale);
        this.flowPane.getChildren().add(0, vBoxValues);
        this.flowPane.getChildren().add(0, vBox);


    }

    /**
     *
     * @param h
     * @return
     * @throws Exception
     */
    private void createPalette(int hIndex, int h)  {
        VBox vBox = new VBox(5);
        this.flowPane.getChildren().add(vBox);
        Label label = new Label("H=" + h);
        vBox.getChildren().add(label);

        int lIndex = 0;
        for (int L=0; L<=HCLColor.MAX_L; L+=HCLColorGenerator.L_INCREMENT) {
            HBox hBox = new HBox(5);
            vBox.getChildren().add(hBox);
            int cIndex = 0;
            for (int C=0; C<=HCLColor.MAX_C; C+=HCLColorGenerator.C_INCREMENT) {
                HCLColor hclColor = new HCLColor(h, C, L);
                Color color = hclColor.toColor();

                hclColorGenerator.getColors()[hIndex][lIndex][cIndex] = color;

                Rectangle rectangle = new Rectangle(20, 20, color);
                hBox.getChildren().add(rectangle);
                cIndex ++;
            }
            lIndex++;
        }
    }
}
