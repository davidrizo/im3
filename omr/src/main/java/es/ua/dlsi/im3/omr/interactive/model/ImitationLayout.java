package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * It creates the layout so that elements are positioned in the same location
 * they appear in the source image
 */
public class ImitationLayout extends ScoreLayout {
    //TODO ¿Cuántos canvases?
    Canvas canvas;
    List<LayoutStaff> layoutStaves;

    public ImitationLayout(ScoreSong song, LayoutFonts font, CoordinateComponent width, CoordinateComponent height) throws IM3Exception {
        super(song, font);
        layoutStaves = new ArrayList<>();
        canvas = new Canvas(width, height);
    }

    public ImitationLayout(ScoreSong song, HashMap<Staff, LayoutFonts> fonts, CoordinateComponent width, CoordinateComponent height) throws IM3Exception {
        super(song, fonts);
        layoutStaves = new ArrayList<>();
        canvas = new Canvas(width, height);
    }

    @Override
    public void layout() throws IM3Exception {

        createStaffConnectors();
        // add the connectors to the canvas
        for (LayoutConnector connector: connectors) {
            canvas.add(connector.getGraphics());
        }

        for (LayoutBeamGroup beam: beams) {
            canvas.add(beam.getGraphicsElement());
        }
    }

    @Override
    public List<Canvas> getCanvases() {
        return Arrays.asList(canvas);
    }

    public LayoutStaff addStaff(Staff staff, Coordinate leftTop, Coordinate rightTop) {
        LayoutStaff layoutStaff = new LayoutStaff(this, leftTop, rightTop, staff);
        layoutStaves.add(layoutStaff);
        return layoutStaff;
    }

    @Override
    protected void createConnectors() throws IM3Exception {
        super.createConnectors();
        System.err.println("TO-DO CONNECTORS IN IMITATION LAYOUT"); // TODO: 11/10/17 Connectors

    }

}
