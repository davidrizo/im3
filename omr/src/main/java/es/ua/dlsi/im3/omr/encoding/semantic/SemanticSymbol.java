package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.dfa.Token;
import es.ua.dlsi.im3.core.score.ScoreLayer;

import java.util.ArrayList;

/**
 * @autor drizo
 */
public class SemanticSymbol extends Token<SemanticSymbolType> {
    public SemanticSymbol(SemanticSymbolType symbol) {
        super(symbol);
    }

    public String toSemanticString() throws IM3Exception {
        return symbol.toSemanticString();
    }

    public String toKernSemanticString() throws IM3Exception {
        return symbol.toKernSemanticString();
    }

    public String toKernSemanticString(String suffix) throws IM3Exception {
        return symbol.toKernSemanticString() + suffix;
    }

    @Override
    public String toString() {
        return "SemanticSymbol of class " + this.getClass().getName();
    }
}
