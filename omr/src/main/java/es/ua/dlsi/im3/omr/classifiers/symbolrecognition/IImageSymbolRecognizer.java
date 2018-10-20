package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.entities.Page;
import es.ua.dlsi.im3.omr.model.entities.Symbol;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * It gets an image and returns all symbols found
 */
public interface IImageSymbolRecognizer {
    List<Symbol> recognize(File file) throws IM3Exception;
    String toString();
}
