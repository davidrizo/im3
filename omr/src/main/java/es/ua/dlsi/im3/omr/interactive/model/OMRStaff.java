package es.ua.dlsi.im3.omr.interactive.model;


import es.ua.dlsi.im3.omr.model.Symbol;

import java.util.List;

public class OMRStaff {
    private final int leftTopX;
    private final int leftTopY;
    private final int bottomRightX;
    private final int bottomRightY;
    private final OMRPage page;
    private List<Symbol> symbols;

    public OMRStaff(OMRPage page, int leftTopX, int leftTopY, int bottomRightX, int bottomRightY) {
        this.leftTopX = leftTopX;
        this.leftTopY = leftTopY;
        this.bottomRightX = bottomRightX;
        this.bottomRightY = bottomRightY;
        this.page = page;
    }

    public int getLeftTopX() {
        return leftTopX;
    }

    public int getLeftTopY() {
        return leftTopY;
    }

    public int getBottomRightX() {
        return bottomRightX;
    }

    public int getBottomRightY() {
        return bottomRightY;
    }

    public OMRPage getPage() {
        return page;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }
}
