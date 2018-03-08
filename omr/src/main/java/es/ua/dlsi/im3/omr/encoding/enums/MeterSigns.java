package es.ua.dlsi.im3.omr.encoding.enums;

public enum MeterSigns {
    // Primus v1. C ("C"), Ccut ("C/"), CZ ("CZ"), CcutZ ("C/Z"), O ("O"), Odot ("O."), Cdot ("C.");
    C ("C"), Ccut ("Ccut"), CZ ("CZ"), CcutZ ("CcutZ"), O ("O"), Odot ("Odot"), Cdot ("Cdot");

    private String agnosticString;

    MeterSigns(String agnosticString) {
        this.agnosticString = agnosticString;
    }

    public String toAgnosticString() {
        return agnosticString;
    }

    public String toSemanticString() {
        return agnosticString; // the same
    }

}
