package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

import static es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType.SEPPROPERTIES;

/**
 * Abstract mark used for slurs and brackets
 * @autor drizo
 */
public abstract class MarkUpDownStartEnd extends AgnosticSymbolType {

    StartEnd startEnd;
    /**
     * Used in version 2
     */
    Positions positions;

    public MarkUpDownStartEnd(StartEnd startEnd) {
        this.startEnd = startEnd;
    }
    public MarkUpDownStartEnd(StartEnd startEnd, Positions positions) {
        this.startEnd = startEnd;
    }

    public MarkUpDownStartEnd() {
    }

    public Positions getPositions() {
        return positions;
    }

    public void setPositions(Positions positions) {
        this.positions = positions;
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
        String [] tokens = string.split(SEPPROPERTIES);
        if (tokens.length == 1) {
            startEnd = StartEnd.parseAgnosticString(string);
        } else if (tokens.length == 2) {
            startEnd = StartEnd.parseAgnosticString(tokens[0]);
            positions = Positions.parseAgnosticString(tokens[1]);
        } else {
            throw new IM3Exception("Expected 1 or 2 tokens, and found " + tokens.length);
        }
    }

    public StartEnd getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(StartEnd startEnd) {
        this.startEnd = startEnd;
    }

    protected abstract String getAgnosticTypeString();

    @Override
    public String toAgnosticString() {
        if (positions == null) {
            return getAgnosticTypeString() + SEPSYMBOL + startEnd.toAgnosticString();
        } else {
            return getAgnosticTypeString() + SEPSYMBOL +startEnd.toAgnosticString() + SEPPROPERTIES + positions.toAgnosticString();
        }
    }
}
