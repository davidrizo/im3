package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @autor drizo
 */
public abstract class PageLayout extends ScoreLayout {
    protected ArrayList<Page> pages;

    public PageLayout(ScoreSong song, Collection<Staff> staves) throws IM3Exception {
        super(song, staves);
    }

    public ArrayList<Page> getPages() {
        return pages;
    }
}
