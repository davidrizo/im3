package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.adt.dfa.Token;
import es.ua.dlsi.im3.core.score.PositionInStaff;

public class AgnosticSymbol extends Token<AgnosticSymbolType>  {
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
}
