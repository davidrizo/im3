package es.ua.dlsi.im3.omr.muret.editpage;

import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.editpage.regions.RegionTypeColors;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public abstract class RegionBaseView<OwnerType> extends Group {
    protected static final double FILL_OPACITY = 0.2;
    protected final Text label;
    protected final OwnerType ownerView;
    protected OMRRegion omrRegion;
    protected DraggableRectangle rectangle;

    public RegionBaseView(OwnerType ownerView, OMRRegion omrRegion) {
        this.setFocusTraversable(true); // to receive key events
        this.ownerView = ownerView;
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
        label.textProperty().bind(omrRegion.regionTypeProperty().asString().concat(" "+ omrRegion.toString()));
        label.fillProperty().bind(rectangle.strokeProperty());
        label.xProperty().bind(rectangle.xProperty().add(20)); // don't overlap with rectangle
        label.yProperty().bind(rectangle.yProperty().add(20)); // don't overlap with rectangle
        this.getChildren().add(label);
        label.setOnContextMenuRequested(event -> {
            onLabelContextMenuRequested(event);
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
            onRegionMouseClicked(event);
        });
    }

    protected abstract void onLabelContextMenuRequested(ContextMenuEvent event);

    protected abstract void onRegionMouseClicked(MouseEvent event);

    public OMRRegion getOmrRegion() {
        return omrRegion;
    }

    public void showRegionBoundingBox(boolean show) {
        rectangle.setVisible(show);
        label.setVisible(show);
    }
}
