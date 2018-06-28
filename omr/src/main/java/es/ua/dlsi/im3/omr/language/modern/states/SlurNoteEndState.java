package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Slur;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.StartEnd;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class SlurNoteEndState extends OMRState{
    private boolean isSlurStarted = false;

    public SlurNoteEndState(int number, String name) {
            super(number, "Slurnoteend");
       }
       @Override
       public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
           if (!(token.getSymbol() instanceof Slur) && previousState.getName().equals("Slurnote")) {
               Slur symbol = (Slur) token.getSymbol();
               if (symbol.getStartEnd() == StartEnd.end) {
                   isSlurStarted = false;
               }
           }

            //if (previousState.getName() == "Slurnote" && token.getSymbol() == GraphicalSymbol.slur && token.getValue() == "end"){
                //TODO 28/11/17 Agregar ligadura a la partitura
           //   isSlurStarted = false;
           //}
    }
}
