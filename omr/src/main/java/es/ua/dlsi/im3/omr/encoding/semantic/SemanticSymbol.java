package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.adt.dfa.Token;

/**
 * @autor drizo
 */
public class SemanticSymbol extends Token<SemanticSymbolType> {
    public SemanticSymbol(SemanticSymbolType symbol) {
        super(symbol);
    }

    public String toSemanticString() {
        return symbol.toSemanticString();
    }
}
