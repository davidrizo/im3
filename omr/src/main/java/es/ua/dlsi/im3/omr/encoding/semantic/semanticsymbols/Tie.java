package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class Tie extends SemanticSymbolType {
    private static final String SEMANTIC = "tie";
    @Override
    public String toSemanticString() {
        return SEMANTIC;
    }
}
