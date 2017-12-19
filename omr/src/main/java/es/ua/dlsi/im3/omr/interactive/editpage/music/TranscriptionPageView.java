package es.ua.dlsi.im3.omr.interactive.editpage.music;

import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.scene.layout.VBox;

/**
 * It contains the excerpt of the manuscript, the digital score staff and the transduced score staff of all gegions
 */
public class TranscriptionPageView extends VBox {
    private static final double SEPARATION = 10;
    ScoreSongView scoreSongView;
    OMRPage page;

    public TranscriptionPageView(OMRPage page) {
        super(SEPARATION);

        this.page = page;
        TranscriptionStaffView prevTranscriptionStaffView = null;
        for (OMRRegion region: page.getRegionList()) {
            if (region.getRegionType() == RegionType.staff) {
                TranscriptionStaffView transcriptionStaffView = new TranscriptionStaffView(page, region);
                prevTranscriptionStaffView = transcriptionStaffView;
                this.getChildren().add(transcriptionStaffView);
            } else if (region.getRegionType() == RegionType.lyrics && prevTranscriptionStaffView != null) {
                prevTranscriptionStaffView.setLyricsRegion(region);
            }
        }
    }
}
