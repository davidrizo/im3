package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Clef;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

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
    public HorizontalLayout(ScoreSong song) {
        super(song);
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
                CoreSymbol coreSymbol = createLayout(symbol);
                layoutStaff.add(coreSymbol);
            }
            // TODO espaciado ....
        }
    }

    @Override
    public Canvas[] getCanvases() {
        return new Canvas[] {canvas};
    }

    CoreSymbol createLayout(ITimedElementInStaff symbol) throws IM3Exception {
        CoreSymbol coreSymbol = null;

        //TODO Revisar patrón de diseño - quitar switch
        if (symbol instanceof Clef) {
            coreSymbol = createClef((Clef) symbol);
        } else {
            System.err.println("TO-DO: Unsupported symbol type: " + symbol.getClass());
        }

        return coreSymbol;
    }

    private CoreSymbol createClef(Clef clef) {
        LayoutClef layoutClef = new LayoutClef(clef);

        return layoutClef;
    }
}
