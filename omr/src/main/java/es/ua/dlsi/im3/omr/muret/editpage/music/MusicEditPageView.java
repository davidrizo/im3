package es.ua.dlsi.im3.omr.muret.editpage.music;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import es.ua.dlsi.im3.omr.muret.editpage.SlicedRegionsWithTranscriptionBasedPageView;
import es.ua.dlsi.im3.omr.muret.model.OMRPage;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;

import java.util.HashMap;

/**
 * It contains the excerpt of the manuscript, the digital score staff and the transduced score staff of all regions.
 * It allows just editing the music
 */
public class MusicEditPageView extends SlicedRegionsWithTranscriptionBasedPageView<MusicEditStaffView> {
    private ScoreSongView scoreSongView;

    public MusicEditPageView(OMRPage page) throws IM3Exception {
        super(page);
    }

    @Override
    protected MusicEditStaffView createTranscriptionStaffViewType(OMRPage page, OMRRegion region) {
        return new MusicEditStaffView(page, region);
    }

    public void setScoreSong(ScoreSong scoreSong) throws IM3Exception {
        // TODO: 19/12/17 Estoy poniéndolo todo a la primera region - debemos pasarle sólo el ScoreStaffLayout (o algo así)
        // TODO: 19/12/17 ¿Ya la traducción?
        // TODO: 19/12/17 Estomos usando sólo el HorizontalLayout en ScoreView
        /*PageLayout layout = new PageLayout(scoreSong, scoreSong.getStaves(), true, LayoutFonts.capitan,
                new CoordinateComponent(1500), new CoordinateComponent(1000));*/

        HashMap<Staff, LayoutFonts> fontsHashMap = new HashMap<>();
        for (Staff astaff: scoreSong.getStaves()) {
            if (astaff.getNotationType() == NotationType.eMensural) {
                fontsHashMap.put(astaff, LayoutFonts.capitan);
            } else if (astaff.getNotationType() == NotationType.eModern) {
                fontsHashMap.put(astaff, LayoutFonts.bravura);
            } else {
                throw new IM3Exception("The staff " + astaff + " has not a notation type");
            }
        }

        HorizontalLayout layout = new HorizontalLayout(scoreSong, fontsHashMap,
                new CoordinateComponent(1500), new CoordinateComponent(1000));


        scoreSongView = new ScoreSongView(scoreSong, layout);
        transcriptionStaffViewList.get(0).setScoreView(scoreSongView);
    }
}
