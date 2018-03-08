package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

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
}
