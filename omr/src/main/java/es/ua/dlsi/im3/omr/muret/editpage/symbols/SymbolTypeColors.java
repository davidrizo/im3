package es.ua.dlsi.im3.omr.muret.editpage.symbols;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.omr.model.pojo.GraphicalSymbol;
import javafx.scene.paint.Color;

import java.util.HashMap;

/**
 * @deprecated Using stripe colors (interleaved colors)
 */
public class SymbolTypeColors {
    private HashMap<GraphicalSymbol, Color> colors;

    private SymbolTypeColors() {
        this.colors = new HashMap<>();
        this.colors.put(GraphicalSymbol.accidental, Color.AQUA);
        this.colors.put(GraphicalSymbol.barline, Color.AQUAMARINE);
        this.colors.put(GraphicalSymbol.clef, Color.BLUE);
        this.colors.put(GraphicalSymbol.digit, Color.BLUEVIOLET);
        this.colors.put(GraphicalSymbol.fermata, Color.BROWN);
        this.colors.put(GraphicalSymbol.dot, Color.BURLYWOOD);
        this.colors.put(GraphicalSymbol.gracenote, Color.CADETBLUE);
        this.colors.put(GraphicalSymbol.line, Color.CHARTREUSE);
        this.colors.put(GraphicalSymbol.metersign, Color.CHOCOLATE);
        this.colors.put(GraphicalSymbol.multirest, Color.CORAL);
        this.colors.put(GraphicalSymbol.note, Color.CORNFLOWERBLUE);
        this.colors.put(GraphicalSymbol.rest, Color.CRIMSON);
        this.colors.put(GraphicalSymbol.separator, Color.CYAN);
        this.colors.put(GraphicalSymbol.slur, Color.DARKCYAN);
        this.colors.put(GraphicalSymbol.text, Color.DARKGOLDENROD);
        this.colors.put(GraphicalSymbol.thickbarline, Color.DARKGREEN);
        this.colors.put(GraphicalSymbol.trill, Color.DARKKHAKI);
    }

    public static SymbolTypeColors instance = null;

    public static final SymbolTypeColors getInstance() {
        synchronized (SymbolTypeColors.class) {
            if (instance == null) {
                instance = new SymbolTypeColors();
            }
        }
        return instance;
    }
    public Color getColor(GraphicalSymbol symbol, double opacity) {
        Color color = colors.get(symbol);
        if (color == null) {
            throw new IM3RuntimeException("Cannot find a color for graphical symbol: " + symbol);
        }
        Color result = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        return result;
    }
}
