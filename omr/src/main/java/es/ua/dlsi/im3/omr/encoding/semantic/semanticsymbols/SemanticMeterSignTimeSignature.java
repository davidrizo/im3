package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.TimeSignature;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.meters.SignTimeSignature;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticConversionContext;

import java.util.List;

/**
 * @autor drizo
 */
public class SemanticMeterSignTimeSignature extends SemanticTimeSignature<SignTimeSignature> {
    public SemanticMeterSignTimeSignature(MeterSigns meterSign) throws IM3Exception {
        super((SignTimeSignature) ImportFactories.processMeter(meterSign.toSemanticString(), null, null));
    }

    public SemanticMeterSignTimeSignature(SignTimeSignature coreSymbol) {
        super(coreSymbol.clone());
    }

    @Override
    public String toSemanticString() {
        StringBuilder sb = new StringBuilder(SEMANTIC);
        String str = coreSymbol.getSignString();
        switch (str) {
            case "C":
                sb.append("Ct");
                break;
            case "C/":
                sb.append("Ccut");
                break;
            default:
                sb.append(str);
        }
        return sb.toString();
    }

    @Override
    public String toKernSemanticString()  {
        StringBuilder sb = new StringBuilder("*met(");
        sb.append(generateKernMeterSign());
        sb.append(')');
        return sb.toString();
    }

    private String generateKernMeterSign() {
        String str = coreSymbol.getSignString();
        switch (str) {
            case "C/":
                return "C|";
            default:
                return str;
        }
    }
}
