package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum NoteFigures implements INoteDurationSpecification {
    quadrupleWholeStem (true),
    tripleWholeStem (true),
    doubleWholeStem (true),
    doubleWholeBlackStem (true),
    doubleWhole (false),
    longa (true),
    longaBlack (true),
    breve (false),
    breveBlack (false),
    whole (false),
    wholeBlack (false),
    half (true),
    quarter (true),
    eighth (true),
    eighthCut (true),
    eighthVoid (true),
    sixteenth (true),
    sixteenthVoid (true),
    thirtySecond (true),
    sixtyFourth (true),
    hundredTwentyEighth (true),
    twoHundredFiftySix (true);

    public static NoteFigures parseAgnosticString(String string) {
        return NoteFigures.valueOf(string);
    }

    private boolean usesStem;

    NoteFigures(boolean usesStem) {
        this.usesStem = usesStem;
    }

    @Override
    public boolean isUsesStem() {
        return usesStem;
    }

    @Override
    public String toAgnosticString() {
        return this.name();
    }
}
