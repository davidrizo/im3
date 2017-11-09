package es.ua.dlsi.im3.omr.mensuralspanish;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefC2;
import es.ua.dlsi.im3.omr.IGraphicalToScoreSymbolFactory;
import es.ua.dlsi.im3.omr.PositionedSymbolType;

// TODO: 9/11/17 Esto debería integrarse con el transductor
// TODO: 9/11/17 Alturas
public class MensuralGraphicalToScoreSymbolFactory implements IGraphicalToScoreSymbolFactory<MensuralSymbols> {
    @Override
    public ITimedElementInStaff convert(PositionedSymbolType<MensuralSymbols> positionedSymbolType) throws IM3Exception {
        switch (positionedSymbolType.getSymbol()) {
            case c_clef:
                return new ClefC2();
            case minima:
                return new SimpleNote(Figures.MINIM, 0, new ScientificPitch(PitchClasses.B, 4));
            case semibrevis:
                return new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.B, 4));
            default:
                throw new IM3Exception("Unsupported symbol type: " + positionedSymbolType.getSymbol().getClass());
        }
    }
}
