package es.ua.dlsi.im3.omr.interactive.editpage.music;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.PageLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.List;

/**
 * It contains the excerpt of the manuscript, the digital score staff and the transduced score staff of all gegions
 */
public class TranscriptionPageView extends VBox {
    private static final double SEPARATION = 10;
    ScoreSongView scoreSongView;
    OMRPage page;
    List<TranscriptionStaffView> transcriptionStaffViewList;

    public TranscriptionPageView(OMRPage page) {
        super(SEPARATION);

        this.page = page;
        transcriptionStaffViewList = new LinkedList<>();
        TranscriptionStaffView prevTranscriptionStaffView = null;
        for (OMRRegion region: page.getRegionList()) {
            if (region.getRegionType() == RegionType.staff) {
                TranscriptionStaffView transcriptionStaffView = new TranscriptionStaffView(page, region);
                prevTranscriptionStaffView = transcriptionStaffView;
                this.getChildren().add(transcriptionStaffView);
                transcriptionStaffViewList.add(transcriptionStaffView);
            } else if (region.getRegionType() == RegionType.lyrics && prevTranscriptionStaffView != null) {
                prevTranscriptionStaffView.setLyricsRegion(region);
            }
        }
    }

    public void setScoreSong(ScoreSong scoreSong) throws IM3Exception {
        // TODO: 19/12/17 Estoy poniéndolo todo a la primera region - debemos pasarle sólo el ScoreStaffLayout (o algo así)
        // TODO: 19/12/17 ¿Ya la traducción?
        // TODO: 19/12/17 Estomos usando sólo el HorizontalLayout en ScoreView
        /*PageLayout layout = new PageLayout(scoreSong, scoreSong.getStaves(), true, LayoutFonts.capitan,
                new CoordinateComponent(1500), new CoordinateComponent(1000));*/
        HorizontalLayout layout = new HorizontalLayout(scoreSong, LayoutFonts.capitan,
                new CoordinateComponent(1500), new CoordinateComponent(1000));


        scoreSongView = new ScoreSongView(scoreSong, layout);
        transcriptionStaffViewList.get(0).setScoreView(scoreSongView);
    }
}
