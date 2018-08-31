package es.ua.dlsi.im3.omr.muret;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public abstract class MuRETBaseController implements Initializable {
    @FXML
    ScrollPane scrollPane;

    @FXML
    Pane mainPane;

    private SimpleDoubleProperty scale;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.prefWidthProperty().bind(scrollPane.widthProperty());
        initZoom();
    }

    private void initZoom() {
        scale = new SimpleDoubleProperty(1);
        Scale scaleTransformation = new Scale();
        scaleTransformation.xProperty().bind(scale);
        scaleTransformation.yProperty().bind(scale);
        scaleTransformation.pivotXProperty().bind(mainPane.layoutXProperty());
        scaleTransformation.pivotYProperty().bind(mainPane.layoutYProperty());
        mainPane.getTransforms().add(scaleTransformation);
    }
    @FXML
    private void handleZoomIn() {
        scale.set(scale.get()+0.1);
    }

    @FXML
    private void handleZoomOut() {
        scale.set(scale.get()-0.1);
    }

    @FXML
    private void handleZoomReset() {
        scale.set(1);
    }

    @FXML
    protected void handleZoomToFit() {
        scale.set(1);
        double ratio = computeZoomToFitRatio();
        scale.set(ratio);
    }

    @FXML
    private void handleOpenDocumentAnalysis() {
        MuRET.openWindow("/fxml/muret/documentanalysis.fxml", true);
    }

    @FXML
    private void handleOpenSymbols() {
        MuRET.openWindow("/fxml/muret/symbols.fxml", true);
    }

    @FXML
    private void handleOpenMusic() {
        MuRET.openWindow("/fxml/muret/music.fxml", true);
    }


    protected abstract double computeZoomToFitRatio();
}
