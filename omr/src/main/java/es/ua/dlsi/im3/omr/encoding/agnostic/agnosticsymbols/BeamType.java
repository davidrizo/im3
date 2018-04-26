package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum BeamType {
    left ("beamedLeft"), both ("beamedBoth"), right ("beamedRight");

    String agnosticString;

    BeamType(String agnosticString) {
        this.agnosticString = agnosticString;
    }

    public String toAgnosticString() {
        return this.agnosticString;
    }

}
