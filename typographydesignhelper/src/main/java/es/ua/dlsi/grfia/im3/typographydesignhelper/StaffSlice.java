package es.ua.dlsi.grfia.im3.typographydesignhelper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class StaffSlice extends Group {
    Line[] lines;

    public StaffSlice(ReadOnlyObjectProperty<Integer> thickness, DoubleProperty width, IntegerProperty fontSize) {
        this.lines = new Line[5];

        for (int i=0; i<lines.length; i++) {
            Line line = new Line();
            line.setStroke(Color.BLACK);
            line.strokeWidthProperty().bind(thickness);
            line.endXProperty().bind(width);
            line.startYProperty().bind(fontSize.multiply(i).divide(4).add(20)); // avoid problems on line width and margins
            line.endYProperty().bind(line.startYProperty());
            lines[4-i] = line;
            this.getChildren().add(line);
        }
    }

    public Line[] getLines() {
        return lines;
    }
}
