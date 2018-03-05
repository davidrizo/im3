package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.adt.dfa.Token;

/**
 * @autor drizo
 */
public abstract class AgnosticToken extends Token<AgnosticSymbolType> {
    public AgnosticToken(AgnosticSymbolType symbol) {
        super(symbol);
    }

    public abstract String getAgnosticString();
}
