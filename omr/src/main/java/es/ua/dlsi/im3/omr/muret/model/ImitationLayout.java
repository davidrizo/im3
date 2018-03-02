package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.gui.javafx.GUIException;

import java.util.*;

/**
 * It creates the layout so that elements are positioned in the same location
 * they appear in the source image. Used in OMR
 */
public class ImitationLayout extends ScoreLayout {
    /**
     * It contains a canvas for each layout staff
     */
    HashMap<LayoutStaff, Canvas> canvases;

    public ImitationLayout(ScoreSong song, LayoutFonts font) throws IM3Exception {
        super(song, null, font); // FIXME: 29/11/17 el null
        canvases = new HashMap<>();
    }

    public ImitationLayout(ScoreSong song, HashMap<Staff, LayoutFonts> fonts) throws IM3Exception {
        super(song, null, fonts); // FIXME: 29/11/17
        canvases = new HashMap<>();
    }

    @Override
    public void layout(boolean proportionalSpacing) throws IM3Exception {

        //createStaffConnectors(); // FIXME: 29/11/17
        // add the connectors to the canvas
        // TODO: 12/10/17
        /*for (LayoutConnector connector: connectors) {
            canvas.add(connector.getGraphics());
        }

        for (LayoutBeamGroup beam: beams) {
            canvas.add(beam.getGraphicsElement());
        }*/
    }

    @Override
    public Collection<Canvas> getCanvases() {
        return canvases.values();
    }

    public LayoutStaff createLayoutStaff(Staff staff, double width, double height, LayoutFont layoutFont) throws IM3Exception {
        Canvas canvas = new Canvas(new CoordinateComponent(width), new CoordinateComponent(height));
        LayoutStaff layoutStaff = new LayoutStaff(this, new Coordinate(), new Coordinate(new CoordinateComponent(width), null), staff);
        canvases.put(layoutStaff, canvas);
        canvas.getElements().add(layoutStaff.getGraphics());
        super.addStaff(layoutStaff, layoutFont);
        return layoutStaff;
    }

    @Override
    protected void createConnectors() throws IM3Exception {
        super.createConnectors();
        System.err.println("TO-DO CONNECTORS IN IMITATION LAYOUT"); // TODO: 11/10/17 Connectors
    }

    public LayoutCoreSymbol createAndAddSymbol(ITimedElementInStaff symbol, LayoutStaff layoutStaff) throws IM3Exception {
        List<LayoutCoreSymbolInStaff> coreSymbolsInStaff = coreSymbolsInStaves.get(symbol.getStaff());
        if (coreSymbolsInStaff == null) {
            coreSymbolsInStaff = new ArrayList<>();
            coreSymbolsInStaves.put(symbol.getStaff(), coreSymbolsInStaff);
        }

        LayoutCoreSymbolInStaff layoutCoreSymbol = (LayoutCoreSymbolInStaff) layoutSymbolFactory.createCoreSymbol(getLayoutFont(symbol.getStaff()), symbol);
        layoutCoreSymbol.setX(20);
        coreSymbolsInStaff.add(layoutCoreSymbol);
        layoutStaff.add(layoutCoreSymbol);
        canvases.get(layoutStaff).add(layoutCoreSymbol.getGraphics());
        return layoutCoreSymbol;
    }

}
