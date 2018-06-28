package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import javafx.geometry.Pos;

/**
 * @autor drizo
 */
public class Slur extends MarkUpDownStartEnd {
    private static final String SLUR = "slur";

    public Slur(StartEnd startEnd) {
        super(startEnd);
    }

    public Slur(StartEnd startEnd, Positions positions) {
        super(startEnd, positions);
    }

    public Slur() {
    }

    @Override
    protected String getAgnosticTypeString() {
        return SLUR;
    }
}
