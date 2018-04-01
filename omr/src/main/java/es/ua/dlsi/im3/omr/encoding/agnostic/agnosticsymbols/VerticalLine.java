package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

/**
 * @autor drizo
 */
public class VerticalLine extends AgnosticSymbolType {
    private static final String BARLINE_V1 = "barline"; // Primus v.1
    private static final String BARLINE_V2 = "verticalLine"; // Primus v.2
    private final String separator;

    public VerticalLine() {
        this(AgnosticVersion.v2);
    }
    public VerticalLine(AgnosticVersion version) {
        super();
        switch (version) {
            case v1:
                separator = BARLINE_V1;
                break;
            case v2:
                separator = BARLINE_V2;
                break;
            default:
                throw new IM3RuntimeException("Unsupported version: " + version);
        }

    }

    @Override
    public String toAgnosticString() {
        return separator;
    }
}
