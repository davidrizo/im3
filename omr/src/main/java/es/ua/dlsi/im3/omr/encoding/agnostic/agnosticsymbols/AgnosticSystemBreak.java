package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

/**
 * @autor drizo
 */
public class AgnosticSystemBreak extends AgnosticToken {
    private static final String BARLINE = "\n"; // Primus v.2

    public AgnosticSystemBreak() {
        super(null);
    }

    @Override
    public String getAgnosticString() {
        return BARLINE;
    }
}
