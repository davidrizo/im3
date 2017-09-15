package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

public class PageLayout extends ScoreLayout {
    /**
     * Each canvas corresponds to a page
     */
    Canvas [] canvases;

    public PageLayout(ScoreSong song) {
        super(song);
    }

    @Override
    public void layout() throws IM3Exception {

    }

    @Override
    public Canvas[] getCanvases() {
        return canvases;
    }
}
