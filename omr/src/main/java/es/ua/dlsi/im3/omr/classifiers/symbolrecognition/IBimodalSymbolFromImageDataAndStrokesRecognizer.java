package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.patternmatching.RankingItem;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.model.entities.Strokes;

import java.io.File;
import java.util.TreeSet;

/**
 * It recognizes a symbol given the image.
 * @autor drizo
 */
public interface IBimodalSymbolFromImageDataAndStrokesRecognizer {
    /**
     *
     * @param imageData
     * @param strokes
     * @return Ordered list of AgnosticSymbol with positionInStaff or not depending on the classifier
     */
    NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> recognize(GrayscaleImageData imageData, Strokes strokes) throws IM3Exception;

    int getTrainingSetSize();

    boolean isTrained();

    /**
     * It locates the suitable files to train in the folder
     * @param folder
     */
    void trainFromFolder(File folder) throws IM3Exception;

    String toString();
}
