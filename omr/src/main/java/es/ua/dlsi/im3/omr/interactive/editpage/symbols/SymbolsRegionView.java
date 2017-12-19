package es.ua.dlsi.im3.omr.interactive.editpage.symbols;

import es.ua.dlsi.im3.omr.interactive.editpage.RegionBaseView;
import es.ua.dlsi.im3.omr.interactive.model.OMRRegion;
import es.ua.dlsi.im3.omr.interactive.model.OMRSymbol;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;

public class SymbolsRegionView extends RegionBaseView<SymbolsPageView> {
    HashMap<OMRSymbol, SymbolView> symbolViewHashMap;

    public SymbolsRegionView(SymbolsPageView pageView, OMRRegion omrRegion) {
        super(pageView, omrRegion);
        // symbols
        symbolViewHashMap = new HashMap<>();
        loadSymbols();
        initSymbolBinding();
    }


    @Override
    protected void onLabelContextMenuRequested(ContextMenuEvent event) {

    }

    @Override
    protected void onRegionMouseClicked(MouseEvent event) {

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
