package es.ua.dlsi.im3.omr.interactive.editpage.symbols;

import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.interactive.model.OMRSymbol;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SymbolView extends Group {
    private static final String FONT_FAMILY = "Arial";
    private final SymbolsStaffView symbolsStaffView;
    OMRSymbol symbol;
    SymbolsRegionView symbolsRegionView;
    /*VBox labels;
    Text labelSymbolType;
    Text labelPosition;*/
    DraggableRectangle rectangle;
    Color color;

    public SymbolView(SymbolsStaffView symbolsStaffView, SymbolsRegionView region, OMRSymbol symbol, Color color) {
        this.symbol = symbol;
        this.symbolsRegionView = region;
        this.symbolsStaffView = symbolsStaffView;
        this.color = color;

        /*labels = new VBox(5);
        labelSymbolType = new Text();
        labelPosition = new Text();
        labels.getChildren().add(labelSymbolType);
        labels.getChildren().add(labelPosition);
        getChildren().add(labels);

        labelSymbolType.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 10));
        labelSymbolType.textProperty().bind(symbol.graphicalSymbolProperty().asString());
        labelPosition.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 9));
        labelPosition.textProperty().bind(symbol.positionInStaffProperty().asString());*/

        rectangle = new DraggableRectangle(Color.GOLD);
        rectangle.xProperty().bindBidirectional(symbol.xProperty());
        rectangle.yProperty().bindBidirectional(symbol.yProperty());
        rectangle.widthProperty().bindBidirectional(symbol.widthProperty());
        rectangle.heightProperty().bindBidirectional(symbol.heightProperty());
        //rectangle.setFill(SymbolTypeColors.getInstance().getColor(symbol.getGraphicalSymbol(), 0.2));
        rectangle.setFill(buildColor(color, 0.2));
        //rectangle.setStroke(SymbolTypeColors.getInstance().getColor(symbol.getGraphicalSymbol(), 1));
        rectangle.setStroke(buildColor(color, 1));
        rectangle.setStrokeWidth(0);
        symbol.graphicalSymbolProperty().addListener(new ChangeListener<GraphicalSymbol>() {
            @Override
            public void changed(ObservableValue<? extends GraphicalSymbol> observable, GraphicalSymbol oldValue, GraphicalSymbol newValue) {
                //rectangle.setFill(SymbolTypeColors.getInstance().getColor(symbol.getGraphicalSymbol(), 0.2));
                rectangle.setFill(buildColor(color, 0.2));
            }
        });
        /*labels.layoutXProperty().bind(rectangle.xProperty().add(-5)); // avoid problems to select
        //labelSymbolType.yProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()).add(3));
        labels.layoutYProperty().bind(rectangle.yProperty().add(5)); // avoid overlap on rectangle handles
        labelSymbolType.setRotate(-15);
        labelPosition.setRotate(-15);*/


        this.getChildren().add(rectangle);
        rectangle.hideHandles();

        rectangle.setOnMouseClicked(event -> {
            symbolsStaffView.handleEvent(new SymbolEditEvent(event, this));
        });

        /*labelSymbolType.setOnContextMenuRequested(event -> {
            showSymbolTypeContextMenu(event.getScreenX(), event.getScreenY());
        });
        labelPosition.setOnContextMenuRequested(event -> {
            showPositionContextMenu(event.getScreenX(), event.getScreenY());
        });*/
    }

    private Color buildColor(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    }

    private void showPositionContextMenu(double screenX, double screenY) {
        ContextMenu contextMenu = new ContextMenu();

        for (int i=-10; i<15; i++) {
            PositionInStaff positionInStaff = new PositionInStaff(i);
            MenuItem menuItem = new MenuItem(positionInStaff.toString());
            contextMenu.getItems().add(menuItem);
            menuItem.setOnAction(event -> {
                symbol.setPositionInStaff(positionInStaff);
                contextMenu.hide();
            });
        }
        contextMenu.show(this, screenX, screenY);
    }

    private void showSymbolTypeContextMenu(double screenX, double screenY) {
        ContextMenu contextMenu = new ContextMenu();
        for (GraphicalSymbol symbolType: GraphicalSymbol.values()) {
            MenuItem menuItem = new MenuItem(symbolType.name());
            contextMenu.getItems().add(menuItem);
            menuItem.setOnAction(event -> {
                symbol.setGraphicalSymbol(symbolType);
                symbol.setAccepted(true);
                contextMenu.hide();
            });
        }
        contextMenu.show(this, screenX, screenY);

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

    public SymbolsRegionView getRegionView() {
        return symbolsRegionView;
    }

    public Color getColor() {
        return color;
    }
}
