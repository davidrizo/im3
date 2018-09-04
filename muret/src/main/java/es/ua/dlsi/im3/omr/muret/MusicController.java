package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.omr.muret.model.IOMRBoundingBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @autor drizo
 */
public class MusicController extends MuRETBaseController {

    @Override
    protected void bindZoom(Scale scaleTransformation) {

    }

    @Override
    protected double computeZoomToFitRatio() {
        return 0;
    }

    @Override
    public <OwnerType extends IOMRBoundingBox> void doSelect(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {

    }

    @Override
    public void unselect() {

    }

    @Override
    public ISelectable first() {
        return null;
    }

    @Override
    public ISelectable last() {
        return null;
    }

    @Override
    public ISelectable previous(ISelectable s) {
        return null;
    }

    @Override
    public ISelectable next(ISelectable s) {
        return null;
    }
}
