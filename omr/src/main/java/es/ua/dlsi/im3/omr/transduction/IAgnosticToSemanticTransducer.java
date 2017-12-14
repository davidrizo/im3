package es.ua.dlsi.im3.omr.transduction;

import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.SemanticSymbol;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;

import java.util.List;

public interface IAgnosticToSemanticTransducer {
    List<SemanticToken> transduce(List<GraphicalToken> agnosticSequence);
}
