package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.omr.encoding.Exporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.AgnosticSeparator;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.HorizontalSeparator;

/**
 * @author drizo
 */
public class AgnosticExporter extends Exporter<AgnosticToken> {
    private AgnosticEncoding encoding;

    @Override
    protected boolean requiresSeparator(AgnosticToken symbol) {
        return (symbol instanceof HorizontalSeparator);
    }

    @Override
    protected String export(AgnosticToken symbol) {
        return symbol.getAgnosticString();
    }
}