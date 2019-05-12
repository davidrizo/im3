package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Measure;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class Barline extends SemanticSymbolType {
    private static final String SEMANTIC = "barline";
    @Override
    public String toSemanticString() {
        return SEMANTIC;
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        System.err.println("TO-DO Barline"); //TODO Barline
        return "";
    }

    @Override
    public SemanticSymbolType semantic2ScoreSong(ScoreLayer scoreLayer, SemanticSymbolType propagatedSymbolType) throws IM3Exception {
        if (scoreLayer.getStaff().getNotationType() == NotationType.eMensural) {
            scoreLayer.getStaff().addMarkBarline(new MarkBarline(scoreLayer.getDuration()));
        } else if (scoreLayer.getStaff().getNotationType() == NotationType.eModern) {
            ScoreSong song = scoreLayer.getStaff().getScoreSong();
            Measure lastMeasure = song.getLastMeasure();
            lastMeasure.setEndTime(scoreLayer.getDuration());

            Measure measure = new Measure(song, lastMeasure.getNumber() + 1);
            scoreLayer.getStaff().getScoreSong().addMeasure(lastMeasure.getEndTime(), measure);
        } else {
            throw new IM3Exception("Not supported notation type:" + scoreLayer.getStaff().getNotationType());
        }
        return propagatedSymbolType;
    }
}
