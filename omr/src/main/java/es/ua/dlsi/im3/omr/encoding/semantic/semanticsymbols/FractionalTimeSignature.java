package es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols;

import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

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
}
