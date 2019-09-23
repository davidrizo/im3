package es.ua.dlsi.im3.omr.encoding.agnostic;

import es.ua.dlsi.im3.omr.encoding.Exporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.AgnosticSeparator;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.HorizontalSeparator;

/**
 * @author drizo
 */
public class AgnosticExporter extends Exporter<AgnosticToken> {
    private AgnosticEncoding encoding;
    AgnosticVersion agnosticVersion;

    public AgnosticExporter() {
        this(AgnosticVersion.v2);
    }
    public AgnosticExporter(AgnosticVersion agnosticVersion) {
        this.agnosticVersion = agnosticVersion;
        if (agnosticVersion == AgnosticVersion.v3_advance) {
            this.separateTokensWithSpace = true;
        }
    }

    @Override
    protected boolean requiresSeparator(AgnosticToken symbol) {
        return agnosticVersion != AgnosticVersion.v1 && agnosticVersion != AgnosticVersion.v3_advance &&
        (symbol instanceof HorizontalSeparator);
    }

    @Override
    protected String export(AgnosticToken symbol) {
        return symbol.getAgnosticString();
    }
}
