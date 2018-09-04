package es.ua.dlsi.im3.omr.classifiers.endtoend;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

/**
 * @autor drizo
 */
public class HorizontallyPositionedSymbol {
    double fromX;
    double toX;
    AgnosticSymbol agnosticSymbol;

    public HorizontallyPositionedSymbol(double fromX, double toX, AgnosticSymbol agnosticSymbol) {
        this.fromX = fromX;
        this.toX = toX;
        this.agnosticSymbol = agnosticSymbol;
    }

    public double getFromX() {
        return fromX;
    }

    public double getToX() {
        return toX;
    }

    public AgnosticSymbol getAgnosticSymbol() {
        return agnosticSymbol;
    }
}
