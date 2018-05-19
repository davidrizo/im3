package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.StemDirection;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

public class Note extends AgnosticSymbolType {
    private static final String NOTE = "note" + SEPSYMBOL;

    INoteDurationSpecification durationSpecification;
    /**
     * v2
     */
    Directions stemDirection;

    public Note(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    public Note(INoteDurationSpecification durationSpecification, Directions stemDirection) {
        this.durationSpecification = durationSpecification;
        this.stemDirection = stemDirection;
    }

    public Note() {
    }

    public Directions getStemDirection() {
        return stemDirection;
    }

    public void setStemDirection(Directions stemDirection) {
        this.stemDirection = stemDirection;
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
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
        if (stemDirection == null) {
            return NOTE + durationSpecification.toAgnosticString();
        } else {
            return NOTE + durationSpecification.toAgnosticString() + SEPPROPERTIES + stemDirection;
        }
    }
}
