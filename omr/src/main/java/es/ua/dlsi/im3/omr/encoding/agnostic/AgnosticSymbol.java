package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.adt.dfa.Token;
import es.ua.dlsi.im3.core.score.PositionInStaff;

public class AgnosticSymbol extends AgnosticToken {
    private static final char SEPVERTICALPOS = '-';
    PositionInStaff positionInStaff;

	public AgnosticSymbol(AgnosticSymbolType symbol, PositionInStaff position) {
		super(symbol);
		this.positionInStaff = position;
	}
	public PositionInStaff getPositionInStaff() {
		return positionInStaff;
	}
	public void setPositionInStaff(PositionInStaff positionInStaff) {
		this.positionInStaff = positionInStaff;
	}

    @Override
    public int compareTo(Token<AgnosticSymbolType> o) {
        AgnosticSymbol other = (AgnosticSymbol) o;
        int diff = super.compareTo(other);
        if (diff == 0) {
            diff = positionInStaff.compareTo(positionInStaff);
        }
        return diff;
    }

    public String getAgnosticString() {
        AgnosticSymbolType specificSymbol = symbol;
        StringBuilder sb = new StringBuilder();
        sb.append(specificSymbol.toAgnosticString());
        sb.append(SEPVERTICALPOS);
        sb.append(positionInStaff.toString());
        return sb.toString();
    }
}
