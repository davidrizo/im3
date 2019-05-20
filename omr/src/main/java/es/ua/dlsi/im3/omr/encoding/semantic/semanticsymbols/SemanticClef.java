package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class SemanticClef extends SemanticSymbolType<Clef> {
    private static final String SEMANTIC = "clef" + SEPSYMBOL;

   /*public SemanticClef(NotationType notationType, ClefNote clefNote, int line) throws ImportException {
        super(ImportFactories.createClef(notationType, clefNote.name(), line, 0)); //TODO octave
    }*/

    public SemanticClef(Clef coreSymbol) {
        super(coreSymbol.clone());
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC + getNoteName() + this.coreSymbol.getLine();
    }

    private String getNoteName() {
        return coreSymbol.getNote().name();
    }

    @Override
    public String toKernSemanticString() {
        StringBuilder stringBuilder = new StringBuilder("*clef");
        stringBuilder.append(getNoteName()); //TODO Petrucci
        stringBuilder.append(coreSymbol.getLine());
        return stringBuilder.toString();
    }
}
