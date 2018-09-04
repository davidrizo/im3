package es.ua.dlsi.im3.omr.muret;

import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;

/**
 * @autor drizo
 */
public class Classifiers {
    AgnosticSequenceRecognizer endToEndAgnosticSequenceRecognizer;

    public Classifiers() {
    }

    public AgnosticSequenceRecognizer getEndToEndAgnosticSequenceRecognizerInstance() {
        if (endToEndAgnosticSequenceRecognizer == null) {
            endToEndAgnosticSequenceRecognizer = new AgnosticSequenceRecognizer();
        }
        return endToEndAgnosticSequenceRecognizer;
    }
}
