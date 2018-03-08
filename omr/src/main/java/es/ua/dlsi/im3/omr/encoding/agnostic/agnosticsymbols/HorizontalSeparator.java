package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;

/**
 * @autor drizo
 */
public class HorizontalSeparator extends AgnosticSeparator {
    private static final String AGNOSTIC = ",";

    public HorizontalSeparator() {
        super(null);
    }

    @Override
    public String getAgnosticString() {
        return AGNOSTIC;
    }
}
