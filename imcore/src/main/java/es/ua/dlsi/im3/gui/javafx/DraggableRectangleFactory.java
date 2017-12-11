package es.ua.dlsi.im3.gui.javafx;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;

/**
 * From https://stackoverflow.com/questions/26298873/resizable-and-movable-rectangle
 */
public class DraggableRectangleFactory {
    private static DraggableRectangleFactory ourInstance = new DraggableRectangleFactory();

    public static DraggableRectangleFactory getInstance() {
        return ourInstance;
    }

    private DraggableRectangleFactory() {
    }
    public Rectangle createDraggableRectangle(double x, double y, double width, double height, Paint handlesColor) {
        final double handleRadius = 10 ;

        Rectangle rect = new Rectangle(x, y, width, height);

        // top left resize handle:
        Circle resizeHandleNW = new Circle(handleRadius, handlesColor);
        // bind to top left corner of Rectangle:
        resizeHandleNW.centerXProperty().bind(rect.xProperty());
        resizeHandleNW.centerYProperty().bind(rect.yProperty());

        // bottom right resize handle:
        Circle resizeHandleSE = new Circle(handleRadius, handlesColor);
        // bind to bottom right corner of Rectangle:
        resizeHandleSE.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
        resizeHandleSE.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

        // move handle:
        Circle moveHandle = new Circle(handleRadius, handlesColor);
        // bind to bottom center of Rectangle:
        moveHandle.centerXProperty().bind(rect.xProperty().add(rect.widthProperty().divide(2)));
        moveHandle.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

        // force circles to live in same parent as rectangle:
        rect.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Circle c : Arrays.asList(resizeHandleNW, resizeHandleSE, moveHandle)) {
                Pane currentParent = (Pane)c.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(c);
                }
                ((Pane)newParent).getChildren().add(c);
            }
        });

        Wrapper<Point2D> mouseLocation = new Wrapper<>();

        setUpDragging(resizeHandleNW, mouseLocation) ;
        setUpDragging(resizeHandleSE, mouseLocation) ;
        setUpDragging(moveHandle, mouseLocation) ;

        resizeHandleNW.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = rect.getX() + deltaX ;
                if (newX >= handleRadius
                        && newX <= rect.getX() + rect.getWidth() - handleRadius) {
                    rect.setX(newX);
                    rect.setWidth(rect.getWidth() - deltaX);
                }
                double newY = rect.getY() + deltaY ;
                if (newY >= handleRadius
                        && newY <= rect.getY() + rect.getHeight() - handleRadius) {
                    rect.setY(newY);
                    rect.setHeight(rect.getHeight() - deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleSE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newMaxX = rect.getX() + rect.getWidth() + deltaX ;
                if (newMaxX >= rect.getX()
                        && newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    rect.setWidth(rect.getWidth() + deltaX);
                }
                double newMaxY = rect.getY() + rect.getHeight() + deltaY ;
                if (newMaxY >= rect.getY()
                        && newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    rect.setHeight(rect.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        moveHandle.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = rect.getX() + deltaX ;
                double newMaxX = newX + rect.getWidth();
                if (newX >= handleRadius
                        && newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    rect.setX(newX);
                }
                double newY = rect.getY() + deltaY ;
                double newMaxY = newY + rect.getHeight();
                if (newY >= handleRadius
                        && newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    rect.setY(newY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }

        });

        return rect ;
    }

    private void setUpDragging(Circle circle, Wrapper<Point2D> mouseLocation) {

        circle.setOnDragDetected(event -> {
            circle.getParent().setCursor(Cursor.CLOSED_HAND);
            mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
        });

        circle.setOnMouseReleased(event -> {
            circle.getParent().setCursor(Cursor.DEFAULT);
            mouseLocation.value = null ;
        });
    }

    static class Wrapper<T> { T value ; }

}
