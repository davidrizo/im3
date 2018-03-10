package es.ua.dlsi.im3.omr.classifiers.segmentation;

public class DocumentSegmenterFactory {
    private static DocumentSegmenterFactory ourInstance = new DocumentSegmenterFactory();

    public static DocumentSegmenterFactory getInstance() {
        return ourInstance;
    }

    private DocumentSegmenterFactory() {
    }

    public IDocumentSegmenter create() {
        //TODO
        return new DummyDocumentSegmenter();
    }
}
