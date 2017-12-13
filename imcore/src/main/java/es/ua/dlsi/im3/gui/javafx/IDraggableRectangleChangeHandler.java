package es.ua.dlsi.im3.gui.javafx;

import es.ua.dlsi.im3.core.IM3Exception;

public interface IDraggableRectangleChangeHandler {
    void onChanged(double prevX, double prevY, double prevWidth, double prevHeight, double x, double y, double width, double height) throws IM3Exception;
}
