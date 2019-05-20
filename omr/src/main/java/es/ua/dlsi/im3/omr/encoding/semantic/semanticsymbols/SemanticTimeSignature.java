package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public abstract class SemanticTimeSignature<TimeSignatureType extends TimeSignature> extends SemanticSymbolType<TimeSignatureType> {
    protected static final String SEMANTIC = "timeSignature" + SEPSYMBOL;

    /**
     *
     * @param coreSymbol Must be cloned from the children
     */
    public SemanticTimeSignature(TimeSignatureType coreSymbol) {
        super(coreSymbol);
    }
}
