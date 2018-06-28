package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum  RestFigures {
    longa3, longa2, breve, whole, half, seminima, quarter , eighth , sixteenth , fusa , semifusa , thirtySecond , sixtyFourth , hundredTwentyEighth , twoHundredFiftySix;

    public static RestFigures parseAgnosticString(String string) {
        if (string.equals("longa")) {
            return longa2; // path to allow reading old encoding
        }
        return RestFigures.valueOf(string);
    }

    public String toAgnosticString() {
        return this.name();
    }
}
