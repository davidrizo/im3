package es.ua.dlsi.im3.omr;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

public interface IGraphicalToScoreSymbolFactory<SymbolType> {
    ITimedElementInStaff convert(AgnosticSymbol positionedSymbolType) throws IM3Exception;
}
