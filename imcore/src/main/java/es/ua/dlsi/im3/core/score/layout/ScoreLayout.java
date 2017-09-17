package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;

/**
 * It contains staves that can be split in several. Symbols like
 * slurs may also be splitted into two parts
 * @author drizo
 *
 */
public abstract class ScoreLayout {
    protected final ScoreSong scoreSong;
    protected final LayoutFont layoutFont;

    public ScoreLayout(ScoreSong song, LayoutFonts font) { //TODO ¿y si tenemos que sacar sólo unos pentagramas?
        this.scoreSong = song;
        layoutFont = FontFactory.getInstance().getFont(font);
    }
    public abstract void layout() throws IM3Exception;
    public abstract Canvas[] getCanvases();

    public LayoutFont getLayoutFont() {
        return layoutFont;
    }
}
