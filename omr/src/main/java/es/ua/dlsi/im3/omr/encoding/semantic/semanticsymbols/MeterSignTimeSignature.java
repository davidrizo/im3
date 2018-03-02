package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

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
}
