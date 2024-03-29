package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.omr.encoding.Exporter;

/**
 * @author drizo
 */
public class SemanticExporter extends Exporter<SemanticSymbol> {

    @Override
    protected boolean requiresSeparator(SemanticSymbol lastSymbol) {
        return true;
    }

    @Override
    protected String export(SemanticSymbol symbol) {
        return symbol.toSemanticString();
    }
}
