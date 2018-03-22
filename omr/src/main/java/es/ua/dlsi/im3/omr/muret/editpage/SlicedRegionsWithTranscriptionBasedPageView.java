package es.ua.dlsi.im3.omr.muret.editpage;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * It contains a page where regions have been sliced. Below the regions the transcription is shown.
 */
public abstract class SlicedRegionsWithTranscriptionBasedPageView<TranscriptionStaffViewType extends TranscriptionStaffView> extends VBox {
    private static final double SEPARATION = 10;
    protected OMRPage page;
    protected List<TranscriptionStaffViewType> transcriptionStaffViewList;

    public SlicedRegionsWithTranscriptionBasedPageView(OMRPage page) throws IM3Exception {
        super(SEPARATION);

        this.page = page;
        transcriptionStaffViewList = new LinkedList<>();
        TranscriptionStaffViewType prevTranscriptionStaffViewType = null;
        for (OMRRegion region: page.getRegionList()) {
            if (region.getRegionType() == RegionType.staff) {
                TranscriptionStaffViewType transcriptionStaffView = createTranscriptionStaffViewType(page, region);
                prevTranscriptionStaffViewType = transcriptionStaffView;
                this.getChildren().add(transcriptionStaffView);
                transcriptionStaffViewList.add(transcriptionStaffView);
            } else if (region.getRegionType() == RegionType.lyrics && prevTranscriptionStaffViewType != null) {
                prevTranscriptionStaffViewType.setLyricsRegion(region);
            }
        }
    }

    protected abstract TranscriptionStaffViewType createTranscriptionStaffViewType(OMRPage page, OMRRegion region) throws IM3Exception;
}
