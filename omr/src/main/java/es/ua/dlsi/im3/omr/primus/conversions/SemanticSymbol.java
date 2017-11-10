package es.ua.dlsi.im3.omr.primus.conversions;

public enum SemanticSymbol {
    clef,
    note,
    rest,
    barline,
    line,
    timeSignature,
    keySignature,
    slur,
    multirest,
    gracenote,
    tie;

    @Override
    public String toString() {
        return this.name(); // podemos sobreescribirlo
    }

}
