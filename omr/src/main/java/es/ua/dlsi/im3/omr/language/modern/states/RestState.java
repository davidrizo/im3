package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Dot;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Rest;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.RestFigures;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public class RestState extends OMRState{
    public RestState(int number, String name) {
        super(number, "rest");
    }

    @Override
    public void onEnter(AgnosticSymbol token, State previousState, OMRTransduction transduction) {
        if (token.getSymbol() instanceof Rest) {
            //TODO No valdría tener antes un accidental
            Rest symbol = (Rest) token.getSymbol();
            SimpleRest rest = new SimpleRest(parseFigure(symbol.getRestFigures()), 0);
            try {
                //transduction.getStaff().add(rest);
                transduction.getLayer().add(rest);
            } catch (IM3Exception e) {
                throw new IM3RuntimeException(e);
            }
    } else if (token.getSymbol() instanceof Dot && previousState.toString().equals("rest")) {
            //TODO 28 11 17 como agregar elpuntillo correctamente
        } else throw new IM3RuntimeException("Symbol should be rest or dot, and it is a " + token.getSymbol());

    }

    private Figures parseFigure(RestFigures restFigures) {
        // TODO: 5/10/17 Valores válidos
        switch (restFigures) {
            case hundredTwentyEighth:
                return Figures.HUNDRED_TWENTY_EIGHTH;
            case sixtyFourth:
                return Figures.SIXTY_FOURTH;
            case thirtySecond:
                return Figures.THIRTY_SECOND;
            case twoHundredFiftySix:
                return Figures.TWO_HUNDRED_FIFTY_SIX;
            default:
                return Figures.valueOf(restFigures.toAgnosticString().toUpperCase());
        }
    }
}
