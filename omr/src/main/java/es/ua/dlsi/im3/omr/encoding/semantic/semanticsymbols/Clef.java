package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class Clef extends SemanticSymbolType {
    private static final String SEMANTIC = "clef" + SEPSYMBOL;
    private ClefNote clefNote;
    private int line;

    public Clef(ClefNote clefNote, int line) {
        this.clefNote = clefNote;
        this.line = line;
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC + clefNote.toSemanticString() + this.line;
    }

    @Override
    public String toKernSemanticString() {
        StringBuilder stringBuilder = new StringBuilder("*clef");
        stringBuilder.append(clefNote.toSemanticString()); //TODO Petrucci
        stringBuilder.append(line);
        return stringBuilder.toString();
    }

    @Override
    public SemanticSymbolType semantic2ScoreSong(ScoreLayer scoreLayer, SemanticSymbolType propagatedSymbolType) throws IM3Exception {
        es.ua.dlsi.im3.core.score.Clef clef = getIM3Clef(scoreLayer.getStaff().getNotationType());
        clef.setTime(scoreLayer.getDuration());
        scoreLayer.getStaff().addClef(clef);
        return propagatedSymbolType;
    }

    public es.ua.dlsi.im3.core.score.Clef getIM3Clef(NotationType notationType) throws ImportException {
        return ImportFactories.createClef(notationType, clefNote.name(), line, 0);
    }
}
