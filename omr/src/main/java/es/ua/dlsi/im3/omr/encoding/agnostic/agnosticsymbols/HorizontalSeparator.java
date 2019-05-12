package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticToken;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

/**
 * @autor drizo
 */
public class HorizontalSeparator extends AgnosticSeparator {
    public static final String AGNOSTIC_V3_ADVANCE = "+";
    public static final String AGNOSTIC_V2 = ","; // Used in v2 of the grammar
    public static final String AGNOSTIC_V1 = "\t"; // Used in v1 of the grammar
    private final String separator;

    public HorizontalSeparator(AgnosticVersion version) {
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
