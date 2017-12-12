package es.ua.dlsi.im3.omr.interactive.documentanalysis;

import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.interactive.documentanalysis.events.RegionEditEvent;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class RegionView extends Group {
    private static final double FILL_OPACITY = 0.2;
    private final Text label;
    private final PageView pageView;
    OMRRegion omrRegion;
    DraggableRectangle rectangle;

    public RegionView(PageView pageView, OMRRegion omrRegion) {
        this.setFocusTraversable(true); // to receive key events
        this.pageView = pageView;
        this.omrRegion = omrRegion;
        rectangle = new DraggableRectangle(Color.GOLD);
        rectangle.hideHandles();
        rectangle.xProperty().bindBidirectional(omrRegion.fromXProperty());
        rectangle.yProperty().bindBidirectional(omrRegion.fromYProperty());
        rectangle.widthProperty().bindBidirectional(omrRegion.widthProperty());
        rectangle.heightProperty().bindBidirectional(omrRegion.heightProperty());
        rectangle.setStrokeWidth(0);
        this.getChildren().add(rectangle);

        label = new Text();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        label.textProperty().bind(omrRegion.regionTypeProperty().asString());
        label.fillProperty().bind(rectangle.strokeProperty());
        label.xProperty().bind(rectangle.xProperty().add(20)); // don't overlap with rectangle
        label.yProperty().bind(rectangle.yProperty().add(20)); // don't overlap with rectangle
        this.getChildren().add(label);
        label.setOnMouseClicked(event -> {
            showRegionTypeContextMenu(event.getScreenX(), event.getScreenY());
        });

        rectangle.setFill(RegionTypeColors.getInstance().getColor(omrRegion.getRegionType(), FILL_OPACITY));
        rectangle.setStroke(RegionTypeColors.getInstance().getColor(omrRegion.getRegionType(), 1));
        omrRegion.regionTypeProperty().addListener(new ChangeListener<RegionType>() {
            @Override
            public void changed(ObservableValue<? extends RegionType> observable, RegionType oldValue, RegionType newValue) {
                rectangle.setFill(RegionTypeColors.getInstance().getColor(omrRegion.getRegionType(), FILL_OPACITY));
                rectangle.setStroke(RegionTypeColors.getInstance().getColor(omrRegion.getRegionType(), 1));
            }
        });

        rectangle.setOnMouseClicked(event -> {
            pageView.handleEvent(new RegionEditEvent(event, this));
        });
    }

    private void showRegionTypeContextMenu(double screenX, double screenY) {
        ContextMenu contextMenu = new ContextMenu();
        for (RegionType regionType: RegionType.values()) {
            MenuItem menuItem = new MenuItem(regionType.name());
            contextMenu.getItems().add(menuItem);
            menuItem.setOnAction(event -> {
                omrRegion.setRegionType(regionType);
                contextMenu.hide();
            });
        }
        contextMenu.show(label, screenX, screenY);
    }

    public void beginEdit() {
        this.getParent().requestFocus();
        rectangle.setStrokeWidth(3);
        rectangle.beginEdit();
    }

    public void acceptEdit() {
        rectangle.setStrokeWidth(0);
        rectangle.endEdit(true);
    }

    public void cancelEdit() {
        rectangle.setStrokeWidth(0);
        rectangle.endEdit(false);
    }

}
