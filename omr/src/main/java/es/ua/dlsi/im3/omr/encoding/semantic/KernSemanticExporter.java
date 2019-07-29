package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.Exporter;

import java.util.Arrays;

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
        if (symbol.getSymbol().getAgnosticIDs() != null) {
            StringBuilder stringBuilder = new StringBuilder(symbol.toKernSemanticString());
            stringBuilder.append('@');
            for (int i=0; i<symbol.getSymbol().getAgnosticIDs().length; i++) {
                if (i>0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(symbol.getSymbol().getAgnosticIDs()[i]);
            }
            return stringBuilder.toString();
        } else {
            return symbol.toKernSemanticString();
        }
    }
}
