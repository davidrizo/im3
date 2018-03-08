package es.ua.dlsi.im3.omr.encoding.enums;

/**
 * @author drizo
 */
public enum ClefNote {
    /**
     * The case of the names are used in string exports
     */
    G, F, Fpetrucci, C;

    public String toSemanticString() {
        return name();
    }
}
