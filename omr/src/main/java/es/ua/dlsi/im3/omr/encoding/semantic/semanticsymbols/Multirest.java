package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

import java.util.List;

/**
 * @autor drizo
 */
public class Multirest extends SemanticSymbolType {
    private static final String SEMANTIC = "multirest" + SEPSYMBOL;
    private int restBars;

    public Multirest(int restBars) {
        this.restBars = restBars;
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC + restBars;
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        System.err.println("TO-DO multirest"); //TODO Barline
        return null;
    }

    @Override
    public void semantic2ScoreSong(SemanticConversionContext semanticConversionContext, List<ITimedElementInStaff> conversionResult) throws IM3Exception {
        throw new UnsupportedOperationException("TO-DO");
    }
}
