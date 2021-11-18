package es.ua.dlsi.im3.omr.classifiers.segmentation;

import java.io.File;

// Removed for avoiding interactions of this OpenCV with Keras in MuRET
public class DocumentSegmenterFactory {
    private static DocumentSegmenterFactory ourInstance = new DocumentSegmenterFactory();

    public static DocumentSegmenterFactory getInstance() {
        return ourInstance;
    }

    private DocumentSegmenterFactory() {
    }

    public IDocumentSegmenter create(File imageFile)  {
        //return new DummyDocumentSegmenter();
        /*return new CalvoDocumentSegmenter(imageFile);*/
        return null;
    }
}
