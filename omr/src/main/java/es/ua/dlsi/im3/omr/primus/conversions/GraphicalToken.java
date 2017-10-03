package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.adt.dfa.Token;
import es.ua.dlsi.im3.core.score.PositionInStaff;

public class GraphicalToken extends Token<GraphicalSymbol> {
    public static final char LINE_SEPARATOR = '-';
    private static final char VALUE_SEPARATOR = '.';

    String value;
    PositionInStaff positionInStaff;

    /**
     *
     * @param symbol
     * @param value May be null
     * @param positionInStaff
     */
    public GraphicalToken(GraphicalSymbol symbol, String value, PositionInStaff positionInStaff) {
        super(symbol);
        this.value = value;
        this.positionInStaff = positionInStaff;
    }

    public String getValue() {
        return value;
    }

    public PositionInStaff getPositionInStaff() {
        return positionInStaff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(symbol);
        if (value != null) {
            sb.append(VALUE_SEPARATOR);
            sb.append(value);
        }
        sb.append(LINE_SEPARATOR);
        sb.append(positionInStaff.toString());
        return sb.toString();
    }
}
