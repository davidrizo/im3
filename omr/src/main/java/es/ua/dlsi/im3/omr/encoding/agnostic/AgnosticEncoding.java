package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.omr.encoding.Sequence;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.AgnosticSeparator;

/**
 * @author drizo
 */
public class AgnosticEncoding extends Sequence<AgnosticToken> {
    /**
     * It removes the last symbol if it is a separator
     */
    public void removeLastSymbolIfSeparator() {
        if (symbols.get(symbols.size()-1) instanceof AgnosticSeparator) {
            symbols.remove(symbols.size()-1);
        }
    }
}
