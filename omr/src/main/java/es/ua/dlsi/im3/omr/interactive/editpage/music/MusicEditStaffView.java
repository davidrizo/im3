package es.ua.dlsi.im3.omr.interactive.editpage.music;

import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.interactive.editpage.TranscriptionStaffView;
import es.ua.dlsi.im3.omr.interactive.model.OMRPage;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;

public class MusicEditStaffView extends TranscriptionStaffView {
    private ScoreSongView scoreSongView;

    public MusicEditStaffView(OMRPage page, OMRRegion region) {
        super(page, region);
    }

    public void setScoreView(ScoreSongView scoreSongView) {
        this.scoreSongView = scoreSongView;
        transcriptionPane.getChildren().clear();
        transcriptionPane.getChildren().add(scoreSongView.getMainPanel()); //// TODO: 19/12/17 Debería ser el pentagrama seleccionado sólo
    }

}
