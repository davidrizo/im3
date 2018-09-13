package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.DiplomaticLayout;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.fonts.PatriarcaFont;

public class OMRScore {
    /**
     * Original song
     */
    ScoreSong diplomaticEdition;
    /**
     * Original song
     */
    ScoreSong criticalEdition;
    /**
     * Default notation type used for the staff creation
     */
    NotationType notationType;
    /**
     * Modern translation of the original song, just used in mensural notation
     */
    ScoreSong modernTranslatedEdition;
    /**
     * Default layout font for manuscript
     */
    LayoutFont manuscriptLayoutFont;
    DiplomaticLayout diplomaticLayout;

    public OMRScore() {
    }

    public ScoreSong getDiplomaticEdition() {
        return diplomaticEdition;
    }

    public void setDiplomaticEdition(ScoreSong diplomaticEdition) {
        this.diplomaticEdition = diplomaticEdition;
    }

    public NotationType getNotationType() {
        return notationType;
    }

    public void setNotationType(NotationType notationType) throws IM3Exception {
        this.notationType = notationType;
        if (notationType == NotationType.eMensural) {
            manuscriptLayoutFont = new PatriarcaFont();
        } else if (notationType == NotationType.eModern) {
            manuscriptLayoutFont = new BravuraFont();
        } else {
            throw new IM3Exception("Unuspported notation type '" + notationType + "'");
        }

    }
}