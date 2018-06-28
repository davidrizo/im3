package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Slur;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.StartEnd;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class SlurNoteState extends OMRState {
    private boolean isSlurStarted = false;
    public SlurNoteState(int number, String name) {
        super(number, "Slurnote");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        //Compruebo que vengo de notes y tengo una ligadura que comienza
        if (!(token.getSymbol() instanceof Slur)) {
            Slur symbol = (Slur) token.getSymbol();
            if (previousState.getName().equals("notes") && symbol.getStartEnd() == StartEnd.start) {
                //if (previousState.getName() == "notes" && token.getSymbol() == GraphicalSymbol.slur && token.getValue() == "start"){
                //TODO 29/11/17 como agregar la ligadura
                isSlurStarted = true;
                try {
                    System.out.println(transduction.getLayer().getAtomPitches().lastIndexOf(transduction.getLayer().getLastAtom()));
                    //transduction.getLayer().getLastAtom().getAtomPitches().get(0).setTiedToNext();
                    //chord.getAtomPitches().get(i).setTiedToNext(secondChord.getAtomPitches().get(i));
                    //transduction.getLayer().getAtomPitches().get(1).setTiedToNext();
                } catch (IM3Exception e) {
                    throw new IM3RuntimeException(e);
                }
            } else if (previousState.getName().equals("Slurnote") && symbol.getStartEnd() == StartEnd.end) {
                //termina la ligadura
                isSlurStarted = false;
            }
        }
    }
}
