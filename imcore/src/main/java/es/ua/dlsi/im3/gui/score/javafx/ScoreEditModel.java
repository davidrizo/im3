package es.ua.dlsi.im3.gui.score.javafx;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;

import java.util.HashMap;

/**
 * @autor drizo
 */
public class ScoreEditModel {
    private final HorizontalLayout layout;
    ScoreSong scoreSong;
    HashMap<Staff, LayoutFonts> fontHashMap;

    // TODO: 12/3/18 No tiene sentido tener width y height como double, deberían ser properties
    public ScoreEditModel(ScoreSong scoreSong, double width, double height) throws IM3Exception {
        this.scoreSong = scoreSong;
        fontHashMap = FontFactory.getInstance().getFontsForScoreSong(scoreSong);

        layout = new HorizontalLayout(scoreSong, fontHashMap,
                new CoordinateComponent(width),
                new CoordinateComponent(height));
    }

    public HorizontalLayout getLayout() {
        return layout;
    }

    public ScoreSong getScoreSong() {
        return scoreSong;
    }
}
