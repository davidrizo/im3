package es.ua.dlsi.im3.omr.language.mensural.states;

import es.ua.dlsi.im3.core.adt.dfa.State;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.language.OMRTransduction;

public abstract class OMRState extends State<AgnosticSymbolType, AgnosticSymbol, OMRTransduction> {
    public OMRState(int number, String name) {
        super(number, name);
    }
}
