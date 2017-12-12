package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.adt.dfa.Alphabet;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;

public class GraphicalSymbolAlphabet extends Alphabet<GraphicalSymbol> {
    public GraphicalSymbolAlphabet() {
        for (GraphicalSymbol graphicalSymbol: GraphicalSymbol.values()) {
            this.symbols.add(graphicalSymbol);
        }
    }
}
