package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * Usually a blot ink
 * @autor drizo
 */
public class Smudge extends AgnosticSymbolType {
    private static final String SMUDGE = "smudge";

    public Smudge() {
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {

    }

    @Override
    public String toAgnosticString() {
        return SMUDGE;
    }

}
