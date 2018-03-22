package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation.CalvoDocumentSegmenter;

public class DocumentSegmenterFactory {
    private static DocumentSegmenterFactory ourInstance = new DocumentSegmenterFactory();

    public static DocumentSegmenterFactory getInstance() {
        return ourInstance;
    }

    private DocumentSegmenterFactory() {
    }

    public IDocumentSegmenter create() {
        //return new DummyDocumentSegmenter();
        return new CalvoDocumentSegmenter();
    }
}
