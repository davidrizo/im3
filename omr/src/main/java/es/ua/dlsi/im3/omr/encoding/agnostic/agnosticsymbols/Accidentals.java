package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;

/**
 * @autor drizo
 */
public enum  Accidentals {
    flat, natural, sharp, doublesharp;

    public static Accidentals parseAgnosticString(String string) {
        return Accidentals.valueOf(string);
    }

    public String toAgnosticString() {
        return this.name();
    }
}
