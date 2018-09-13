package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.interaction.ISelectableTraversable;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.model.IOMRBoundingBox;
import es.ua.dlsi.im3.omr.muret.model.OMRImage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public abstract class MuRETBaseController implements Initializable, ISelectableTraversable {
    OMRImage omrImage;

    private SimpleDoubleProperty scale;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initZoom();
    }

    private void initZoom() {
        scale = new SimpleDoubleProperty(1);
        Scale scaleTransformation = new Scale();
        scaleTransformation.xProperty().bind(scale);
        scaleTransformation.yProperty().bind(scale);

        bindZoom(scaleTransformation);
    }

    protected abstract void bindZoom(Scale scaleTransformation);

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
    private void handleOpenSymbols() {
        DocumentAnalysisSymbolsDiplomaticMusicController symbolsController = MuRET.getInstance().openWindow("/fxml/muret/symbols.fxml", true, true);
        try {
            symbolsController.loadOMRImage(omrImage);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load symbols screen", e);
            ShowError.show(MuRET.getInstance().getMainStage(),"Cannot load symbols screen", e);
        }
    }

    @FXML
    private void handleOpenMusic() {
        MusicController musicController = MuRET.getInstance().openWindow("/fxml/muret/music.fxml", true, true);
        try {
            musicController.loadScoreSongView();
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load music screen", e);
            ShowError.show(MuRET.getInstance().getMainStage(),"Cannot load music screen", e);
        }
    }

    @FXML
    private void handleClose() {
        MuRET.getInstance().closeCurrentWindow();
    }

    protected abstract double computeZoomToFitRatio();

    public abstract <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> selectedBoundingBoxView);

    public abstract <OwnerType extends IOMRBoundingBox> void onUnselected(BoundingBoxBasedView<OwnerType> selectedBoundingBoxView);

    protected abstract Node getRoot();

    protected void showError(String message, Throwable t) {
        ShowError.show(getRoot().getScene().getWindow(), message, t);
    }
}
