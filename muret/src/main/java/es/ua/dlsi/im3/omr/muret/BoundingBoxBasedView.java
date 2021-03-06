package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.muret.model.IOMRBoundingBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * View of any element selectable as a bounding box
 * @param <OwnerType>
 */
public abstract class BoundingBoxBasedView<OwnerType extends IOMRBoundingBox> extends Group {
    protected static final double FILL_OPACITY = 0.2;
    protected Text label;
    protected OwnerType owner;
    protected BoundingBoxBasedView parentBoundingBox;
    protected Color backgroundColor;
    protected DraggableRectangle rectangle;
    protected Color color;

    protected ObjectProperty<Boolean> selected;
    protected ImageBasedAbstractController controller;

    /**
     *
     * @param parentBoundingBox It may be null
     * @param fromX
     * @param fromY
     * @param width
     * @param height
     * @param owner
     * @param color
     */
    public BoundingBoxBasedView(ImageBasedAbstractController controller, BoundingBoxBasedView parentBoundingBox, double fromX, double fromY, double width, double height, OwnerType owner, Color color) {
        rectangle = new DraggableRectangle(Color.GOLD);
        rectangle.hideHandles();
        rectangle.xProperty().setValue(fromX);
        rectangle.yProperty().setValue(fromY);
        rectangle.widthProperty().setValue(width);
        rectangle.heightProperty().setValue(height);

        init(controller, parentBoundingBox, owner, color, rectangle);
    }

    /**
     *
     * @param parentBoundingBox It may be null
     * @param owner
     * @param color
     */
    public BoundingBoxBasedView(ImageBasedAbstractController controller, BoundingBoxBasedView parentBoundingBox, OwnerType owner, Color color) {
        rectangle = new DraggableRectangle(Color.GOLD);
        rectangle.hideHandles();
        rectangle.xProperty().bindBidirectional(owner.fromXProperty());
        rectangle.yProperty().bindBidirectional(owner.fromYProperty());
        rectangle.widthProperty().bindBidirectional(owner.widthProperty());
        rectangle.heightProperty().bindBidirectional(owner.heightProperty());
        init(controller, parentBoundingBox, owner, color, rectangle);
    }


    private void init(ImageBasedAbstractController controller, BoundingBoxBasedView parentBoundingBox, OwnerType owner, Color color, DraggableRectangle rectangle) {
        this.controller = controller;
        this.setFocusTraversable(true); // to receive key events
        this.owner = owner;
        this.parentBoundingBox = parentBoundingBox;
        this.color = color;
        this.getChildren().add(rectangle);

        label = new Text();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        label.textProperty().bind(owner.nameProperty());
        label.fillProperty().bind(rectangle.strokeProperty());
        label.xProperty().bind(rectangle.xProperty().add(20)); // don't overlap with rectangle
        label.yProperty().bind(rectangle.yProperty().add(20)); // don't overlap with rectangle
        this.getChildren().add(label);
        label.setOnContextMenuRequested(event -> {
            onLabelContextMenuRequested(event);
        });

        backgroundColor = getColor(color, FILL_OPACITY);
        rectangle.setStroke(getColor(color, 1));
        initInteraction();
    }

    private void initInteraction() {
        selected = new SimpleObjectProperty<>(false);

        rectangle.setOnMouseClicked(event -> {
            doSelect(true, true);
            onRegionMouseClicked(event); //TODo ¿quitar - cambiar por el cambio de selected?
        });


        rectangle.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!selected.get()) {
                    setHover(true);
                }
            }
        });

        rectangle.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!selected.get()) {
                    setHover(false);
                }
            }
        });

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
               @Override
               public void handle(MouseEvent event) {
                   doMousePressed(event);
               }
           }
        );

        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
               @Override
               public void handle(MouseEvent event) {
                   doMouseReleased(event);
               }
           }
        );

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                doMouseDragged(event);
            }
        });

        hoverProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                doHover(newValue);
            }
        });

        selected.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                doHighlight(newValue);
            }
        });
        doHighlight(false);
        doSelect(false, true);
    }

    private Color getColor(Color color, double opacity) {
        Color result = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        return result;
    }


    protected abstract void onLabelContextMenuRequested(ContextMenuEvent event);

    protected abstract void onRegionMouseClicked(MouseEvent event);

    /*public void showRegionBoundingBox(boolean show) {
        rectangle.setVisible(show);
        label.setVisible(show);
    }*/

    public void doHighlight(boolean highlight) {
        if (highlight) {
            rectangle.setStrokeWidth(2); //TODO
            rectangle.setFill(backgroundColor);
            label.setVisible(true);
        } else {
            rectangle.setStrokeWidth(1); //TODO
            rectangle.setFill(Color.TRANSPARENT);
            label.setVisible(false);
        }
    }

    protected void doHover(boolean enable) { //TODO Hacer una máquina de estados para doHighlight y doHover
        if (enable) {
            rectangle.setStrokeWidth(4);
        } else {
            rectangle.setStrokeWidth(1);
        }
    }

    public OwnerType getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return owner.toString();
    }

    public void doSelect(boolean select, boolean notifyController) {
        if (select != this.selected.get()) {
            this.selected.setValue(select);
            if (notifyController) {
                controller.doSelect(this);
            }
        }
    }

    public void handle(KeyEvent event) {
        // no - op by default
    }

    public void onSymbolRemoved(BoundingBoxBasedView elementView) {
        // no - op
    }

    protected void doMousePressed(MouseEvent event) {
        // no op - used for being overriden
    }

    protected void doMouseReleased(MouseEvent event) {
        // no op - used for being overriden
    }


    protected void doMouseDragged(MouseEvent event) {
        // no op - used for being overriden
    }


}
