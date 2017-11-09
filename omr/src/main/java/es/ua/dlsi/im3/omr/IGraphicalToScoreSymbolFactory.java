package es.ua.dlsi.im3.omr;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;

public interface IGraphicalToScoreSymbolFactory<SymbolType> {
    ITimedElementInStaff convert(PositionedSymbolType<SymbolType> positionedSymbolType) throws IM3Exception;
}
