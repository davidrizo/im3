package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutClef;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;
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
    public HorizontalLayout(ScoreSong song) {
        super(song);
        layoutSymbolFactory = new LayoutSymbolFactory();
        canvas = new Canvas(2000, 700); //TODO ¿qué valor ponemos? ¿Lo vamos cambiando conforme metamos cosas?
    }

    @Override
    public void layout() throws IM3Exception {
        //TODO scoreSong.getStaffSystems()
        staves = new ArrayList<>(); //TODO supongo que no habrá que rehacerlo siempre
        for (Staff staff: scoreSong.getStaves()) {
            LayoutStaff layoutStaff = new LayoutStaff(canvas.getWidth(), staff);
            staves.add(layoutStaff);
            canvas.add(layoutStaff.getGraphics());

            // add contents of staff
            List<ITimedElementInStaff> symbols = staff.getCoreSymbolsOrdered();
            for (ITimedElementInStaff symbol: symbols) {
                LayoutSymbolInStaff layoutSymbolInStaff = layoutSymbolFactory.createCoreSymbol(layoutStaff, symbol);
                        //createLayout(symbol, layoutStaff);
                if (layoutSymbolInStaff != null) {
                    layoutStaff.add(layoutSymbolInStaff);
                    layoutSymbolInStaff.setX(symbol.getTime().getComputedTime()); // TODO algoritmo espaciado x de LayoutEngine .... - ahora cojo el tiempo

                    //TODO Ver si esto es mejor aquí o después
                    layoutSymbolInStaff.computeLayout();
                }
            }
        }
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
