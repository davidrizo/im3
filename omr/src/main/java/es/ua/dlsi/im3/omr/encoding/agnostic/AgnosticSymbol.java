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
    private static final char SEPVERTICALPOS_V1 = '-';
    private static final char SEPVERTICALPOS_V2 = ':';
    private static final String SEPVERTICALPOS_STR_V1 = ""+SEPVERTICALPOS_V1;
    private static final String SEPVERTICALPOS_STR_V2 = ""+SEPVERTICALPOS_V2;
    private final AgnosticVersion agnosticVersion;
    /**
     * Vertical position in the staff
     */
    private PositionInStaff positionInStaff;

    private Long id;

	public AgnosticSymbol(AgnosticVersion agnosticVersion, AgnosticSymbolType symbol, PositionInStaff position) {
		super(symbol);
		this.agnosticVersion = agnosticVersion;

		if (position == null) {
		    throw new IM3RuntimeException("Cannot create an agnostic symbols without position");
        }
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
        if (agnosticVersion == AgnosticVersion.v1) {
            sb.append(SEPVERTICALPOS_V1);
        } else {
            sb.append(SEPVERTICALPOS_V2);
        }
        sb.append(positionInStaff.toString());
        return sb.toString();
    }

    public static AgnosticSymbol parseAgnosticString(AgnosticVersion agnosticVersion, String string) throws IM3Exception {
	    String trimmedString = string.trim();
	    if (trimmedString.isEmpty()) {
	        throw new IM3RuntimeException("Empty string");
        }
        String separator;
        if (agnosticVersion == AgnosticVersion.v1) {
            separator = SEPVERTICALPOS_STR_V1;
        } else {
            separator = SEPVERTICALPOS_STR_V2;
        }
	    //String [] tokens = string.split("-");
        String [] tokens = string.split(separator);

        PositionInStaff positionInStaff = null;
        AgnosticSymbolType agnosticSymbolType = AgnosticSymbolTypeFactory.parseString(tokens[0]);
	    if (tokens.length == 2) {
            positionInStaff = PositionInStaff.parseString(tokens[1]);
        } else if (tokens.length > 2) {
	        throw new ImportException("Invalid agnostic symbol string: '" + string + "'");
        }
        return new AgnosticSymbol(agnosticVersion, agnosticSymbolType, positionInStaff);

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

    public PositionInStaff changeRelativePosition(int lineSpaces) {
        PositionInStaff prevPosition = positionInStaff;
	    positionInStaff = new PositionInStaff(positionInStaff.getLineSpace()+lineSpaces);
	    return prevPosition;
    }



    /**
     * @param agnosticSymbolType
     * @return Old value
     */
    public AgnosticSymbolType changeAgnosticSymbolType(AgnosticSymbolType agnosticSymbolType) {
        AgnosticSymbolType oldValue = this.symbol;
	    this.symbol = agnosticSymbolType;
        return oldValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
