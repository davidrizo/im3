package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class Clef extends SemanticSymbolType {
    private static final String SEMANTIC = "clef" + SEPSYMBOL;
    private ClefNote clefNote;
    private int line;

    public Clef(ClefNote clefNote, int line) {
        this.clefNote = clefNote;
        this.line = line;
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC + clefNote.toSemanticString() + this.line;
    }
}
