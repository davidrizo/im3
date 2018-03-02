package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

public class GraceNote extends AgnosticSymbolType {
    private static final String GRACENOTE = "gracenote" + SEPSYMBOL;

    INoteDurationSpecification durationSpecification;

    public GraceNote(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    public GraceNote() {

    }

    public INoteDurationSpecification getDurationSpecification() {
        return durationSpecification;
    }

    public void setDurationSpecification(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    @Override
    public String toAgnosticString() {
        return GRACENOTE + durationSpecification.toAgnosticString();
    }
}
