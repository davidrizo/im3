package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.PositionsInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Digit;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class DigitTimeSignatureState extends OMRState {

    private int numerator;

    public DigitTimeSignatureState (int number, String name) {
        super(number, "digittimesignature");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        if (!(token.getSymbol() instanceof Digit)) {
            // the automaton has an error
            throw new IM3RuntimeException("Expected an clef and found a " + token.getSymbol());
        }

        Digit symbol = (Digit) token.getSymbol();

        if (token.getPositionInStaff() == PositionsInStaff.LINE_4) {
            numerator = symbol.getDigit();
        } else {
            throw new IM3RuntimeException("Expected line 4 and got " + token.getPositionInStaff());
        }
    }

    public int getNumerator(){
        return numerator;
    }
}
