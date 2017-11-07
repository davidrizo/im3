package es.ua.dlsi.im3.omr.wrimus;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class HomusDataset {
    HashMap<Symbol, List<Glyph>> glyphs;

    public HomusDataset() {
        this.glyphs = new HashMap<>();
    }

    public void addGlyph(Glyph glyph) {
        List<Glyph> list = glyphs.get(glyph.getSymbol());
        if (list == null) {
            list = new LinkedList<>();
            this.glyphs.put(glyph.getSymbol(), list);
        }

        list.add(glyph);
    }

    public HashMap<Symbol, List<Glyph>> getGlyphs() {
        return glyphs;
    }

    public List<Glyph> getGlyphs(Symbol symbol) throws IM3Exception {
        List<Glyph> list = glyphs.get(symbol);
        if (list == null) {
            throw new IM3Exception("Symbol '" + symbol + "' not found");
        }
        return list;
    }

    public List<Glyph> getGlyphs(String name) throws IM3Exception {
        return getGlyphs(new Symbol(name));
    }

    /**
     *
     * @throws IM3Exception When some symbol is not found
     */
    public void checkCompleteness() throws IM3Exception {
        String [] symbolNames = {"Barline", "C-Clef", "Common-Time", "Cut-Time", "Dot", "Double-Sharp", "Eighth-Note", "Eighth-Rest", "F-Clef", "Flat", "G-Clef", "Half-Note", "Natural", "Quarter-Note", "Quarter-Rest", "Sharp", "Sixteenth-Note", "Sixteenth-Rest", "Sixty-Four-Note", "Sixty-Four-Rest", "Thirty-Two-Note", "Thirty-Two-Rest", "Whole-Half-Rest", "Whole-Note"};
        for (int i=0; i<symbolNames.length; i++) {
            getGlyphs(symbolNames[i]);
        }
    }
}
