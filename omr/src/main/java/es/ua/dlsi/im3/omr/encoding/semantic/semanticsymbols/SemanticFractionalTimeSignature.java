package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

/**
 * @autor drizo
 */
public class SemanticFractionalTimeSignature extends SemanticTimeSignature<FractionalTimeSignature> {

    public SemanticFractionalTimeSignature(int num, int den) throws IM3Exception {
        super((FractionalTimeSignature) ImportFactories.processMeter(null, new Integer(num).toString(), new Integer(den).toString()));
    }

    public SemanticFractionalTimeSignature(FractionalTimeSignature coreSymbol) {
        super(coreSymbol.clone());
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder(SEMANTIC);
        sb.append(coreSymbol.getNumerator());
        sb.append('/');
        sb.append(coreSymbol.getDenominator());
        return sb.toString();
    }

    @Override
    public String toKernSemanticString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*M");
        sb.append(coreSymbol.getNumerator());
        sb.append('/');
        sb.append(coreSymbol.getDenominator());
        return sb.toString();
    }
}
