package es.ua.dlsi.im3.omr.muret.old.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.NotationType;

/**
 * @autor drizo
 */
public class AgnosticSymbolFonts {
    MensuralAgnosticSymbolFont mensuralAgnosticSymbolFont;
    ModernAgnosticSymbolFont modernAgnosticSymbolFont;

    public AgnosticSymbolFonts() throws IM3Exception {
        mensuralAgnosticSymbolFont = new MensuralAgnosticSymbolFont();
        modernAgnosticSymbolFont = new ModernAgnosticSymbolFont();
    }

    public AgnosticSymbolFont getAgnosticSymbolFont(NotationType notationType) {
        if (notationType == NotationType.eMensural) {
            return mensuralAgnosticSymbolFont;
        } else if (notationType == NotationType.eModern) {
            return modernAgnosticSymbolFont;
        } else {
            throw new IM3RuntimeException("No agnostic symbol font for: " + notationType);
        }
    }
}
