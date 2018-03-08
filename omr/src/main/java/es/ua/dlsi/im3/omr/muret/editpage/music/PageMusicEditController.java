package es.ua.dlsi.im3.omr.muret.editpage.music;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import es.ua.dlsi.im3.omr.muret.editpage.PageBasedController;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.model.pojo.*;
import es.ua.dlsi.im3.omr.transduction.AgnosticToSemanticTransducerFactory;
import es.ua.dlsi.im3.omr.transduction.IAgnosticToSemanticTransducer;
import es.ua.dlsi.im3.omr.transduction.ISemanticToScoreSongTransducer;
import es.ua.dlsi.im3.omr.transduction.SemanticToScoreSongTransducerFactory;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class PageMusicEditController extends PageBasedController<MusicEditPageView> {

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
            for (Map.Entry<OMRPage, MusicEditPageView> entry : pages.entrySet()) {
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
            ScoreSong mensural = new ScoreSong();
            ScorePart part = mensural.addPart();
            part.setName("Soprano"); //TODO - es importante !!! - nombre del instrumento
            Pentagram staff = new Pentagram(mensural, "1", 1);
            staff.setNotationType(NotationType.eMensural);
            staff.setName("Soprano"); //TODO - es importante !!!
            //TODO Crear elementos visuales - o mejor poner aquí los system break y que se genere
            part.addStaff(staff); // TODO: 19/12/17 Esto debería añadir el staff al scoreSong
            mensural.addStaff(staff);
            ScoreLayer scoreLayer = part.addScoreLayer();
            scoreLayer.setStaff(staff);
            // TODO: 19/12/17 Cogiendo sólo el primero (2 porque es el staff)
            semanticToScoreSongTransducer.transduceInto(semanticSequence.get(0).get(2), staff, scoreLayer);

            //TODO Traducción a moderno con valores de claves... a piñón - mejor en clase modelo
            Clef [] modernClefs = new Clef [] {
                    new ClefG2()
            };
            MensuralToModern mensuralToModern = new MensuralToModern(modernClefs);
            //TODO Parámetro
            //ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.FOURTH_PERFECT_DESC); // ésta genera más sostenidos
            ScoreSong modern = mensuralToModern.convertIntoNewSong(mensural, Intervals.FIFTH_PERFECT_DESC);
            mensuralToModern.merge(mensural, modern);

            // TODO: 19/12/17 Estoy poniendo todo en el primer pentagrama
            MusicEditPageView transcriptionPageView = pages.entrySet().iterator().next().getValue();
            transcriptionPageView.setScoreSong(mensural);
        } catch (IM3Exception e) {
            e.printStackTrace();
            ShowError.show(OMRApp.getMainStage(), "Cannot generate music notation", e);
        }
    }

    @Override
    protected MusicEditPageView createPageView(OMRPage omrPage, PageBasedController pageBasedController, ReadOnlyDoubleProperty widthProperty) throws IM3Exception {
        return new MusicEditPageView(omrPage);
    }

}