package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

public class Appoggiatura extends GraceNote {
    private static final String APPOGGIATURA = "appoggiatura" + SEPSYMBOL;

    public Appoggiatura(INoteDurationSpecification durationSpecification) {
        super(durationSpecification);
    }

    public Appoggiatura(INoteDurationSpecification durationSpecification, Directions stemDirection) {
        super(durationSpecification, stemDirection);
    }

    public Appoggiatura() {
    }

    @Override
    protected String getAgnosticCode() {
        return APPOGGIATURA;
    }

}
