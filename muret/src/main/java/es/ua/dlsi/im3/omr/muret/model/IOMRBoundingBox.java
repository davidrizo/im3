package es.ua.dlsi.im3.omr.muret.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

public interface IOMRBoundingBox {
    DoubleProperty fromXProperty();
    DoubleProperty widthProperty();
    DoubleProperty fromYProperty();
    DoubleProperty heightProperty();
    StringProperty nameProperty();

}
