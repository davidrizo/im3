package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreBarline;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolInStaff;
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

    /**
     * Everything is arranged in a single canvas
     */
    Canvas canvas;
    public HorizontalLayout(ScoreSong song, LayoutFonts font, Coordinate leftTop, Coordinate bottomRight) throws IM3Exception {
        super(song, font);
        canvas = new Canvas(leftTop, bottomRight);
    }

    @Override
    public void layout() throws IM3Exception {
        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        canvas.clear();

        //TODO scoreSong.getStaffSystems()
        staves = new ArrayList<>(); //TODO supongo que no habrá que rehacerlo siempre

        double nextY = LayoutConstants.TOP_MARGIN;
        for (Staff staff: scoreSong.getStaves()) {
            CoordinateComponent y = new CoordinateComponent(canvas.getLeftTop().getY(), nextY);
            nextY += LayoutConstants.STAFF_SEPARATION;
            Coordinate leftTop = new Coordinate(canvas.getLeftTop().getX(), y);
            Coordinate rightTop = new Coordinate(canvas.getBottomRight().getX(), y);

            LayoutStaff layoutStaff = new LayoutStaff(this, leftTop, rightTop, staff);
            staves.add(layoutStaff);
            canvas.add(layoutStaff.getGraphics());

            // add contents of layout staff, we have just one
            List<LayoutCoreSymbolInStaff> layoutSymbolsInStaff = coreSymbols.get(staff);
            if (layoutSymbolsInStaff == null) {
                throw new IM3RuntimeException("There should not be null the layoutSymbolsInStaff, it is initialized in ScoreLayout");
            }

            for (LayoutCoreSymbolInStaff coreSymbol: layoutSymbolsInStaff) {
                coreSymbol.setLayoutStaff(layoutStaff);
                layoutStaff.add(coreSymbol);
            }

            layoutStaff.createNoteAccidentals();
            //layoutStaff.createBeaming();
            //System.out.println("Staff " + staff.getNumberIdentifier());
            //simultaneities.printDebug();
        }
        for (LayoutCoreBarline barline: barlines) {
            //TODO IMPORTANT it is the same system now - it should be drawn for the different groups (piano....)
            barline.setLayoutStaff(staves.get(0), staves.get(staves.size()-1));
            canvas.getElements().add(barline.getGraphics());
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
}
