package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;

import java.util.ArrayList;
import java.util.List;

public class ScoreGraphicalDescription {
    /**
     * Currently we use a list of tokens
     */
    List<GraphicalToken> tokens;

    List<SemanticToken> semanticTokens;

    public ScoreGraphicalDescription(List<GraphicalToken> tokens) {
        this.tokens = tokens;
    }

    public ScoreGraphicalDescription(List<GraphicalToken> tokens, ArrayList<SemanticToken> semanticTokens) {
        this.tokens = tokens;
        this.semanticTokens = semanticTokens;
    }

    public List<GraphicalToken> getTokens() {
        return tokens;
    }

    public List<SemanticToken> getSemanticTokens() {
        return semanticTokens;
    }

    @Override
    public String toString() {
        return tokens.toString();
    }
}
