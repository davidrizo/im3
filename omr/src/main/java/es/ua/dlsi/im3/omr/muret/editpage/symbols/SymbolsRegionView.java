package es.ua.dlsi.im3.omr.muret.editpage.symbols;

import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.muret.editpage.RegionBaseView;
import es.ua.dlsi.im3.omr.muret.model.OMRRegion;
import es.ua.dlsi.im3.omr.muret.model.OMRSymbol;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolsRegionView extends Group {
    //TODO Poner stripeColors como privado - que funcione symbolViewArrayList que ahora está vacío
    /**
     * Used to visually distinguish adjacent symbols
     */
    public static Color[] stripeColors = {Color.RED, Color.GREEN, Color.BLUE};
    private static Color changedColor = Color.YELLOW;
    private final OMRRegion omrRegion;
    private final SymbolsStaffView symbolsStaffView;
    HashMap<OMRSymbol, SymbolView> symbolViewHashMap;
    private ArrayList<SymbolView> symbolViewArrayList;

    public SymbolsRegionView(SymbolsStaffView symbolsStaffView, OMRRegion omrRegion) {
        this.symbolsStaffView = symbolsStaffView;
        this.omrRegion = omrRegion;
        // symbols
        symbolViewHashMap = new HashMap<>();
        symbolViewArrayList = new ArrayList<>();
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
                            createSymbolView(additem, changedColor);
                        }
                    }
                }
            }
        });
    }

    private void removeSymbolView(OMRSymbol remitem) {
        SymbolView symbolView = symbolViewHashMap.remove(remitem);
        symbolViewArrayList.remove(symbolView);
        this.getChildren().remove(symbolView);

    }

    private void loadSymbols() {
        int i=0;
        for (OMRSymbol omrSymbol: omrRegion.symbolListProperty()) {
            Color color = stripeColors[i % stripeColors.length];
            createSymbolView(omrSymbol, color);
            i++;
        }
    }

    private void createSymbolView(OMRSymbol omrSymbol, Color color) {
        SymbolView symbolView = new SymbolView(symbolsStaffView, this, omrSymbol, color);
        symbolViewHashMap.put(omrSymbol, symbolView);
        symbolViewArrayList.add(symbolView);
        getChildren().add(symbolView);
    }

    public OMRRegion getOmrRegion() {
        return omrRegion;
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

    public ArrayList<SymbolView> getSymbolViewArrayList() {
        return symbolViewArrayList;
    }
}
