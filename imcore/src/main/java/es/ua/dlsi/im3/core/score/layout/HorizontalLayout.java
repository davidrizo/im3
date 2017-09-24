package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutBarline;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.layout.layoutengines.BelliniLayoutEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * All systems are arranged in a single line
 */
public class HorizontalLayout extends ScoreLayout {
    List<LayoutStaff> staves; //TODO systems
    LayoutSymbolFactory layoutSymbolFactory;
    /**
     * Everything is arranged in a single canvas
     */
    Canvas canvas;
    public HorizontalLayout(ScoreSong song, LayoutFonts font, Coordinate leftTop, Coordinate bottomRight) {
        super(song, font);
        layoutSymbolFactory = new LayoutSymbolFactory();
        canvas = new Canvas(leftTop, bottomRight);
    }

    @Override
    public void layout() throws IM3Exception {
        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        canvas.clear();

        //TODO scoreSong.getStaffSystems()
        staves = new ArrayList<>(); //TODO supongo que no habrá que rehacerlo siempre

        // first create symbols and simultaneities
        Simultaneities simultaneities = new Simultaneities();

        double nextY = LayoutConstants.TOP_MARGIN;
        for (Staff staff: scoreSong.getStaves()) {
            CoordinateComponent y = new CoordinateComponent(canvas.getLeftTop().getY(), nextY);
            nextY += LayoutConstants.STAFF_SEPARATION;
            Coordinate leftTop = new Coordinate(canvas.getLeftTop().getX(), y);
            Coordinate rightTop = new Coordinate(canvas.getBottomRight().getX(), y);

            LayoutStaff layoutStaff = new LayoutStaff(this, leftTop, rightTop, staff);
            staves.add(layoutStaff);
            canvas.add(layoutStaff.getGraphics());

            // add contents of staff
            List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();
            for (ITimedElementInStaff symbol: symbols) {
                LayoutSymbolInStaff layoutSymbolInStaff = layoutSymbolFactory.createCoreSymbol(layoutStaff, symbol);
                        //createLayout(symbol, layoutStaff);
                if (layoutSymbolInStaff != null) {
                    layoutStaff.add(layoutSymbolInStaff);
                    simultaneities.add(layoutSymbolInStaff);
                }
            }

            // create barlines
            // TODO: 21/9/17 Deberíamos poder crear barlines de system
            for (Measure measure: scoreSong.getMeasures()) {
                LayoutBarline barline = new LayoutBarline(layoutStaff, measure.getEndTime());
                layoutStaff.add(barline);
                simultaneities.add(barline);
            }

            layoutStaff.createNoteAccidentals();
            //layoutStaff.createBeaming();
            //System.out.println("Staff " + staff.getNumberIdentifier());
            //simultaneities.printDebug();
        }


        doHorizontalLayout(simultaneities);
    }

    private void doHorizontalLayout(Simultaneities simultaneities) throws IM3Exception {
        // Replace for a factory if required
        Pictogram noteHead = new Pictogram("_NHWC_", getLayoutFont(), NotePitch.NOTE_HEAD_WIDTH_CODEPOINT, // TODO: 22/9/17 Quizás esto debería ser cosa del FontLayout
                new Coordinate(new CoordinateComponent(0),
                new CoordinateComponent(0)
        ));
        double noteHeadWidth = noteHead.getWidth();
        ILayoutEngine layoutEngine = new BelliniLayoutEngine(2, 2, noteHeadWidth); // TODO: 22/9/17 ¿qué valor ponemos?
        layoutEngine.doHorizontalLayout(simultaneities);
    }

    @Override
    public Canvas[] getCanvases() {
        return new Canvas[] {canvas};
    }

    /*LayoutSymbolInStaff createLayout(ITimedElementInStaff symbol, LayoutStaff layoutStaff) throws IM3Exception {
        LayoutSymbolInStaff layoutSymbolInStaff = null;

        //TODO Revisar patrón de diseño - quitar switch
        if (symbol instanceof Clef) {
            layoutSymbolInStaff = createClef((Clef) symbol, layoutStaff);
        } else if (symbol instanceof KeySignature) {
            layoutSymbolInStaff = createKeySignature((Clef) symbol, layoutStaff);
        } else {
            System.err.println("TO-DO: Unsupported symbol type: " + symbol.getClass());
        }

        return layoutSymbolInStaff;
    }*/
}
