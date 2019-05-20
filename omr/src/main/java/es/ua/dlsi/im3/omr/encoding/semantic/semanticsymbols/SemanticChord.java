package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.score.SimpleChord;

public class SemanticChord extends SemanticAtom<SimpleChord> {
    public SemanticChord(SimpleChord coreSymbol) {
        super(coreSymbol.clone());
    }

    @Override
    public String toSemanticString() {
        throw new UnsupportedOperationException("TO-DO");
    }
}
