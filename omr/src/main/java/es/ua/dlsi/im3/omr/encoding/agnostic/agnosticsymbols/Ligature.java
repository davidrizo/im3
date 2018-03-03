package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

//TODO
/**
 * @autor drizo
 */
public class Ligature extends AgnosticSymbolType {
    private static final String LIGATURE = "gregorian"; //TODO - CAMBIAR

    public Ligature() {
    }

    @Override
    public String toAgnosticString() {
        return LIGATURE;
    }

}
