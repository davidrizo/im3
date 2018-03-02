package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public abstract class DurationalSymbol extends SemanticSymbolType {
    protected static final String FERMATA = "fermata";

    Figures figures;
    int dots;
    boolean fermata;

    public DurationalSymbol(Figures figures, int dots, boolean fermata) {
        this.figures = figures;
        this.dots = dots;
        this.fermata = fermata;
    }
}
