package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

public class Note extends AgnosticSymbolType {
    private static final String NOTE = "note" + SEPSYMBOL;

    INoteDurationSpecification durationSpecification;

    public Note(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    public Note() {

    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
        durationSpecification = NoteDurationSpecificationFactory.parseString(string);
    }

    public INoteDurationSpecification getDurationSpecification() {
        return durationSpecification;
    }

    public void setDurationSpecification(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    @Override
    public String toAgnosticString() {
        return NOTE + durationSpecification.toAgnosticString();
    }
}
