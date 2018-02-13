package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class SlurNoteEndState extends OMRState{
    private boolean isSlurStarted = false;

    public SlurNoteEndState(int number, String name) {
            super(number, "Slurnoteend");
       }
       @Override
       public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {

            if (previousState.getName() == "Slurnote" && token.getSymbol() == GraphicalSymbol.slur && token.getValue() == "end"){
                //TODO 28/11/17 Agregar ligadura a la partitura
                isSlurStarted = false;
            }
    }
}
