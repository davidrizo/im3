package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreBarline;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.layout.layoutengines.BelliniLayoutEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * It contains staves that can be split in several. Symbols like
 * slurs may also be splitted into two parts
 * @author drizo
 *
 */
public abstract class ScoreLayout {
    protected final ScoreSong scoreSong;
    protected final LayoutFont layoutFont;
    protected final LayoutSymbolFactory layoutSymbolFactory;
    protected final Simultaneities simultaneities;
    protected final double noteHeadWidth;
    protected HashMap<Staff, List<LayoutCoreSymbolInStaff>> coreSymbols;
    protected final List<LayoutCoreBarline> barlines;

    public ScoreLayout(ScoreSong song, LayoutFonts font) throws IM3Exception { //TODO ¿y si tenemos que sacar sólo unos pentagramas?
        this.scoreSong = song;
        layoutFont = FontFactory.getInstance().getFont(font);
        layoutSymbolFactory = new LayoutSymbolFactory();
        simultaneities = new Simultaneities();
        barlines = new ArrayList<>();

        Pictogram noteHead = new Pictogram("_NHWC_", getLayoutFont(), NotePitch.NOTE_HEAD_WIDTH_CODEPOINT, // TODO: 22/9/17 Quizás esto debería ser cosa del FontLayout
                new Coordinate(new CoordinateComponent(0),
                        new CoordinateComponent(0)
                ));
        noteHeadWidth = noteHead.getWidth();
        
        createLayoutSymbols();
    }

    private void createLayoutSymbols() throws IM3Exception {
        coreSymbols = new HashMap<>();
        for (Staff staff: scoreSong.getStaves()) {
            ArrayList<LayoutCoreSymbolInStaff> coreSymbolsInStaff = new ArrayList<>();
            coreSymbols.put(staff, coreSymbolsInStaff);
            // add contents of staff
            List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();
            for (ITimedElementInStaff symbol: symbols) {
                LayoutCoreSymbol layoutCoreSymbol = layoutSymbolFactory.createCoreSymbol(layoutFont, symbol);
                //createLayout(symbol, layoutStaff);
                if (layoutCoreSymbol != null) {
                    simultaneities.add(layoutCoreSymbol);
                    if (layoutCoreSymbol instanceof LayoutCoreSymbolInStaff) {
                        coreSymbolsInStaff.add((LayoutCoreSymbolInStaff) layoutCoreSymbol);
                    } else {
                        throw new IM3RuntimeException("Unimplemented " + layoutCoreSymbol.getClass()); // TODO: 24/9/17 Debemos ponerlos en otra lista? Beamed groups?
                    }
                }
            }

            // create barlines
            // TODO: 21/9/17 Deberíamos poder crear barlines de system
            for (Measure measure: scoreSong.getMeasures()) {
                LayoutCoreBarline barline = new LayoutCoreBarline(layoutFont, measure.getEndTime());
                simultaneities.add(barline);
                barlines.add(barline);
            }

            //layoutStaff.createBeaming();
            //System.out.println("Staff " + staff.getNumberIdentifier());
            //simultaneities.printDebug();
        }
    }

    protected void doHorizontalLayout(Simultaneities simultaneities) throws IM3Exception {
        // Replace for a factory if required
        ILayoutEngine layoutEngine = new BelliniLayoutEngine(1, 1, noteHeadWidth/2); // TODO: 22/9/17 ¿qué valor ponemos?
        layoutEngine.reset(simultaneities);
        layoutEngine.doHorizontalLayout(simultaneities);
    }


    public abstract void layout() throws IM3Exception;
    public abstract List<Canvas> getCanvases();

    public LayoutFont getLayoutFont() {
        return layoutFont;
    }
}
