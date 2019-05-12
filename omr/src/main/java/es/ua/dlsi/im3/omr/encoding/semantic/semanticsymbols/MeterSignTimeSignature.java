package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreLayer;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticSymbolType;

/**
 * @autor drizo
 */
public class MeterSignTimeSignature extends TimeSignature {
    MeterSigns meterSign;

    public MeterSignTimeSignature(MeterSigns meterSign) {
        this.meterSign = meterSign;
    }

    @Override
    public String toSemanticString() {
        return SEMANTIC + meterSign.toAgnosticString();
    }

    @Override
    public String toKernSemanticString() throws IM3Exception {
        StringBuilder sb = new StringBuilder("*met(");
        sb.append(generateKernMeterSign());
        sb.append(')');
        return sb.toString();
    }

    private String generateKernMeterSign() throws IM3Exception {
        switch (meterSign) {
            case C: return "c";
            case Ccut: return "C|";
            default: throw new IM3Exception("Unsupported meter " + meterSign);
        }

    }
    @Override
    public SemanticSymbolType semantic2ScoreSong(ScoreLayer scoreLayer, SemanticSymbolType propagatedSymbolType) throws IM3Exception {
        es.ua.dlsi.im3.core.score.TimeSignature meter = ImportFactories.processMeter(meterSign.toSemanticString(), null, null);
        meter.setTime(scoreLayer.getDuration());
        scoreLayer.getStaff().addTimeSignature(meter);
        return propagatedSymbolType;
    }
}
