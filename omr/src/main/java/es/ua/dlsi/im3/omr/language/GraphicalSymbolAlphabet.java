package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.adt.dfa.Alphabet;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;

public class GraphicalSymbolAlphabet extends Alphabet<AgnosticSymbolType> {
    public GraphicalSymbolAlphabet() {
        add(new MeterSign());
        add(new Clef());
        add(new Accidental());
        add(new Custos());
        add(new Breath());
        add(new DalSengno());
        add(new Digit());
        add(new Dot());
        add(new Fermata());
        add(new GraceNote());
        add(new Multirest());
        add(new Note());
        add(new Rest());
        add(new Ligature());
        add(new Slur());
        add(new Trill());
        add(new ThickBarline());
        add(new VerticalLine());
    }
}
