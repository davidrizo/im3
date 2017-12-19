package es.ua.dlsi.im3.omr.transduction;

import es.ua.dlsi.im3.omr.model.pojo.GraphicalToken;
import es.ua.dlsi.im3.omr.model.pojo.SemanticSymbol;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;

import java.util.List;

public interface IAgnosticToSemanticTransducer {
    /**
     *
     * @param agnosticSequence First list = pages, second list = regions, third list = graphical tokens
     * @return First list = pages, second list = regions, third list = semantic tokens
     */
    List<List<List<SemanticToken>>> transduce(List<List<List<GraphicalToken>>> agnosticSequence);
}
