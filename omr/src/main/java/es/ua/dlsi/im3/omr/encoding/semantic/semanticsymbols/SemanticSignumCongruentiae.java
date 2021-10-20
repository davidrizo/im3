package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.SignumCongruentiaeMark;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class SemanticSignumCongruentiae extends SemanticSymbolType<SignumCongruentiaeMark> {
    private static final String SEMANTIC = "signumc";

    public SemanticSignumCongruentiae(SignumCongruentiaeMark signumCongruentiaeMark) {
        super(signumCongruentiaeMark);
    }
    public SemanticSignumCongruentiae() {
        super(new SignumCongruentiaeMark(null, null));
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC;
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        return "*sc";
    }
}
