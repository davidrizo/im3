package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

import java.io.File;
import java.io.IOException;

public class BimodalSymbolFromImageAndStrokesDataRecognizerFactory {
    private static BimodalSymbolFromImageAndStrokesDataRecognizerFactory ourInstance = new BimodalSymbolFromImageAndStrokesDataRecognizerFactory();

    public static BimodalSymbolFromImageAndStrokesDataRecognizerFactory getInstance() {
        return ourInstance;
    }

    private BimodalSymbolFromImageAndStrokesDataRecognizerFactory() {
    }

    //TODO Generalizar este parámetro
    public IBimodalSymbolFromImageDataAndStrokesRecognizer create(AgnosticVersion agnosticVersion, File trainingFolder) throws IM3Exception {
        FastBimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer result = new FastBimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(agnosticVersion);
        result.trainFromFolder(trainingFolder);
        return result;
    }
}
