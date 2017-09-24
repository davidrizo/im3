package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

public class PageLayout extends ScoreLayout {
    /**
     * Each canvas corresponds to a page
     */
    Canvas [] canvases;

    public PageLayout(ScoreSong song, LayoutFonts font) throws IM3Exception {
        super(song, font);

        // TODO: 24/9/17 Ahora sólo lo pongo todo en una página y sólo hago caso de los system break. No hago ningún algoritmo de line breaking

    }


    @Override
    public void layout() throws IM3Exception {

    }

    @Override
    public Canvas[] getCanvases() {
        return canvases;
    }
}
