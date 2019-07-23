package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class SemanticCustos extends SemanticSymbolType<Custos> {
    private static final String SEMANTIC_CUSTOS = "custos" + SEPSYMBOL;

    public SemanticCustos(Custos coreSymbol) {
        super(coreSymbol);
    }


    @Override
    public String toSemanticString()  {
        return SEMANTIC_CUSTOS;
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        StringBuilder stringBuilder = new StringBuilder("*custos");
        stringBuilder.append(KernExporter.generatePitch(this.coreSymbol.getScientificPitch()));
        return stringBuilder.toString();
    }
}
