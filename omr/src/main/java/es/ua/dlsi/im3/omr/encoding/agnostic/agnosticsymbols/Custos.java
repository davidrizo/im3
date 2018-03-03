package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Custos extends AgnosticSymbolType {
    private static final String CUSTOS = "custos";

    @Override
    public String toAgnosticString() {
        return CUSTOS;
    }

}