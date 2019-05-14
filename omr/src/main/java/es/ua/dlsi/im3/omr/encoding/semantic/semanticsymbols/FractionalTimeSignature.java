package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

import java.util.List;

/**
 * @autor drizo
 */
public class FractionalTimeSignature extends TimeSignature {
    int num;
    int den;

    public FractionalTimeSignature(int num, int den) {
        this.num = num;
        this.den = den;
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder(SEMANTIC);
        sb.append(num);
        sb.append('/');
        sb.append(den);
        return sb.toString();
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("*M");
        sb.append(num);
        sb.append('/');
        sb.append(den);
        return sb.toString();
    }

    @Override
    public void semantic2ScoreSong(SemanticConversionContext semanticConversionContext, List<ITimedElementInStaff> conversionResult) throws IM3Exception {
        es.ua.dlsi.im3.core.score.TimeSignature meter =
                ImportFactories.processMeter(null, new Integer(num).toString(), new Integer(den).toString());
        semanticConversionContext.setCurrentTimeSignature(meter);
        conversionResult.add(meter);
    }
}
