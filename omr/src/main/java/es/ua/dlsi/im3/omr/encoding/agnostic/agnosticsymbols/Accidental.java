package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

import java.util.Objects;

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

    @Override
    public void setSubtype(String string) {
        accidentals = Accidentals.parseAgnosticString(string);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Accidental)) return false;
        if (!super.equals(o)) return false;
        Accidental that = (Accidental) o;
        return accidentals == that.accidentals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accidentals);
    }
}
