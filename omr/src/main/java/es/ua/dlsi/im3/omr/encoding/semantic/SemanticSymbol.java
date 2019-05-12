package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.adt.dfa.Token;
import es.ua.dlsi.im3.core.score.ScoreLayer;

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
