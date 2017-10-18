package es.ua.dlsi.im3.omr.primus.conversions;

public enum GraphicalSymbol {
    dot,
    clef,
    note,
    rest,
    accidental,
    separator,
    barline,
    thickbarline,
    line,
    metersign,
    digit,
    slur,
    fermata,
    multirest,
    gracenote,
    trill;


    /*public static final String DOT = "dot-";
    public static final String CLEF = "clef-";
    public static final String NOTE = "note.";
    public static final String REST = "rest.";
    public static final String ACC = "accidental.";
    public static final String BARLINE_0 = "barline-L1";
    public static final String LINE_2 = "-L2";
    public static final String LINE_4 = "-L4";
    public static final String THICKBARLINE_0 = "thickbarline_L1";
    public static final String TIMESIGNATURE = "timeSig.";*/


    @Override
    public String toString() {
        return this.name(); // podemos sobreescribirlo
    }
}
