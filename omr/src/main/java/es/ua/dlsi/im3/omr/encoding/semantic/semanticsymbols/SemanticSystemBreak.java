package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbol;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class SemanticSystemBreak extends SemanticSymbol {
    private static final String SEMANTIC = "\n";

    public SemanticSystemBreak() {
        super(null);
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC;
    }
}
