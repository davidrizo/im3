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
import java.util.logging.Level;
import java.util.logging.Logger;

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
    protected boolean skipSymbol(ITimedElementInStaff symbol) {
        return symbol instanceof SystemBreak || symbol instanceof PageBreak;
    }

    @Override
    protected void createConnectors() throws IM3Exception {
        super.createConnectors();
    }

    @Override
    public void layout(boolean computeProportionalSpacing) throws IM3Exception {
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
                coreSymbol.layout();

                if (!(coreSymbol instanceof LayoutCoreCustos)) { //TODO ¿por qué?
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

        doHorizontalLayout(simultaneities, computeProportionalSpacing);

        createConnectors();
        createBeams();

        // add the connectors to the canvas
        for (LayoutConnector connector: connectors) {
            canvas.add(connector.getGraphics());
        }

        if (beams != null) {
            for (LayoutBeamGroup beam : beams) {
                canvas.add(beam.getGraphics());
            }
        }

    }

    @Override
    public List<Canvas> getCanvases() {
        return Arrays.asList(canvas);
    }

    public LayoutStaffSystem getSystem() {
        return system;
    }


    @Override
    public void replace(Clef clef, Clef newClef, boolean changePitches) throws IM3Exception {
        // TODO: 14/3/18 Generalizar este replace para todos los símbolos y layouts
        LayoutCoreClef layoutClef = (LayoutCoreClef) this.coreSymbolViews.get(clef);
        if (layoutClef  == null) {
            throw new IM3Exception("Cannot find a notation symbol for clef to be replaced: " + clef);
        }

        clef.getStaff().replaceClef(clef, newClef, changePitches);
        layoutClef.setCoreSymbol(newClef);
        if (!clef.getNote().equals(newClef.getNote())) {
            layoutClef.rebuild(); // TODO: 26/3/18 ¿Mejor en el setCoreSymbol? 
        }
        layoutClef.layout();

        coreSymbolViews.remove(clef);
        coreSymbolViews.put(newClef, layoutClef);

        // set dirty of all affected notes to change their vertical position
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Changing note head positions for change in clef");

        // we change all elements in the staff because it is faster than filtering it - e.g. key signature must be changed
        List<LayoutCoreSymbolInStaff> list = this.coreSymbolsInStaves.get(clef.getStaff());
        if (list != null) {
            for (LayoutCoreSymbolInStaff layoutCoreSymbolInStaff: list) {
                layoutCoreSymbolInStaff.layout();
            }
        }

        /*System.err.println("TO-DO cambiar pitches o posición en replace Clef"); // TODO: 14/3/18 TO-DO cambiar pitches o posición en replace Clef

        LayoutCoreSymbolInStaff newLayoutClef = (LayoutCoreSymbolInStaff) createLayoutCoreSymbol(newClef);

        List<LayoutCoreSymbolInStaff> coreSymbolsInStaffList = coreSymbolsInStaves.get(clef.getStaff());
        CollectionsUtils.replace(coreSymbolsInStaffList, oldLayoutClef, newLayoutClef);
        coreSymbolViews.replace(clef, oldLayoutClef, newLayoutClef);
        //this.simultaneities.replace(oldLayoutClef, newLayoutClef); //TODO Recalcular posiciones - si es necesario borrar o insertar nuevos símbolos
        System.err.println("TO-DO Notificar score view para que cambie - también simultaneities");*/
    }
}
