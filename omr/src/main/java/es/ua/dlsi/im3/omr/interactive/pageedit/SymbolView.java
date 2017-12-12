package es.ua.dlsi.im3.omr.interactive.pageedit;

import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.interactive.model.OMRSymbol;
import es.ua.dlsi.im3.omr.interactive.pageedit.events.SymbolEditEvent;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.Symbol;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SymbolView extends Group {
    private final PageView pageView;
    OMRSymbol symbol;
    RegionView region;
    Text label;
    DraggableRectangle rectangle;

    public SymbolView(PageView pageView, RegionView region, OMRSymbol symbol) {
        this.symbol = symbol;
        this.region = region;
        this.pageView = pageView;
        label = new Text();
        label.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        label.setRotate(-90);
        label.textProperty().bind(symbol.graphicalTokenProperty().asString());
        rectangle = new DraggableRectangle(Color.GOLD);
        rectangle.xProperty().bindBidirectional(symbol.xProperty());
        rectangle.yProperty().bindBidirectional(symbol.yProperty());
        rectangle.widthProperty().bindBidirectional(symbol.widthProperty());
        rectangle.heightProperty().bindBidirectional(symbol.heightProperty());
        rectangle.setFill(SymbolTypeColors.getInstance().getColor(symbol.getGraphicalToken().getSymbol(), 0.2));
        rectangle.setStroke(SymbolTypeColors.getInstance().getColor(symbol.getGraphicalToken().getSymbol(), 1));
        rectangle.setStrokeWidth(0);
        symbol.graphicalTokenProperty().addListener(new ChangeListener<GraphicalToken>() {
            @Override
            public void changed(ObservableValue<? extends GraphicalToken> observable, GraphicalToken oldValue, GraphicalToken newValue) {
                rectangle.setFill(SymbolTypeColors.getInstance().getColor(symbol.getGraphicalToken().getSymbol(), 0.2));
            }
        });
        label.xProperty().bind(rectangle.xProperty().add(3));
        label.yProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()).add(3));

        this.getChildren().add(label);
        this.getChildren().add(rectangle);
        rectangle.hideHandles();

        rectangle.setOnMouseClicked(event -> {
            pageView.handleEvent(new SymbolEditEvent(event, this));
        });

    }

    public void beginEdit() {
        rectangle.beginEdit();
        rectangle.setStrokeWidth(2);
    }

    public void acceptEdit() {
        rectangle.setStrokeWidth(0);
        rectangle.endEdit(true);
    }

    public void cancelEdit() {
        rectangle.setStrokeWidth(0);
        rectangle.endEdit(false);
    }

    public OMRSymbol getOmrSymbol() {
        return symbol;
    }

    public RegionView getRegionView() {
        return region;
    }
}
