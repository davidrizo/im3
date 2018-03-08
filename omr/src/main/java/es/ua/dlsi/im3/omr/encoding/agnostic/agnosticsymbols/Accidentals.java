package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum  Accidentals {
    flat, natural, sharp, double_sharp;

    public String toAgnosticString() {
        return this.name();
    }
}
