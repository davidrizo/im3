package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import es.ua.dlsi.im3.omr.muret.model.IOMRBoundingBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class MusicController extends MuRETBaseController {
    @FXML
    BorderPane rootBorderPane;
    @FXML
    ScrollPane scrollPaneMusic;

    @FXML
    ScrollPane scrollPaneImages;
    private ScoreSongView scoreSongView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        try {
            loadScoreSongView();
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load the score sheet", e);
            showError("Cannot load the score sheet", e);
        }
    }

    public void loadScoreSongView() throws IM3Exception {
        //TODO convertir sólo si hace falta
        MuRET.getInstance().getModel().getCurrentProject().synchronizeAgnosticSemantic();
        ScoreSong scoreSong = MuRET.getInstance().getModel().getCurrentProject().getScoreSong();

        LayoutFonts font;
        if (MuRET.getInstance().getModel().getCurrentProject().getNotationType() == NotationType.eMensural) { //TODO Mensural "internacional"
            font = LayoutFonts.patriarca;
        } else {
            font = LayoutFonts.bravura;
        }

        ScoreLayout layout = new HorizontalLayout(scoreSong, font,
                new CoordinateComponent(scrollPaneMusic.widthProperty().doubleValue()),
                new CoordinateComponent(scrollPaneMusic.heightProperty().doubleValue()));
        scoreSongView = new ScoreSongView(layout);
        scrollPaneMusic.setContent(scoreSongView.getMainPanel());
    }

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
    public <OwnerType extends IOMRBoundingBox> void onUnselected(BoundingBoxBasedView<OwnerType> ownerTypeBoundingBoxBasedView) {

    }

    @Override
    protected Node getRoot() {
        return rootBorderPane;
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
