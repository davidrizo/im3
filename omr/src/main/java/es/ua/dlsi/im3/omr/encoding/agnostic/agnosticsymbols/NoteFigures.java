package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum NoteFigures implements INoteDurationSpecification {
    quadrupleWholeStem ,   tripleWholeStem ,   doubleWholeStem ,   doubleWholeBlackStem, double_whole ,   longa ,   breve ,   breveBlack ,   whole ,   wholeBlack ,   half ,   quarter ,   eighth ,   eighthCut ,   eighthVoid ,   sixteenth ,   sixteenthVoid ,   thirtySecond ,   sixtyFourth ,   hundredTwentyEighth ,   twoHundredFiftySix;

    @Override
    public String toAgnosticString() {
        return this.name();
    }
}
