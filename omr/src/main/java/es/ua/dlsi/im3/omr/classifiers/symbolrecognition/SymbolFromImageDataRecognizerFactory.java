package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

import java.io.File;

public class SymbolFromImageDataRecognizerFactory {
    private static SymbolFromImageDataRecognizerFactory ourInstance = new SymbolFromImageDataRecognizerFactory();

    public static SymbolFromImageDataRecognizerFactory getInstance() {
        return ourInstance;
    }

    private SymbolFromImageDataRecognizerFactory() {
    }

    //TODO Generalizar este parámetro
    public ISymbolFromImageDataRecognizer create(AgnosticVersion agnosticVersion, File trainingFolder) throws IM3Exception {
        return new NearestNeighbourSymbolFromImageRecognizer(agnosticVersion, trainingFolder);
    }
}
