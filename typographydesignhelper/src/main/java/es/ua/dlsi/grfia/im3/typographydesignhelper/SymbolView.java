package es.ua.dlsi.grfia.im3.typographydesignhelper;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SymbolView extends Group {
    Text text;
    StaffSlice staffSlice;

    public SymbolView(ReadOnlyObjectProperty<Integer> thickness, IntegerProperty fontSize, DoubleBinding verticalPosition, ObjectProperty<Font> font, String unicode) {
        text = new Text(unicode);
        text.fontProperty().bind(font);

        DoubleProperty width = new SimpleDoubleProperty(text.getWrappingWidth());
        text.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                width.set(newValue.getWidth());
            }
        });
        staffSlice = new StaffSlice(thickness, width, fontSize);
        text.yProperty().bind(staffSlice.getLines()[0].startYProperty().add(fontSize.divide(4).multiply(verticalPosition)));
        this.getChildren().add(staffSlice);
        this.getChildren().add(text);
    }

    public void setFill(Color color) {
        text.setFill(color);
    }
}
