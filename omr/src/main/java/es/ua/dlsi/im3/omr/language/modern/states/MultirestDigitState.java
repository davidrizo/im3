package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.SimpleRest;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

import java.util.ArrayList;

public class MultirestDigitState extends OMRState {
    private int multirestDigits = 0; //cuantas veces entro
    private int multirestFinalNumber = 0;
    private ArrayList<Integer> digits;

    public MultirestDigitState(int number, String name) {
        super(number, "multirestDigit");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        if (token.getSymbol()== GraphicalSymbol.digit)
        {
            multirestDigits++;
            //TODO 13/12/17 Implementar cantidad de compases de espera en onExit
        }
    }

    public void onExit(GraphicalToken token, State previousState, OMRTransduction transduction){
        //En futuras implementaciones agregaremos a la partitura la cantidad de compases
    }
}
