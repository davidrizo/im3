package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

//TODO
/**
 * @autor drizo
 */
public class Ligature extends AgnosticSymbolType {
    private static final String LIGATURE = "gregorian"; //TODO - CAMBIAR

    boolean jorgeCMark; //TODO

    public Ligature(boolean jorgeCMark) {
        this.jorgeCMark = jorgeCMark;
    }

    public Ligature() {
        this.jorgeCMark = false;
    }

    @Override
    public String toAgnosticString() {
        //TODO
        if (jorgeCMark) {
            return LIGATURE + "C";
        } else {
            return LIGATURE;
        }

    }

}
