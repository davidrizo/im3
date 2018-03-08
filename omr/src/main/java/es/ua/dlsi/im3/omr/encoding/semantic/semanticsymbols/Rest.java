package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class Rest extends DurationalSymbol  {
    private static final String SEMANTIC = "rest" + SEPSYMBOL;

    public Rest(Figures figures, int dots, boolean fermata) {
        super(figures, dots, fermata);
    }


    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder(SEMANTIC);

        sb.append(figures.name().toLowerCase()); //TODO ¿Para moderno y mensural?
        for (int i=0; i<dots; i++) {
            sb.append('.');
        }
        if (fermata) {
            sb.append(SEPVALUES);
            sb.append(FERMATA);
        }

        return sb.toString();
    }
}
