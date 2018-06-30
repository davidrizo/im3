package es.ua.dlsi.im3.mavr.model.graph;

import es.ua.dlsi.im3.core.adt.graph.INodeLabel;

/**
 * @autor drizo
 */
public class ConceptLabel<ConceptType> implements INodeLabel {
    ConceptType concept;

    public ConceptLabel(ConceptType concept) {
        this.concept = concept;
    }

    public ConceptType getConcept() {
        return concept;
    }

    @Override
    public String toString() {
        return concept.toString();
    }
}
