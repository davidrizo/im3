package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

/**
 * @autor drizo
 */
public class VerticalLine extends AgnosticSymbolType {
    public static final String BARLINE_V1 = "barline"; // Primus v.1
    public static final String BARLINE_V2 = "verticalLine"; // Primus v.2
    private final String agnosticValue;

    public VerticalLine() {
        this(AgnosticVersion.v2);
    }

    @Override
    public void setSubtype(String string) {

    }

    public VerticalLine(AgnosticVersion version) {
        super();
        switch (version) {
            case v1:
                agnosticValue = BARLINE_V1;
                break;
            default:
                agnosticValue = BARLINE_V2;
        }

    }

    @Override
    public String toAgnosticString() {
        return agnosticValue;
    }
}
