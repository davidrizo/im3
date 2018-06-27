package es.ua.dlsi.im3.gui.javafx;

import es.ua.dlsi.im3.core.IM3Exception;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * From https://stackoverflow.com/questions/26298873/resizable-and-movable-rectangle
 */
public class DraggableRectangle extends Group {
    Rectangle rect;
    Group handles;
    double prevX;
    double prevY;
    double prevWidth;
    double prevHeight;

    IDraggableRectangleChangeHandler changeHandler;

    public DraggableRectangle(Paint handlesColor) {
        this(0,0,0,0, handlesColor);
    }

    public DraggableRectangle(double x, double y, double width, double height, Paint handlesColor) {
        handles = new Group();
        createDraggableRectangle(x, y, width, height, handlesColor);
        this.getChildren().add(rect);
        this.getChildren().add(handles);
    }

    public void beginEdit() {
        handles.setVisible(true);
        prevX = rect.getX();
        prevY = rect.getY();
        prevWidth = rect.getWidth();
        prevHeight = rect.getHeight();
    }

    public void endEdit(boolean accept) {
        if (accept) {
            if (changeHandler != null) {
                try {
                    changeHandler.onChanged(prevX, prevY, prevWidth, prevHeight, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                } catch (IM3Exception e) {
                    Logger.getLogger(DraggableRectangle.class.getName()).log(Level.INFO, "Cannot apply edit to rectangle", e);
                    endEdit(false);
                    return;
                }
            }
        } else {
            rect.setX(prevX);
            rect.setY(prevY);
            rect.setWidth(prevWidth);
            rect.setHeight(prevHeight);
        }
        handles.setVisible(false);
    }

    public IDraggableRectangleChangeHandler getChangeHandler() {
        return changeHandler;
    }

    public void setChangeHandler(IDraggableRectangleChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    public DoubleProperty xProperty() {
        return rect.xProperty();
    }

    public DoubleProperty yProperty() {
        return rect.yProperty();
    }

    public DoubleProperty heightProperty() {
        return rect.heightProperty();
    }

    public DoubleProperty widthProperty() {
        return rect.widthProperty();
    }

    public ObjectProperty<Paint> fillProperty() {
        return rect.fillProperty();
    }

    public ObjectProperty<Paint> strokeProperty() {
        return rect.strokeProperty();
    }

    private void createDraggableRectangle(double x, double y, double width, double height, Paint handlesColor) {
        final double handleRadius = 5;

        rect = new Rectangle(x, y, width, height);

        // top left resize handle:
        Circle resizeHandleNW = new Circle(handleRadius, handlesColor);
        handles.getChildren().add(resizeHandleNW);
        // bind to top left corner of Rectangle:
        resizeHandleNW.centerXProperty().bind(rect.xProperty());
        resizeHandleNW.centerYProperty().bind(rect.yProperty());

        // bottom right resize handle:
        Circle resizeHandleSE = new Circle(handleRadius, handlesColor);
        handles.getChildren().add(resizeHandleSE);
        // bind to bottom right corner of Rectangle:
        resizeHandleSE.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
        resizeHandleSE.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

        // move handle:
        Circle moveHandle = new Circle(handleRadius, handlesColor);
        handles.getChildren().add(moveHandle);
        // bind to bottom center of Rectangle:
        moveHandle.centerXProperty().bind(rect.xProperty().add(rect.widthProperty().divide(2)));
        moveHandle.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

        // force circles to live in same parent as rectangle:
        /*rect.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Circle c : Arrays.asList(resizeHandleNW, resizeHandleSE, moveHandle)) {
                Pane currentParent = (Pane)c.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(c);
                }
                ((Pane)newParent).getChildren().add(c);
            }
        });*/

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
                event.consume();
            }
        });

        resizeHandleSE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newMaxX = rect.getX() + rect.getWidth() + deltaX ;
                if (newMaxX >= rect.getX()
                        //&& newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                        //&& newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) { // David because the rectangle is inside this group
                        ) {
                    rect.setWidth(rect.getWidth() + deltaX);
                }
                double newMaxY = rect.getY() + rect.getHeight() + deltaY ;
                if (newMaxY >= rect.getY()
                        //&& newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                        //&& newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) { // David because the rectangle is inside this group
                        ) {
                    rect.setHeight(rect.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                event.consume();
            }
        });

        moveHandle.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = rect.getX() + deltaX ;
                double newMaxX = newX + rect.getWidth();
                if (newX >= handleRadius
                        //&& newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                       // && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) { // David because the rectangle is inside this group
                        ) {
                    rect.setX(newX);
                }
                double newY = rect.getY() + deltaY ;
                double newMaxY = newY + rect.getHeight();
                if (newY >= handleRadius
                       // && newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                       // && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) { // David because the rectangle is inside this group
                        ) {
                    rect.setY(newY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
                event.consume();
            }

        });
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

    public void setFill(Color color) {
        rect.setFill(color);
    }

    public void setStroke(Color color) {
        rect.setStroke(color);
    }

    public void setStrokeWidth(int width) {
        rect.setStrokeWidth(width);
    }

    public void hideHandles() {
        handles.setVisible(false);
    }


    static class Wrapper<T> { T value ; }

}
