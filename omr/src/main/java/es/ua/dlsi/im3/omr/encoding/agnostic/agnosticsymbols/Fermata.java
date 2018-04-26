package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Fermata extends AgnosticSymbolType {
    private static final String FERMATA = "fermata";

    Positions positions;

    public Fermata(Positions positions) {
        this.positions = positions;
    }

    public Fermata() {

    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
        positions = Positions.parseAgnosticString(string);
    }

    public Positions getPositions() {
        return positions;
    }

    public void setPositions(Positions positions) {
        this.positions = positions;
    }

    @Override
    public String toAgnosticString() {
        if (positions == null) {
            return FERMATA;
        } else {
            return FERMATA + SEPSYMBOL + positions.toAgnosticString();
        }
    }
}
