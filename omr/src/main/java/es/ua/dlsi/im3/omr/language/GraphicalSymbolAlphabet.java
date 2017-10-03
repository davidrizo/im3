package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.adt.dfa.Alphabet;
import es.ua.dlsi.im3.omr.primus.conversions.GraphicalSymbol;

import java.util.Set;

public class GraphicalSymbolAlphabet extends Alphabet<GraphicalSymbol> {
    public GraphicalSymbolAlphabet() {
        for (GraphicalSymbol graphicalSymbol: GraphicalSymbol.values()) {
            this.symbols.add(graphicalSymbol);
        }
    }
}
