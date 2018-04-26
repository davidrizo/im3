package es.ua.dlsi.im3.omr.muret.regions;

import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.muret.model.IOMRBoundingBox;
import javafx.scene.Group;
import javafx.scene.input.ContextMenuEvent;
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
    protected final Text label;
    protected final OwnerType owner;
    private final IOMRBoundingBox parentBoundingBox;
    protected DraggableRectangle rectangle;

    public BoundingBoxBasedView(IOMRBoundingBox parentBoundingBox, OwnerType owner, Color color) {
        this.setFocusTraversable(true); // to receive key events
        this.owner = owner;
        this.parentBoundingBox = parentBoundingBox;

        rectangle = new DraggableRectangle(Color.GOLD);
        rectangle.hideHandles();
        rectangle.xProperty().bindBidirectional(owner.fromXProperty());
        rectangle.yProperty().bindBidirectional(owner.fromYProperty());
        rectangle.widthProperty().bindBidirectional(owner.widthProperty());
        rectangle.heightProperty().bindBidirectional(owner.heightProperty());
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

        rectangle.setFill(getColor(color, FILL_OPACITY));
        rectangle.setStroke(getColor(color, 1));

        rectangle.setOnMouseClicked(event -> {
            onRegionMouseClicked(event);
        });

        highlight(false);
    }

    private Color getColor(Color color, double opacity) {
        Color result = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        return result;
    }


    protected abstract void onLabelContextMenuRequested(ContextMenuEvent event);

    protected abstract void onRegionMouseClicked(MouseEvent event);

    public void showRegionBoundingBox(boolean show) {
        rectangle.setVisible(show);
        label.setVisible(show);
    }

    public void highlight(boolean highlight) {
        if (highlight) {
            rectangle.setStrokeWidth(2); //TODO
            label.setVisible(true);
        } else {
            rectangle.setStrokeWidth(0); //TODO
            label.setVisible(false);
        }
    }

    public OwnerType getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return owner.toString();
    }
}
