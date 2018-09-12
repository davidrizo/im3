package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.ScoreLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.interaction.ISelectable;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import es.ua.dlsi.im3.omr.muret.model.IOMRBoundingBox;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;

import java.io.File;
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

    @FXML
    Menu menuExportMensural;

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

        menuExportMensural.setDisable(MuRET.getInstance().getModel().getCurrentProject().getNotationType() != NotationType.eMensural);
    }

    public void loadScoreSongView() throws IM3Exception {
        //TODO convertir sólo si hace falta
        MuRET.getInstance().getModel().getCurrentProject().synchronizeAgnosticSemantic();
        ScoreSong scoreSong = MuRET.getInstance().getModel().getCurrentProject().getScoreSong();


        ScoreLayout layout = new HorizontalLayout(scoreSong,
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

    private File askExportFile(String formatDescription, String extension) {
        OpenSaveFileDialog openSaveFileDialog = new OpenSaveFileDialog();
        return openSaveFileDialog.saveFile(getRoot().getScene().getWindow(), "Export", formatDescription, extension);
    }
    @FXML
    private void handleFileSave() {
        try {
            MuRET.getInstance().getModel().save();
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot save", e);
            showError( "Cannot save", e);
        }
    }

    @FXML
    private void handleExportMensuralMEI() {
        try {
            File file = askExportFile("Mensural notation MEI", "mei");
            if (file != null) {
                MuRET.getInstance().getModel().exportMensuralMEI(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportMensuralMens() {
        try {
            File file = askExportFile("**mens", "mns");
            if (file != null) {
                MuRET.getInstance().getModel().exportMensuralMens(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportMensuralDiplomaticPDF() {
        try {
            File file = askExportFile("PDF with diplomatic edition", "pdf");
            if (file != null) {
                MuRET.getInstance().getModel().exportMensuralDiplomaticPDF(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportMensuralEditorialPDF() {
        try {
            File file = askExportFile("PDF with edited content", "pdf");
            if (file != null) {
                MuRET.getInstance().getModel().exportMensuralEditorialPDF(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportModernAndMensuralPDF() {
        try {
            File file = askExportFile("Export PDF of modern notation score aligned with mensural", "xml");
            if (file != null) {
                MuRET.getInstance().getModel().exportMensuralAndModernPDF(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }


    @FXML
    private void handleExportMensuralLilypond() {
        try {
            File file = askExportFile("Export mensural Lilypond", "ly");
            if (file != null) {
                MuRET.getInstance().getModel().exportMensuralLilypond(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportImagesWithEditorialComments() {
        try {
            File file = askExportFile("PDF with images and comments", "pdf");
            if (file != null) {
                MuRET.getInstance().getModel().exportPDFWithImagesAndEditorialComments(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportModernMusicXML() {
        try {
            File file = askExportFile("Export MusicXML of modern notation score", "xml");
            if (file != null) {
                MuRET.getInstance().getModel().exportModernMusicXML(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportModernMEI() {
        try {
            File file = askExportFile("Export MEI of modern notation score", "xml");
            if (file != null) {
                MuRET.getInstance().getModel().exportModernMEI(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportModernKern() {
        try {
            File file = askExportFile("Export **kern of modern notation score", "krn");
            if (file != null) {
                MuRET.getInstance().getModel().exportModernKern(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportModernPDF() {
        try {
            File file = askExportFile("Export PDF of modern notation score", "xml");
            if (file != null) {
                MuRET.getInstance().getModel().exportModernPDF(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }


    @FXML
    private void handleExportModernLilypond() {
        try {
            File file = askExportFile("Export Lilypond of modern notation score", "xml");
            if (file != null) {
                MuRET.getInstance().getModel().exportModernLilypond(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleExportModernMIDI() {
        try {
            File file = askExportFile("Export MIDI performance", "xml");
            if (file != null) {
                MuRET.getInstance().getModel().exportModernMIDI(file);
            }
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot export", e);
            showError( "Cannot export", e);
        }
    }

    @FXML
    private void handleShowUserInteractionLogs() {
        MuRET.getInstance().showUserInteractionLogs();
    }

}
