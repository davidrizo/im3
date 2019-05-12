package es.ua.dlsi.im3.omr.encoding.enums;

/**
 * @author drizo
 */
public enum Defects {
    /**
     * The case of the names are used in string exports
     */
    inkBlot, paperHole, smudge;

    public String toSemanticString() {
        return name();
    }
}
