package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.model.entities.Strokes;

import java.util.TreeSet;

/**
 * It recognizes a symbol given the image.
 * @autor drizo
 */
public interface ISymbolFromStrokesRecognizer {
    /**
     *
     * @param strokes
     * @return Orderred list of AgnosticSymbol with positionInStaff or not depending on the classifier
     */
    NearestNeighbourClassesRanking<AgnosticSymbol, SymbolPointsPrototype> recognize(Strokes strokes) throws IM3Exception;
}
