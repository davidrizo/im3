package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.Exporter;

/**
 * @author drizo
 */
public class KernSemanticExporter extends Exporter<SemanticSymbol> {

    public KernSemanticExporter() {
        super('\n');
        this.setEndWithSeparator(true);
    }

    @Override
    protected boolean requiresSeparator(SemanticSymbol lastSymbol) {
        return true;
    }

    @Override
    protected String export(SemanticSymbol symbol) throws IM3Exception {
        return symbol.toKernSemanticString();
    }
}