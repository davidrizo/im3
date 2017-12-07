package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        super(song, song.getStaves(), font);
        canvas = new Canvas(width, height);
    }

    public HorizontalLayout(ScoreSong song, HashMap<Staff, LayoutFonts> fonts, CoordinateComponent width, CoordinateComponent height) throws IM3Exception {
        super(song, song.getStaves(), fonts);
        canvas = new Canvas(width, height);
    }

    @Override
    protected void createConnectors() throws IM3Exception {
        super.createConnectors();
    }

    @Override
    public void layout() throws IM3Exception {
        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        canvas.clear();

        //scoreSong.getStaffGroups();
        //TODO scoreSong.getStaffGroups()
        system = new LayoutStaffSystem();

        double nextY = LayoutConstants.TOP_MARGIN;
        for (Staff staff: staves) {
            CoordinateComponent y = new CoordinateComponent(nextY);
            nextY += LayoutConstants.STAFF_SEPARATION;
            Coordinate leftTop = new Coordinate(null, y);
            Coordinate rightTop = new Coordinate(canvas.getWidthCoordinateComponent(), y);

            LayoutStaff layoutStaff = new LayoutStaff(this, leftTop, rightTop, staff);
            layoutStaves.put(staff, layoutStaff);
            system.addLayoutStaff(layoutStaff);
            canvas.add(layoutStaff.getGraphics());// TODO: 25/9/17 ¿Realmente hace falta el canvas? 

            // add contents of layout staff, we have just one
            List<LayoutCoreSymbolInStaff> layoutSymbolsInStaff = coreSymbolsInStaves.get(staff);
            if (layoutSymbolsInStaff == null) {
                throw new IM3RuntimeException("There should not be null the layoutSymbolsInStaff, it is initialized in ScoreLayout");
            }

            for (LayoutCoreSymbolInStaff coreSymbol: layoutSymbolsInStaff) {
                coreSymbol.setLayoutStaff(layoutStaff);
                if (!(coreSymbol instanceof LayoutCoreCustos)) {
                    layoutStaff.add(coreSymbol);
                } // ommitted in this layout
            }

            layoutStaff.createNoteAccidentals();
            //layoutStaff.createBeaming();
            //System.out.println("Staff " + staff.getNumberIdentifier());
            //simultaneities.printDebug();
        }

        for (Map.Entry<Staff, List<LayoutCoreBarline>> entry: barlines.entrySet()) {
            Staff staff = entry.getKey();
            for (LayoutCoreBarline barline: entry.getValue()) {
                LayoutStaff layoutStaff = system.get(staff);
                barline.setLayoutStaff(layoutStaff, layoutStaff);
                canvas.getElements().add(barline.getGraphics());
                system.addLayoutCoreBarline(barline);
            }
            //barline.setLayoutStaff(system.getBottomStaff(), system.getTopStaff());
        }


        system.createStaffConnectors(connectors); //TODO ¿Qué pasa si un conector salta de página?

        doHorizontalLayout(simultaneities);

        createConnectors();
        createBeams();

        // add the connectors to the canvas
        for (LayoutConnector connector: connectors) {
            canvas.add(connector.getGraphics());
        }

        if (beams != null) {
            for (LayoutBeamGroup beam : beams) {
                canvas.add(beam.getGraphicsElement());
            }
        }

    }

    @Override
    public List<Canvas> getCanvases() {
        return Arrays.asList(canvas);
    }
}
