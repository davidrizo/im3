package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.score.Figures;
public class FigureAndDots {
    Figures figure;
    int dots;

    public FigureAndDots(Figures figure, int dots) {
        this.figure = figure;
        this.dots = dots;
    }

    public Figures getFigure() {
        return figure;
    }

    public int getDots() {
        return dots;
    }

    @Override
    public String toString() {
        return "FigureAndDots{" +
                "figure=" + figure +
                ", dots=" + dots +
                '}';
    }
}
