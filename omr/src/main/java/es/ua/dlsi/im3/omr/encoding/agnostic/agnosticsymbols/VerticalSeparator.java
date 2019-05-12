package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

/**
 * @autor drizo
 */
public class VerticalSeparator extends AgnosticSeparator {
    private static final String AGNOSTIC_V3_ADVANCE = " ";
    private static final String AGNOSTIC_V2 = "/";
    private static final String AGNOSTIC_V1 = "\t";
    private final String separator;

    public VerticalSeparator() {
        this(AgnosticVersion.v2);
    }

    public VerticalSeparator(AgnosticVersion version) {
        super(null);
        switch (version) {
            case v1:
                separator = AGNOSTIC_V1;
                break;
            case v2:
                separator = AGNOSTIC_V2;
                break;
            case v3_advance:
                separator = AGNOSTIC_V3_ADVANCE;
                break;
            default:
                throw new IM3RuntimeException("Unsupported version: " + version);
        }
    }

    @Override
    public String getAgnosticString() {
        return separator;
    }
}
