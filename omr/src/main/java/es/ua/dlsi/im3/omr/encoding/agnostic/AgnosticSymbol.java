package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.dfa.Token;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.PositionInStaff;

import java.util.Objects;

/**
 * @author drizo
 */
public class AgnosticSymbol extends AgnosticToken {
    private static final char SEPVERTICALPOS = '-';
    private static final String SEPVERTICALPOS_STR = ""+SEPVERTICALPOS;
    /**
     * Vertical position in the staff
     */
    private PositionInStaff positionInStaff;

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

    public static AgnosticSymbol parseString(String string) throws IM3Exception {
	    String trimmedString = string.trim();
	    if (trimmedString.isEmpty()) {
	        throw new IM3RuntimeException("Empty string");
        }
	    String [] tokens = string.split(SEPVERTICALPOS_STR);

        PositionInStaff positionInStaff = null;
        AgnosticSymbolType agnosticSymbolType = AgnosticSymbolTypeFactory.parseString(tokens[0]);
	    if (tokens.length == 2) {
            positionInStaff = PositionInStaff.parseString(tokens[1]);
        } else if (tokens.length > 2) {
	        throw new ImportException("Invalid agnostic symbol string: '" + string + "'");
        }
        return new AgnosticSymbol(agnosticSymbolType, positionInStaff);

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgnosticSymbol)) return false;
        if (!super.equals(o)) return false;
        AgnosticSymbol that = (AgnosticSymbol) o;
        return Objects.equals(positionInStaff, that.positionInStaff);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), positionInStaff);
    }

    @Override
    public String toString() {
        return getAgnosticString();
    }

    public void changePosition(int lineSpaces) {
	    positionInStaff = new PositionInStaff(positionInStaff.getLineSpace()+lineSpaces);
    }

    public void changeAgnosticSymbolType(AgnosticSymbolType agnosticSymbolType) {
	    this.symbol = agnosticSymbolType;
    }
}
