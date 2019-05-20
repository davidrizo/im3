package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.Sequence;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.AgnosticSeparator;

import java.util.LinkedList;
import java.util.List;

/**
 * @author drizo
 */
public class AgnosticEncoding extends Sequence<AgnosticToken> {
    public AgnosticEncoding() {
    }

    public AgnosticEncoding(AgnosticVersion agnosticVersion, String [] agnosticSequence) throws IM3Exception {
        for (String s: agnosticSequence) {
            add(AgnosticSymbol.parseAgnosticString(agnosticVersion, s));
        }
    }

    /**
     * It removes the last symbol if it is a separator
     */
    public void removeLastSymbolIfSeparator() {
        if (symbols.get(symbols.size()-1) instanceof AgnosticSeparator) {
            symbols.remove(symbols.size()-1);
        }
    }

    // it returns all symbols without agnostic separators
    public List<AgnosticToken> getSymbolsWithoutSeparators() {
        List<AgnosticToken> result = new LinkedList<>();
        for (AgnosticToken token: symbols) {
            if (!(token instanceof AgnosticSeparator)) {
                result.add(token);
            }
        }
        return result;
    }


}
