package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

/**
 * It recognizes a symbol given the image.
 * @autor drizo
 */
public interface ISymbolFromImageDataRecognizer {
    String getName();
    boolean isTrained();

    /**
     * It locates the model files it needs
     * @param trainingFolder
     * @throws IM3Exception
     */
    void trainFromFolder(File trainingFolder) throws IM3Exception;
    /**
     *
     * @param imageData
     * @return Orderred list of AgnosticSymbol with positionInStaff or not depending on the classifier
     */
    NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImagePrototype> recognize(GrayscaleImageData imageData) throws IM3Exception;
}
