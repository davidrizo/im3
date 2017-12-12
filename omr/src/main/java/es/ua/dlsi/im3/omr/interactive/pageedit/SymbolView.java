package es.ua.dlsi.im3.omr.interactive.pageedit;

import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.interactive.model.OMRSymbol;
import es.ua.dlsi.im3.omr.model.pojo.Symbol;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SymbolView extends Group {
    OMRSymbol symbol;
    Text label;
    DraggableRectangle rectangle;

    public SymbolView(OMRSymbol symbol) {
        this.symbol = symbol;
        label = new Text();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 8));
        label.setRotate(90);
        label.textProperty().bind(symbol.graphicalTokenProperty().asString());
        rectangle = new DraggableRectangle(Color.color(0, 0, 1, 0.2));
        rectangle.xProperty().bindBidirectional(symbol.xProperty());
        rectangle.yProperty().bindBidirectional(symbol.yProperty());
        rectangle.widthProperty().bindBidirectional(symbol.widthProperty());
        rectangle.heightProperty().bindBidirectional(symbol.heightProperty());

        this.getChildren().add(label);
        this.getChildren().add(rectangle);
    }
}
