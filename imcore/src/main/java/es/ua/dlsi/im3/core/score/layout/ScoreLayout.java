package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

/**
 * It contains staves that can be split in several. Symbols like
 * slurs may also be splitted into two parts
 * @author drizo
 *
 */
public abstract class ScoreLayout {
    protected final ScoreSong scoreSong;

    public ScoreLayout(ScoreSong song) { //TODO ¿y si tenemos que sacar sólo unos pentagramas?
        this.scoreSong = song;
    }
    public abstract void layout() throws IM3Exception;
    public abstract Canvas[] getCanvases();
}
