package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

import java.util.*;

/**
 * It shows the element exactly as the manuscript
 * @autor drizo
 */
public class DiplomaticLayout extends PageLayout {
    ArrayList<Canvas> canvases;
    private LayoutStaffSystem system;

    public DiplomaticLayout(ScoreSong song, Collection<Staff> staves) throws IM3Exception {
        super(song, staves);
        init();
    }

    private void init() {
        canvases = new ArrayList<>(); //TODO
        canvases.add(new Canvas(new CoordinateComponent(500), new CoordinateComponent(200)));
    }

    @Override
    protected boolean skipSymbol(ITimedElementInStaff symbol) {
        return false;
    }

    @Override
    public void layout(boolean computeProportionalSpacing) throws IM3Exception {
//TODO Copiado de Horizontal layout
        Canvas canvas = canvases.get(0);


        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        canvas.clear();

        //scoreSong.getStaffGroups();
        //TODO scoreSong.getStaffGroups()
        system = new LayoutStaffSystem();

        double nextY = topMargin;
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
    public Collection<Canvas> getCanvases() {
        return canvases;
    }

    @Override
    public void replace(Clef clef, Clef newClef, boolean changePitches) {

    }
}
