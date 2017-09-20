package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

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
    public HorizontalLayout(ScoreSong song, LayoutFonts font) {
        super(song, font);
        layoutSymbolFactory = new LayoutSymbolFactory();
        canvas = new Canvas(2000, 700); //TODO ¿qué valor ponemos? ¿Lo vamos cambiando conforme metamos cosas?
    }

    @Override
    public void layout() throws IM3Exception {
        // TODO: 19/9/17 En esta versión creamos todos los símbolos cada vez - habría que crear sólo los necesarios
        canvas.clear();

        //TODO scoreSong.getStaffSystems()
        staves = new ArrayList<>(); //TODO supongo que no habrá que rehacerlo siempre

        // first create symbols and simultaneities
        Simultaneities simultaneities = new Simultaneities();

        for (Staff staff: scoreSong.getStaves()) {
            LayoutStaff layoutStaff = new LayoutStaff(this, canvas.getWidth(), staff);
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
        }

        // TODO: 19/9/17 This is a very simple algorithm, that does not permit overlaps without collisions. It must be replaced by a serious one
        /**
         * Set the time between simultaneities and x position
         */
        double x = 0;
        Simultaneity prevSimultaneity = null;
        for (Simultaneity simultaneity: simultaneities.getSimiltaneities()) {
            if (prevSimultaneity != null) {
                prevSimultaneity.setTimeSpan(simultaneity.getTime().substract(prevSimultaneity.getTime()));
                computeWidth(prevSimultaneity);
                prevSimultaneity.setElementsX(x);
                prevSimultaneity.computeElementsLayout();
                x += prevSimultaneity.getLayoutWidth();
            }

            prevSimultaneity = simultaneity;
        }

        if (prevSimultaneity != null) {
            prevSimultaneity.setTimeSpanFromElementsDuration();
            computeWidth(prevSimultaneity);
            prevSimultaneity.setElementsX(x);
            prevSimultaneity.computeElementsLayout();
        }


        //layoutSymbolInStaff.computeLayout();

    }

    private void computeWidth(Simultaneity prevSimultaneity) {
        double timeToWidth = Math.log(prevSimultaneity.getTimeSpan().getComputedTime())*LayoutConstants.EM;
        double layoutWidth = Math.max(prevSimultaneity.getMinimumWidth(), timeToWidth);
        prevSimultaneity.setLayoutWidth(layoutWidth);
        /*System.out.println("Width from time span " + prevSimultaneity.getTimeSpan().getComputedTime() + ", min " +
                prevSimultaneity.getMinimumWidth() + " and timeToWidth  " +
                timeToWidth + " = "  + layoutWidth);*/
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
