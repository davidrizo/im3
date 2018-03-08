package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Digit extends AgnosticSymbolType {
    private static final String DIGIT = "digit" + SEPSYMBOL;
    int digit;

    public Digit(int digit) throws IM3Exception {
        if (digit >= 10 || digit < 0) {
            throw new IM3Exception("Invalid digit: " + digit);
        }
        this.digit = digit;
    }

    public Digit() {

    }

    public int getDigit() {
        return digit;
    }

    public void setDigit(int digit) {
        this.digit = digit;
    }

    @Override
    public String toAgnosticString() {
        return DIGIT + digit;
    }
}
