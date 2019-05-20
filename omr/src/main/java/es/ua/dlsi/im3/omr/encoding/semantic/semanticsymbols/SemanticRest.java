package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.Fermata;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.SimpleRest;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;

/**
 * @autor drizo
 */
public class SemanticRest extends SemanticAtom<SimpleRest> {
    private static final String SEMANTIC = "rest" + SEPSYMBOL;
    protected static final String TUPLET = "tuplet";

    boolean fermata;


    /*public SemanticRest(Figures figures, int dots, boolean fermata, Integer tupletNumber) {
        super(new SimpleRest(figures, dots));
        this.fermata = fermata;
        this.tupletNumber = tupletNumber; //TODO fermata and tuplets

    }*/

    public SemanticRest(SimpleRest coreSymbol) {
        super(coreSymbol.clone());
        this.fermata = coreSymbol.getAtomFigure().getFermata() != null;
    }

    @Override
    public String toSemanticString() throws IM3Exception {
        StringBuilder sb = new StringBuilder(SEMANTIC);

        Figures figures = coreSymbol.getAtomFigure().getFigure();
        int dots = coreSymbol.getAtomFigure().getDots();

        sb.append(figures.name().toLowerCase()); //TODO ¿Para moderno y mensural?
        for (int i=0; i<dots; i++) {
            sb.append('.');
        }
        /*if (tupletNumber != null) {
            sb.append(SEPVALUES);
            sb.append(TUPLET);
            sb.append(tupletNumber);
        }*/

        if (fermata) {
            sb.append(SEPVALUES);
            sb.append(FERMATA);
        }

        /*//TODO código copiado en SemanticNote
        if (coreSymbol.getAtomFigure().isExplicitMensuralPerfection()) {
            Perfection perfection = coreSymbol.getAtomFigure().getMensuralPerfection();
            if (perfection != null) {
                switch (perfection) {
                    case perfectum:
                        sb.append('p');
                        break;
                    case imperfectum:
                        sb.append('i');
                        break;
                    default:
                        throw new IM3Exception("Unsupported perfection type: " + perfection);
                }
            } else {
                throw new IM3Exception("Expected a perfection");
            }
        }*/
        return sb.toString();
    }
}
