package es.ua.dlsi.im3.omr.encoding.enums;

import es.ua.dlsi.im3.core.IM3Exception;

public enum MeterSigns {
    // Primus v1. C ("C"), Ccut ("C/"), CZ ("CZ"), CcutZ ("C/Z"), O ("O"), Odot ("O."), Cdot ("C.");
    // v2a C ("C"), Ccut ("Ccut"), CZ ("CZ"), CcutZ ("CcutZ"), O ("O"), Odot ("Odot"), Cdot ("Cdot");
    // v2b
    C ("Ct"), Ccut ("Ccut"), CZ ("CZ"), CcutZ ("CcutZ"), O ("O"), Odot ("Odot"), Cdot ("Cdot");

    private String agnosticString;

    MeterSigns(String agnosticString) {
        this.agnosticString = agnosticString;
    }

    public static MeterSigns parseAgnosticString(String string) throws IM3Exception {
        // migration V2a to V2b
        if (string.equals("C")) {
            return C;
        }

        for (MeterSigns meterSigns: MeterSigns.values()) {
            if (meterSigns.agnosticString.equals(string)) {
                return meterSigns;
            }
        }
        throw new IM3Exception("Cannot find a meter sign with agnostic string '" + string + "'");
    }

    public String toAgnosticString() {
        return agnosticString;
    }

    public String toSemanticString() {
        return agnosticString; // the same
    }

}
