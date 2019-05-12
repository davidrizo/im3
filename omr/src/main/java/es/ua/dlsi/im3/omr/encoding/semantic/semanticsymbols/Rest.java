package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.SimpleRest;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;
import org.apache.commons.lang3.math.Fraction;

/**
 * @autor drizo
 */
public class Rest extends DurationalSymbol  {
    private static final String SEMANTIC = "rest" + SEPSYMBOL;

    public Rest(Figures figures, int dots, boolean fermata, Integer tupletNumber) {
        super(figures, dots, fermata, tupletNumber);
    }


    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder(SEMANTIC);

        sb.append(figures.name().toLowerCase()); //TODO ¿Para moderno y mensural?
        for (int i=0; i<dots; i++) {
            sb.append('.');
        }
        if (tupletNumber != null) {
            sb.append(SEPVALUES);
            sb.append(TUPLET);
            sb.append(tupletNumber);
        }
        if (fermata) {
            sb.append(SEPVALUES);
            sb.append(FERMATA);
        }

        return sb.toString();
    }

    @Override
    public String toKernSemanticString() throws ExportException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(KernExporter.generateDuration(figures, dots, Fraction.ONE)); //TODO fracciones para tresillos...
        stringBuilder.append('r');  //TODO slurs...
        return stringBuilder.toString();
    }

    @Override
    public SemanticSymbolType semantic2ScoreSong(ScoreLayer scoreLayer, SemanticSymbolType propagatedSymbolType) throws IM3Exception {
        SimpleRest rest = new SimpleRest(figures, dots);
        scoreLayer.add(rest);
        scoreLayer.getStaff().addCoreSymbol(rest);
        return propagatedSymbolType;
    }
}
