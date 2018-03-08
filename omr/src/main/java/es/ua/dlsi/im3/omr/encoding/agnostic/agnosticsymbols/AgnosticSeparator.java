package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;

/**
 * @autor drizo
 */
public abstract class AgnosticSeparator extends AgnosticToken {
    public AgnosticSeparator(AgnosticSymbolType symbol) {
        super(symbol);
    }
}
