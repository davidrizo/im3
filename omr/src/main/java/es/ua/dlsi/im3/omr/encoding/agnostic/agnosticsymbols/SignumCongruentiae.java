package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class SignumCongruentiae extends AgnosticSymbolType {
    private static final String DS = "signumCongruentiae";

    public SignumCongruentiae() {
    }

    @Override
    public void setSubtype(String string) {

    }

    @Override
    public String toAgnosticString() {
        return DS;
    }

}
