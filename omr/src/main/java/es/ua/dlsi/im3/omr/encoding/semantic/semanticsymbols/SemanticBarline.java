package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

import java.util.List;

/**
 * @autor drizo
 */
public class SemanticBarline extends SemanticSymbolType<MarkBarline> {
    private static final String SEMANTIC = "barline";

    public SemanticBarline() {
        super(new MarkBarline());
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC;
    }

    @Override
    public String toKernSemanticString() {
        System.err.println("=-");
        return "=-";
    }
}
