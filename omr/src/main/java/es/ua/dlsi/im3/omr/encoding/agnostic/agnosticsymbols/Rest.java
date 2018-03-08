package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

/**
 * @autor drizo
 */
public class Rest extends AgnosticSymbolType {
    private static final String REST = "rest" + SEPSYMBOL;

    RestFigures restFigures;

    public Rest(RestFigures restFigures) {
        this.restFigures = restFigures;
    }

    public Rest() {

    }

    public RestFigures getRestFigures() {
        return restFigures;
    }

    public void setRestFigures(RestFigures restFigures) {
        this.restFigures = restFigures;
    }

    @Override
    public String toAgnosticString() {
        return REST + restFigures.toAgnosticString();
    }
}
