package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.adt.dfa.Alphabet;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;

public class GraphicalSymbolAlphabet extends Alphabet<AgnosticSymbolType> {
    public GraphicalSymbolAlphabet() {
        this.symbols.add(new MeterSign());
        this.symbols.add(new Clef());
        this.symbols.add(new Accidental());
        this.symbols.add(new Custos());
        this.symbols.add(new DalSengno());
        this.symbols.add(new Digit());
        this.symbols.add(new Dot());
        this.symbols.add(new Fermata());
        this.symbols.add(new GraceNote());
        this.symbols.add(new Multirest());
        this.symbols.add(new Note());
        this.symbols.add(new Rest());
        this.symbols.add(new Slur());
        this.symbols.add(new Trill());
        this.symbols.add(new ThickBarline());
        this.symbols.add(new VerticalLine());
    }
}
