package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;

/**
 * @autor drizo
 */
public enum  Accidentals {
    flat ("b"), natural ("n"), sharp ("#"), doublesharp ("x");

    private final String abbr;

    Accidentals(String abbr) {
        this.abbr = abbr;
    }

    public String getAbbr() {
        return abbr;
    }

    public static Accidentals parseAgnosticString(String string) {
        return Accidentals.valueOf(string);
    }

    public String toAgnosticString() {
        return this.name();
    }
}
