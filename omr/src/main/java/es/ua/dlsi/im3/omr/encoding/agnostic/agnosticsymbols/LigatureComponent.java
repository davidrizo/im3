package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

//TODO
/**
 * @autor drizo
 */
public class LigatureComponent extends AgnosticSymbolType {
    private static final String LIGATURE = "ligature" + SEPSYMBOL;
    private NoteFigures figure;

    public LigatureComponent() {
    }

    @Override
    public void setSubtype(String string) {
        figure = NoteFigures.parseAgnosticString(string);
    }

    public LigatureComponent(NoteFigures figures) {
        this.figure = figures;
    }

    @Override
    public String toAgnosticString() {
        return LIGATURE + figure.toAgnosticString();
    }

}
