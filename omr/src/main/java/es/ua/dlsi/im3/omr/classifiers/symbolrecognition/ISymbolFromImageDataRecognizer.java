package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;

import java.util.List;

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
    List<AgnosticSymbol> recognize(GrayscaleImageData imageData) throws IM3Exception;
}
