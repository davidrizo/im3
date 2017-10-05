package es.ua.dlsi.im3.omr.language.modern.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.language.OMRTransduction;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalToken;

public abstract class OMRState extends State<GraphicalSymbol, GraphicalToken, OMRTransduction> {
    public OMRState(int number, String name) {
        super(number, name);
    }
}
