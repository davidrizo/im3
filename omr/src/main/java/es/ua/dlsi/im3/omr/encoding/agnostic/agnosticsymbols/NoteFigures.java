package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @autor drizo
 */
public enum NoteFigures implements INoteDurationSpecification {
    quadruple_whole_stem ,   triple_whole_stem ,   double_whole_stem ,   double_whole ,   longa ,   breve ,   breve_black ,   whole ,   whole_black ,   half ,   quarter ,   eighth ,   eighth_cut ,   eighth_void ,   sixteenth ,   sixteenth_void ,   thirty_second ,   sixty_fourth ,   hundred_twenty_eighth ,   two_hundred_fifty_six;

    @Override
    public String toAgnosticString() {
        return this.name();
    }
}
