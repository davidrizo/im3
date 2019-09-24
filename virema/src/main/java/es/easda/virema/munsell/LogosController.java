package es.easda.virema.munsell;

import es.ua.dlsi.im3.core.utils.Histogram;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LogosController implements Initializable {
    @FXML
    VBox vboxHistogram;

    MunsellTreeModel munsellTreeModel;

    public LogosController() throws Exception {
        munsellTreeModel = new MunsellTreeModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void handleOpenImage() {
        OpenSaveFileDialog openSaveFileDialog = new OpenSaveFileDialog();
        File file = openSaveFileDialog.openFile("Open image", "png", "png");
        if (file != null) {
            try {
                openFile(file);
            } catch (IOException e) {
                ShowError.show(null, "Cannot read image", e);
            }
        }
    }

    private void openFile(File file) throws IOException {
        HashMap<RGBColor, MunsellColor> munsellColorHashMap = new HashMap<>();


        Histogram<MunsellColor> histogram = new Histogram<>();
        BufferedImage image = ImageIO.read(file);
        for (int j=0; j<image.getHeight(); j++) {
            for (int i=0; i<image.getWidth(); i++) {
                int clr=  image.getRGB(i,j);
                RGBColor rgbColor = new RGBColor(clr);
                MunsellColor munsellColor = munsellColorHashMap.get(rgbColor);
                if (munsellColor == null) {
                    munsellColor = munsellTreeModel.findClosest(rgbColor.getR(), rgbColor.getG(), rgbColor.getB());
                }
                histogram.addElement(munsellColor);
            }
        }

        vboxHistogram.getChildren().clear();
        for (MunsellColor rgbColor: histogram.getKeys()) {
            long count = histogram.getCountOfElement(rgbColor);
            HBox hBox = new HBox(3);
            vboxHistogram.getChildren().add(hBox);
            Rectangle rectangle = new Rectangle(20, 20, rgbColor.toColor());
            hBox.getChildren().add(rectangle);

            Label label = new Label(count + "");
            hBox.getChildren().add(label);
        }
    }
}
