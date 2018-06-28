package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

public class SymbolRecognizerFactory {
    private static SymbolRecognizerFactory ourInstance = new SymbolRecognizerFactory();

    public static SymbolRecognizerFactory getInstance() {
        return ourInstance;
    }

    private SymbolRecognizerFactory() {
    }

    public IImageSymbolRecognizer create() {
        return new StubImage00531SymbolRecognizer(); //TODO
    }
}
