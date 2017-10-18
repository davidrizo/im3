package es.ua.dlsi.im3.omr.primus.conversions;

import java.util.List;

public class ScoreGraphicalDescription {
    /**
     * Currently we use a list of tokens
     */
    List<GraphicalToken> tokens;

    public ScoreGraphicalDescription(List<GraphicalToken> tokens) {
        this.tokens = tokens;
    }

    public List<GraphicalToken> getTokens() {
        return tokens;
    }

    @Override
    public String toString() {
        return tokens.toString();
    }
}
