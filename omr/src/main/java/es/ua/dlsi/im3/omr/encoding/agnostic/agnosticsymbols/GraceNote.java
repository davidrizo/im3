package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;

public class GraceNote extends AgnosticSymbolType {
    private static final String GRACENOTE = "gracenote" + SEPSYMBOL;

    INoteDurationSpecification durationSpecification;

    /**
     * v2
     */
    Directions stemDirection;

    public GraceNote(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    public GraceNote(INoteDurationSpecification durationSpecification, Directions stemDirection) {
        this.durationSpecification = durationSpecification;
        this.stemDirection = stemDirection;
    }

    public GraceNote() {

    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
        //TODO Código repetido en Note
        String [] tokens = string.split(SEPPROPERTIES);
        if (tokens.length == 1) {
            durationSpecification = NoteDurationSpecificationFactory.parseString(string);
        } else if (tokens.length == 2) {
            durationSpecification = NoteDurationSpecificationFactory.parseString(tokens[0]);
            this.stemDirection = Directions.parseAgnosticString(tokens[1]);
        } else {
            throw new IM3Exception("Expected 1 or 2 tokens, and found " + tokens.length);
        }
    }

    public INoteDurationSpecification getDurationSpecification() {
        return durationSpecification;
    }

    public void setDurationSpecification(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    @Override
    public String toAgnosticString() {
        if (durationSpecification != null) {
            if (stemDirection == null) {
                return GRACENOTE + durationSpecification.toAgnosticString();
            } else {
                return GRACENOTE + durationSpecification.toAgnosticString() + SEPPROPERTIES + stemDirection;
            }
        } else {
            return GRACENOTE;
        }
    }
}
