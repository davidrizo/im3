package es.ua.dlsi.im3.omr.interactive.documentanalysis;

import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class RegionView extends Group {
    OMRRegion omrRegion;
    Rectangle rectangle;

    public RegionView(OMRRegion omrRegion) {
        this.omrRegion = omrRegion;
        rectangle = new Rectangle();
        rectangle.xProperty().bind(omrRegion.fromXProperty());
        rectangle.yProperty().bind(omrRegion.fromYProperty());
        rectangle.widthProperty().bind(omrRegion.toXProperty().subtract(omrRegion.fromXProperty()));
        rectangle.heightProperty().bind(omrRegion.toYProperty().subtract(omrRegion.fromYProperty()));
        rectangle.setFill(RegionTypeColors.getInstance().getColor(omrRegion.getRegionType()));
        rectangle.setOpacity(0.2);
        rectangle.strokeProperty().bind(rectangle.fillProperty());
        highlight(false);
        this.getChildren().add(rectangle);

        Text label = new Text();
        label.textProperty().bind(omrRegion.regionTypeProperty().asString());
        label.fillProperty().bind(rectangle.fillProperty());
        label.xProperty().bind(rectangle.xProperty());
        label.yProperty().bind(rectangle.yProperty());
        this.getChildren().add(label);
    }

    public void highlight(boolean highlight) {
        if (highlight) {
            rectangle.setStrokeWidth(2);
        } else {
            rectangle.setStrokeWidth(0);
        }
    }
}
