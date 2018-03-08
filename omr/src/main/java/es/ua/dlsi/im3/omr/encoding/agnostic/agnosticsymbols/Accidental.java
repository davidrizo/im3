package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Accidental extends AgnosticSymbolType {
    private static final String ACCIDENTAL = "accidental" + SEPSYMBOL;

    Accidentals accidentals;

    public Accidental(Accidentals accidentals) {
        this.accidentals = accidentals;
    }


    public Accidental() {

    }

    public Accidentals getAccidentals() {
        return accidentals;
    }

    public void setAccidentals(Accidentals accidentals) {
        this.accidentals = accidentals;
    }

    @Override
    public String toAgnosticString() {
        return ACCIDENTAL + accidentals.toAgnosticString();
    }
}
