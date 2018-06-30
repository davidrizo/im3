package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;

public class LayoutMark extends NotationSymbol {
    @Override
    public GraphicsElement getGraphics() {
        return null;
    }

    @Override
    protected void doLayout() {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
