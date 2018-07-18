package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

import java.util.TreeSet;

/**
 * It recognizes a symbol given the image.
 * @autor drizo
 */
public interface ISymbolFromImageDataRecognizer {
    /**
     *
     * @param imageData
     * @return Orderred list of AgnosticSymbol with positionInStaff or not depending on the classifier
     */
    NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImagePrototype> recognize(GrayscaleImageData imageData) throws IM3Exception;
}
