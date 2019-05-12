package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public abstract class DurationalSymbol extends SemanticSymbolType {
    protected static final String FERMATA = "fermata";
    protected static final String TUPLET = "tuplet";


    Figures figures;
    int dots;
    boolean fermata;
    Integer tupletNumber;


    public DurationalSymbol(Figures figures, int dots, boolean fermata, Integer tupletNumber) {
        this.figures = figures;
        this.dots = dots;
        this.fermata = fermata;
        this.tupletNumber = tupletNumber;
    }

    public Figures getFigures() {
        return figures;
    }

    public void setFigures(Figures figures) {
        this.figures = figures;
    }

    public int getDots() {
        return dots;
    }

    public void setDots(int dots) {
        this.dots = dots;
    }

    public boolean isFermata() {
        return fermata;
    }

    public void setFermata(boolean fermata) {
        this.fermata = fermata;
    }

    public Integer getTupletNumber() {
        return tupletNumber;
    }

    public void setTupletNumber(Integer tupletNumber) {
        this.tupletNumber = tupletNumber;
    }
}
