package es.ua.dlsi.im3.omr.interactive.editpage.music;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.ScorePart;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.interactive.DashboardController;
import es.ua.dlsi.im3.omr.interactive.OMRApp;
import es.ua.dlsi.im3.omr.interactive.editpage.IPagesController;
import es.ua.dlsi.im3.omr.interactive.editpage.PageBasedController;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.interactive.model.OMRSymbol;
import es.ua.dlsi.im3.omr.model.pojo.*;
import es.ua.dlsi.im3.omr.transduction.AgnosticToSemanticTransducerFactory;
import es.ua.dlsi.im3.omr.transduction.IAgnosticToSemanticTransducer;
import es.ua.dlsi.im3.omr.transduction.ISemanticToScoreSongTransducer;
import es.ua.dlsi.im3.omr.transduction.SemanticToScoreSongTransducerFactory;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class PageMusicEditController extends PageBasedController<TranscriptionPageView> {

    @FXML
    ToolBar toolbarMusic;

    @FXML
    VBox vboxPages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    // Music step
    @FXML
    public void handleGenerateMusicNotation() {
        try {
            //TODO Mover todas las operaciones no presenter a servicios (para hacer luego la web)
            // Convert agnostic sequence to semantic sequence
            IAgnosticToSemanticTransducer agnosticToSemanticTransducer = AgnosticToSemanticTransducerFactory.getInstance().create();
            List<List<List<GraphicalToken>>> agnosticSequence = new LinkedList<>();
            for (Map.Entry<OMRPage, TranscriptionPageView> entry : pages.entrySet()) {
                List<List<GraphicalToken>> pagesAgnosticSequence = new LinkedList<>();
                OMRPage page = entry.getKey();
                List<OMRRegion> regions = page.getRegionList();
                for (OMRRegion region : regions) {
                    List<GraphicalToken> regionAgnosticSequence = new LinkedList<>();
                    for (OMRSymbol symbol: region.symbolListProperty()) {
                        regionAgnosticSequence.add(new GraphicalToken(symbol.getGraphicalSymbol(), symbol.getValue(), symbol.getPositionInStaff()));
                    }
                    pagesAgnosticSequence.add(regionAgnosticSequence);
                }
                agnosticSequence.add(pagesAgnosticSequence);
            }
            List<List<List<SemanticToken>>> semanticSequence = agnosticToSemanticTransducer.transduce(agnosticSequence);

            // Now convert semantic sequence to IMCore
            //TODO Pasarle el instrumento y la jerarquía
            ISemanticToScoreSongTransducer semanticToScoreSongTransducer = SemanticToScoreSongTransducerFactory.getInstance().create(NotationType.eMensural);
            ScoreSong scoreSong = new ScoreSong();
            ScorePart part = scoreSong.addPart();
            Pentagram staff = new Pentagram(scoreSong, "1", 1);
            staff.setNotationType(NotationType.eMensural);
            //TODO Crear elementos visuales - o mejor poner aquí los system break y que se genere
            part.addStaff(staff); // TODO: 19/12/17 Esto debería añadir el staff al scoreSong
            scoreSong.addStaff(staff);
            ScoreLayer scoreLayer = part.addScoreLayer();
            scoreLayer.setStaff(staff);
            // TODO: 19/12/17 Cogiendo sólo el primero (2 porque es el staff)
            semanticToScoreSongTransducer.transduceInto(semanticSequence.get(0).get(2), staff, scoreLayer);

            //TODO Añadir traducción a moderno

            // TODO: 19/12/17 Estoy poniendo todo en el primer pentagrama
            TranscriptionPageView transcriptionPageView = pages.entrySet().iterator().next().getValue();
            transcriptionPageView.setScoreSong(scoreSong);
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot generate music notation", e);
        }
    }

    @Override
    protected TranscriptionPageView createPageView(OMRPage omrPage, PageBasedController pageBasedController, ReadOnlyDoubleProperty widthProperty) {
        return new TranscriptionPageView(omrPage);
    }

}