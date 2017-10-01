package es.ua.dlsi.im3.core.score.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to group things like beams
 */
public class CompoundLayout {
    ArrayList<LayoutCoreSymbol>  coreSymbols;

    public CompoundLayout() {
        coreSymbols = new ArrayList<>();
    }

    public void add(LayoutCoreSymbol layoutCoreSymbol) {
        coreSymbols.add(layoutCoreSymbol);
        layoutCoreSymbol.setParent(this);
    }

    public List<LayoutCoreSymbol> getCoreSymbols() {
        return coreSymbols;
    }
}
