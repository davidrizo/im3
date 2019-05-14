package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

import java.util.List;

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
    public String toKernSemanticString() {
        System.err.println("TO-DO Barline"); //TODO Barline
        return "";
    }

    @Override
    public void semantic2ScoreSong(SemanticConversionContext semanticConversionContext, List<ITimedElementInStaff> conversionResult) {
        MarkBarline markBarline = new MarkBarline();
        conversionResult.add(markBarline);

        /*if (scoreLayer.getStaff().getNotationType() == NotationType.eMensural) {
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
        return propagatedSymbolType;*/
    }
}
