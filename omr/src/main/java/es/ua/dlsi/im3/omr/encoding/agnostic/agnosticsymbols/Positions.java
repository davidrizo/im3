package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum Positions {
    above, below;

    public String toAgnosticString() {
        return this.name();
    }
}
