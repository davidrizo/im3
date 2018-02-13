package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;

public class RestState extends OMRState{
    public RestState(int number, String name) {
        super(number, "rest");
    }

    @Override
    public void onEnter(GraphicalToken token, State previousState, OMRTransduction transduction) {
        if (token.getSymbol() == GraphicalSymbol.rest) {
            //TODO No valdría tener antes un accidental
            SimpleRest rest = new SimpleRest(parseFigure(token.getValue()), 0);
            try {
                transduction.getStaff().addCoreSymbol(rest);
                transduction.getLayer().add(rest);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }
    } else if (token.getSymbol() == GraphicalSymbol.dot && previousState.toString() == "rest") {
            //TODO 28 11 17 como agregar elpuntillo correctamente
        }else throw new IM3RuntimeException("Symbol should be rest or dot");

    }

    private Figures parseFigure(String value) {
        // TODO: 5/10/17 Valores válidos
        return Figures.valueOf(value.toUpperCase());
    }
}
