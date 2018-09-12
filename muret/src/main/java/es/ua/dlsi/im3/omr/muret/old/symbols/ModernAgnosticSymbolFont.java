package es.ua.dlsi.im3.omr.muret.old.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;

/**
 * @autor drizo
 */
public class ModernAgnosticSymbolFont extends AgnosticSymbolFont {
    public ModernAgnosticSymbolFont() throws IM3Exception {
        super(new BravuraFont());
    }
}
