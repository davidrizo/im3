package es.ua.dlsi.im3.omr.transduction;

import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.SemanticSymbol;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;

import java.util.LinkedList;
import java.util.List;

/**
 * Just works with the first symbols of 12608.JPG
 */
public class DummySpanishMensuralAgnosticToSemanticTransducer implements IAgnosticToSemanticTransducer {
    @Override
    public List<SemanticToken> transduce(List<GraphicalToken> agnosticSequence) {
        LinkedList<SemanticToken> result = new LinkedList<>();
        for (GraphicalToken graphicalToken: agnosticSequence) {
            switch (graphicalToken.getSymbol()) {
                case clef:
                    result.add(new SemanticToken(SemanticSymbol.clef, "G2"));
                    break;
                case metersign:
                    result.add(new SemanticToken(SemanticSymbol.timeSignature, "C"));
                    break;
                case rest:
                    result.add(new SemanticToken(SemanticSymbol.rest, "minima"));
                    break;
                case note:
                    result.add(new SemanticToken(SemanticSymbol.note, "E4_semibreve"));
                    break;
            }
        }
        return result;
    }
}
