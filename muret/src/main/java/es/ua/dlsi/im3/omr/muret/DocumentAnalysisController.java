package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class DocumentAnalysisController extends MuRETBaseController {
    @FXML
    ScrollPane scrollPane;

    @FXML
    Pane mainPane;

    OMRImage omrImage;

    ImageView imageView;

    public void loadOMRImage(OMRImage omrImage) throws IM3Exception {
        this.omrImage = omrImage;

        imageView = new ImageView(omrImage.getImage());
        imageView.setPreserveRatio(true);
        mainPane.setMinWidth(imageView.getImage().getWidth());
        mainPane.setMinHeight(imageView.getImage().getHeight());
        mainPane.getChildren().add(imageView);

        handleZoomToFit();
    }

    @Override
    protected double computeZoomToFitRatio() {
        double xRatio = this.scrollPane.getViewportBounds().getWidth() / this.imageView.getLayoutBounds().getWidth();
        double yRatio = this.scrollPane.getViewportBounds().getHeight() / this.imageView.getLayoutBounds().getHeight();
        if (xRatio > yRatio) {
            return xRatio;
        } else {
            return yRatio;
        }
    }
}
