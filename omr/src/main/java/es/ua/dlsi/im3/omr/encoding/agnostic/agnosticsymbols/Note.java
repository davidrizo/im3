package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.StemDirection;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

public class Note extends AgnosticSymbolType implements IAgnosticNote {
    private static final String NOTE = "note" + SEPSYMBOL;
    public static final String CONTEXT_SEP = "@";

    INoteDurationSpecification durationSpecification;
    /**
     * v2
     */
    Directions stemDirection;

    /**
     * Used for some special translation methods (used in Worms'21 special issue)
     */
    String agnosticContext = null;

    public Note(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    public Note(INoteDurationSpecification durationSpecification, Directions stemDirection) {
        this.durationSpecification = durationSpecification;
        this.stemDirection = stemDirection;
    }

    public Note() {
    }

    @Override
    public Directions getStemDirection() {
        return stemDirection;
    }

    public void setStemDirection(Directions stemDirection) {
        this.stemDirection = stemDirection;
    }

    @Override
    public void setSubtype(String string) throws IM3Exception {
        //TODO Código repetido en GraceNote
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

    @Override
    public INoteDurationSpecification getDurationSpecification() {
        return durationSpecification;
    }

    public void setDurationSpecification(INoteDurationSpecification durationSpecification) {
        this.durationSpecification = durationSpecification;
    }

    @Override
    public String toAgnosticString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(NOTE);
        if (durationSpecification != null) {
            stringBuilder.append(durationSpecification.toAgnosticString());
            if (stemDirection != null) {
                stringBuilder.append(SEPPROPERTIES);
                stringBuilder.append(stemDirection);
            }
        }
        if (this.agnosticContext != null) {
            stringBuilder.append(CONTEXT_SEP);
            stringBuilder.append(this.agnosticContext);
        }
        return stringBuilder.toString();
    }

    public String getAgnosticContext() {
        return agnosticContext;
    }

    public void setAgnosticContext(String agnosticContext) {
        this.agnosticContext = agnosticContext;
    }
}
