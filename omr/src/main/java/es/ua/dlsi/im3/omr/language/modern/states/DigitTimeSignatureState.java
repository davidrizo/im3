package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class DigitTimeSignatureState extends OMRState {

    private static Integer numerator = 0;

    public DigitTimeSignatureState (int number, String name) {
        super(number, "digittimesignature");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        if (token.getPositionInStaff().getLine() == 4) {
            numerator = Integer.parseInt(token.getValue()); // El numerador puede ser cualquier valor
        }
    }

    public static int getNumerator(){
        return numerator;
    }
}
