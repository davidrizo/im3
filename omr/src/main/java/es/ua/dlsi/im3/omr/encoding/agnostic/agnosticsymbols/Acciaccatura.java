package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

public class Acciaccatura extends GraceNote {
    private static final String APPOGGIATURA = "gracenote" + SEPSYMBOL;

    public Acciaccatura(INoteDurationSpecification durationSpecification) {
        super(durationSpecification);
    }

    public Acciaccatura(INoteDurationSpecification durationSpecification, Directions stemDirection) {
        super(durationSpecification, stemDirection);
    }

    public Acciaccatura() {
    }

    @Override
    protected String getAgnosticCode() {
        return APPOGGIATURA;
    }

}
