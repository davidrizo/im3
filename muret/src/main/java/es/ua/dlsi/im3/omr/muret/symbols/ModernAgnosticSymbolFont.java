package es.ua.dlsi.im3.omr.muret.symbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class ModernAgnosticSymbolFont extends AgnosticSymbolFont {
    public ModernAgnosticSymbolFont() throws IM3Exception {
        super(new BravuraFont());
    }
}
