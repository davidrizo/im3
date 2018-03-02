package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Fermata extends AgnosticSymbolType {
    private static final String FERMATA = "fermata" + SEPSYMBOL;

    Positions positions;

    public Fermata(Positions positions) {
        this.positions = positions;
    }

    public Fermata() {

    }

    public Positions getPositions() {
        return positions;
    }

    public void setPositions(Positions positions) {
        this.positions = positions;
    }

    @Override
    public String toAgnosticString() {
        return FERMATA + positions.toAgnosticString();
    }
}
