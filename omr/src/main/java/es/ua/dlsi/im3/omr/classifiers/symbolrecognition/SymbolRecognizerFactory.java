package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

public class SymbolRecognizerFactory {
    private static SymbolRecognizerFactory ourInstance = new SymbolRecognizerFactory();

    public static SymbolRecognizerFactory getInstance() {
        return ourInstance;
    }

    private SymbolRecognizerFactory() {
    }

    public ISymbolsRecognizer create() {
        return new DummySymbolRecognizer(); //TODO
    }
}
