package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreBarline;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaffSystem;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.layout.layoutengines.BelliniLayoutEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * All systems are arranged in a single line
 */
public class HorizontalLayout extends ScoreLayout {
    LayoutStaffSystem system;

    /**
     * Everything is arranged in a single canvas
     */
    Canvas canvas;
    public HorizontalLayout(ScoreSong song, LayoutFonts font, CoordinateComponent width, CoordinateComponent height) throws IM3Exception {
        super(song, font);
        canvas = new Canvas(width, height);
    }

    @Override
    public void layout() throws IM3Exception {
        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        canvas.clear();

        //scoreSong.getStaffGroups();
        //TODO scoreSong.getStaffGroups()
        system = new LayoutStaffSystem();

        double nextY = LayoutConstants.TOP_MARGIN;
        for (Staff staff: scoreSong.getStaves()) {
            CoordinateComponent y = new CoordinateComponent(nextY);
            nextY += LayoutConstants.STAFF_SEPARATION;
            Coordinate leftTop = new Coordinate(null, y);
            Coordinate rightTop = new Coordinate(canvas.getWidthCoordinateComponent(), y);

            LayoutStaff layoutStaff = new LayoutStaff(this, leftTop, rightTop, staff);
            system.addLayoutStaff(layoutStaff);
            canvas.add(layoutStaff.getGraphics());// TODO: 25/9/17 ¿Realmente hace falta el canvas? 

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
            barline.setLayoutStaff(system.getBottomStaff(), system.getTopStaff());
            canvas.getElements().add(barline.getGraphics());
        }
        doHorizontalLayout(simultaneities);


    }

    @Override
    public List<Canvas> getCanvases() {
        return Arrays.asList(canvas);
    }
}
