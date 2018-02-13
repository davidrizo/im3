package es.ua.dlsi.im3.gui.javafx;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;

public class JavaFXUtils {
    public static void ensureVisibleY(ScrollPane scrollPane, Node node) {
        Bounds viewport = scrollPane.getViewportBounds();
        double contentHeight = scrollPane.getContent().localToScene(scrollPane.getContent().getBoundsInLocal()).getHeight();
        double nodeMinY = node.localToScene(node.getBoundsInLocal()).getMinY();
        double nodeMaxY = node.localToScene(node.getBoundsInLocal()).getMaxY();

        double vValueDelta = 0;
        double vValueCurrent = scrollPane.getVvalue();

        if (nodeMaxY < 0) {
            // currently located above (remember, top left is (0,0))
            vValueDelta = (nodeMinY - viewport.getHeight()) / contentHeight;
        } else if (nodeMinY > viewport.getHeight()) {
            // currently located below
            vValueDelta = (nodeMinY + viewport.getHeight()) / contentHeight;
        }
        scrollPane.setVvalue(vValueCurrent + vValueDelta);
    }
    public static void ensureVisibleX(ScrollPane scrollPane, Node node) {
        Bounds viewport = scrollPane.getViewportBounds();
        double contentWidth = scrollPane.getContent().localToScene(scrollPane.getContent().getBoundsInLocal()).getWidth();
        double nodeMinX = node.localToScene(node.getBoundsInLocal()).getMinX();
        double nodeMaxX = node.localToScene(node.getBoundsInLocal()).getMaxX();

        double hValueDelta = 0;
        double hValueCurrent = scrollPane.getHvalue();

        if (nodeMaxX < 0) {
            // currently located above (remember, top left is (0,0))
            hValueDelta = (nodeMinX - viewport.getWidth()) / contentWidth;
        } else if (nodeMinX > viewport.getWidth()) {
            // currently located below
            hValueDelta = (nodeMinX + viewport.getWidth()) / contentWidth;
        }
        scrollPane.setHvalue(hValueCurrent + hValueDelta);
    }

}
