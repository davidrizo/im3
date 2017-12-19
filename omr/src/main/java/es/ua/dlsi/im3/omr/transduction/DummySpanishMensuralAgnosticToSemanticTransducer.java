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
    public List<List<List<SemanticToken>>> transduce(List<List<List<GraphicalToken>>> agnosticSequence) {
        List<List<List<SemanticToken>>> result = new LinkedList<>();

        for (List<List<GraphicalToken>> pagesList: agnosticSequence) {
            List<List<SemanticToken>> pagesResult = new LinkedList<>();
            for (List<GraphicalToken> regionsList : pagesList) {
                LinkedList<SemanticToken> regionsResult = new LinkedList<>();
                for (GraphicalToken graphicalToken : regionsList) {
                    switch (graphicalToken.getSymbol()) {
                        case clef:
                            regionsResult.add(new SemanticToken(SemanticSymbol.clef, "G2"));
                            break;
                        case metersign:
                            regionsResult.add(new SemanticToken(SemanticSymbol.timeSignature, "C"));
                            break;
                        case rest:
                            regionsResult.add(new SemanticToken(SemanticSymbol.rest, "minima"));
                            break;
                        case note:
                            regionsResult.add(new SemanticToken(SemanticSymbol.note, "E5_semibreve"));
                            break;
                    }
                }
                pagesResult.add(regionsResult);
            }
            result.add(pagesResult);
        }
        return result;
    }

}
