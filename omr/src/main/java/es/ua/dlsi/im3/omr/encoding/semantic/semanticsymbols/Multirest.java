package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

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
    public SemanticSymbolType semantic2ScoreSong(ScoreLayer scoreLayer, SemanticSymbolType propagatedSymbolType) throws IM3Exception {
        throw new UnsupportedOperationException("TO-DO");
    }
}
