package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation.CalvoDocumentSegmenter;

import java.io.File;

public class DocumentSegmenterFactory {
    private static DocumentSegmenterFactory ourInstance = new DocumentSegmenterFactory();

    public static DocumentSegmenterFactory getInstance() {
        return ourInstance;
    }

    private DocumentSegmenterFactory() {
    }

    public IDocumentSegmenter create(File imageFile) throws IM3Exception {
        //return new DummyDocumentSegmenter();
        return new CalvoDocumentSegmenter(imageFile);
    }
}
