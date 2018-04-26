package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;

/**
 * @author drizo
 */
public class Clef extends AgnosticSymbolType {
    private static final String CLEF = "clef" + SEPSYMBOL;
    ClefNote clefNote;

    public Clef(ClefNote clefNote) {
        this.clefNote = clefNote;
    }

    /**
     * For use in automata and in factory
     */
    public Clef() {
    }

    @Override
    public void setSubtype(String string) {
        clefNote = ClefNote.valueOf(string);
    }

    public ClefNote getClefNote() {
        return clefNote;
    }

    public void setClefNote(ClefNote clefNote) {
        this.clefNote = clefNote;
    }

    @Override
    public String toAgnosticString() {
        return CLEF + clefNote.name();
    }
}
