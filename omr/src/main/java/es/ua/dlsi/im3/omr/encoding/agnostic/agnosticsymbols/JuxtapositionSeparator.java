package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;

/**
 * @autor drizo
 */
public class JuxtapositionSeparator extends AgnosticSeparator {
    private static final String AGNOSTIC = "|";

    public JuxtapositionSeparator() {
        super(null);
    }

    @Override
    public String getAgnosticString() {
        return AGNOSTIC;
    }
}
