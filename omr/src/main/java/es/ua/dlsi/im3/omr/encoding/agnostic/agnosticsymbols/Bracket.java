package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;


/**
 * @autor drizo
 */
public class Bracket extends MarkUpDownStartEnd {
    private static String CODE = "bracket";

    public Bracket(StartEnd startEnd) {
        super(startEnd);
    }

    public Bracket(StartEnd startEnd, Positions positions) {
        super(startEnd, positions);
    }

    public Bracket() {
    }

    @Override
    protected String getAgnosticTypeString() {
        return CODE;
    }
}
