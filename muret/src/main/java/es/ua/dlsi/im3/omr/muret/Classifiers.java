package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.FastBimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.IBimodalSymbolFromImageDataAndStrokesRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.ISymbolFromImageDataRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.NearestNeighbourSymbolFromImageRecognizer;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

/**
 * @autor drizo
 */
public class Classifiers {
    AgnosticSequenceRecognizer endToEndAgnosticSequenceRecognizer;
    ISymbolFromImageDataRecognizer symbolFromImageDataRecognizer;
    IBimodalSymbolFromImageDataAndStrokesRecognizer bimodalSymbolFromImageDataAndStrokesRecognizer;

    public Classifiers() {
    }

    public AgnosticSequenceRecognizer getEndToEndAgnosticSequenceRecognizerInstance() {
        if (endToEndAgnosticSequenceRecognizer == null) {
            endToEndAgnosticSequenceRecognizer = new AgnosticSequenceRecognizer();
        }
        return endToEndAgnosticSequenceRecognizer;
    }

    public ISymbolFromImageDataRecognizer getSymbolFromImageDataRecognizer() {
        if (symbolFromImageDataRecognizer == null) {
            symbolFromImageDataRecognizer = new NearestNeighbourSymbolFromImageRecognizer(AgnosticVersion.v2);
        }
        return symbolFromImageDataRecognizer;
    }


    public IBimodalSymbolFromImageDataAndStrokesRecognizer getBimodalSymbolFromImageDataAndStrokesRecognizer() {
        if (bimodalSymbolFromImageDataAndStrokesRecognizer == null) {
            bimodalSymbolFromImageDataAndStrokesRecognizer = new FastBimodalNearestNeighbourSymbolFromImageAndStrokesRecognizer(AgnosticVersion.v2);
        }
        return bimodalSymbolFromImageDataAndStrokesRecognizer;
    }
}
