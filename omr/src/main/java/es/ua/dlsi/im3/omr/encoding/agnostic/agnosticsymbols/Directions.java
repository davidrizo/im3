package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum Directions {
    up, down;

    public String toAgnosticString() {
        return this.name();
    }

    public static Directions parseAgnosticString(String string) {
        return Directions.valueOf(string);
    }
}
