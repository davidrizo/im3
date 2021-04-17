package es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols;

/**
 * @author David Rizo - drizo@dlsi.ua.es
 * @created 17/4/21
 */
public interface IAgnosticNote {
    INoteDurationSpecification getDurationSpecification();
    Directions getStemDirection();
}
