package es.ua.dlsi.im3.omr.interactive.pageedit;

import es.ua.dlsi.im3.gui.javafx.DraggableRectangle;
import es.ua.dlsi.im3.omr.interactive.model.OMRSymbol;
import es.ua.dlsi.im3.omr.interactive.pageedit.events.RegionEditEvent;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;

public class RegionView extends Group {
    private static final double FILL_OPACITY = 0.2;
    private final Text label;
    private final PageView pageView;
    OMRRegion omrRegion;
    DraggableRectangle rectangle;
    HashMap<OMRSymbol, SymbolView> symbolViewHashMap;

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

        // symbols
        symbolViewHashMap = new HashMap<>();
        loadSymbols();
        initSymbolBinding();

    }

    private void initSymbolBinding() {
        omrRegion.symbolListProperty().addListener(new ListChangeListener<OMRSymbol>() {
            @Override
            public void onChanged(Change<? extends OMRSymbol> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        // no-op
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (OMRSymbol remitem : c.getRemoved()) {
                            removeSymbolView(remitem);
                        }
                        for (OMRSymbol additem : c.getAddedSubList()) {
                            createSymbolView(additem);
                        }
                    }
                }
            }
        });
    }

    private void removeSymbolView(OMRSymbol remitem) {
        SymbolView symbolView = symbolViewHashMap.remove(remitem);
        /*if (regionView == null) {
            throw new IM3RuntimeException("Item " + remitem + " not found");
        }*/
        this.getChildren().remove(symbolView);

    }

    private void loadSymbols() {
        for (OMRSymbol omrSymbol: omrRegion.symbolListProperty()) {
            createSymbolView(omrSymbol);
        }
    }

    private void createSymbolView(OMRSymbol omrSymbol) {
        SymbolView symbolView = new SymbolView(pageView, this, omrSymbol);
        symbolViewHashMap.put(omrSymbol, symbolView);
        getChildren().add(symbolView);
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
        //this.getParent().requestFocus();
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

    public OMRRegion getOmrRegion() {
        return omrRegion;
    }

    public void showRegionBoundingBox(boolean show) {
        rectangle.setVisible(show);
        label.setVisible(show);
    }

    public void showSymbols(boolean show) {
        for (SymbolView symbolView: symbolViewHashMap.values()) {
            symbolView.setVisible(show);
        }
    }

    public void bringToTop(SymbolView symbolView) {
        this.getChildren().remove(symbolView);
        this.getChildren().add(symbolView); // put on top

    }
}
