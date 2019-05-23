package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.Multirest;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;

/**
 * @autor drizo
 */
public class SemanticMultirest extends SemanticAtom<SimpleMultiMeasureRest> {
    private static final String SEMANTIC = "multirest" + SEPSYMBOL;

    public SemanticMultirest(Time timeMeasureDuration, int restBars) {
        super(new SimpleMultiMeasureRest(timeMeasureDuration, restBars));
    }

    public SemanticMultirest(SimpleMultiMeasureRest coreSymbol) {
        super(coreSymbol.clone());
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC + coreSymbol.getNumMeasures();
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        return KernExporter.encodeAtom(coreSymbol);
    }
}
