package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
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

    @Override
    public void setSubtype(String string) {
        restFigures = RestFigures.parseAgnosticString(string);
    }

    public RestFigures getRestFigures() {
        return restFigures;
    }

    public void setRestFigures(RestFigures restFigures) {
        this.restFigures = restFigures;
    }

    @Override
    public String toAgnosticString() {
        if (restFigures != null) {
            return REST + restFigures.toAgnosticString();
        } else {
            return REST;
        }
    }
}
