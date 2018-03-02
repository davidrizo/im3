package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

/**
 * @autor drizo
 */
public class MeterSign extends AgnosticSymbolType {
    private static final String MS = "metersign" + SEPSYMBOL;

    MeterSigns meterSigns;

    public MeterSign(MeterSigns meterSigns) {
        this.meterSigns = meterSigns;
    }

    /**
     * For using it in automata
     */
    public MeterSign() {

    }

    public MeterSigns getMeterSigns() {
        return meterSigns;
    }

    public void setMeterSigns(MeterSigns meterSigns) {
        this.meterSigns = meterSigns;
    }


    @Override
    public String toAgnosticString() {
        return MS + meterSigns.toAgnosticString();
    }
}
