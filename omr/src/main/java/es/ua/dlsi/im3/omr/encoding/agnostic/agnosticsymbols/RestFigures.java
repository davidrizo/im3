package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum  RestFigures {
    longa, breve, whole, half, seminima, quarter , eighth , sixteenth , fusa , semifusa , thirtySecond , sixtyFourth , hundredTwentyEighth , twoHundredFiftySix;

    public static RestFigures parseAgnosticString(String string) {
        return RestFigures.valueOf(string);
    }

    public String toAgnosticString() {
        return this.name();
    }
}
