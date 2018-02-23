package es.ua.dlsi.im3.omr.classifiers.segmentation;

public class PageSegmenterFactory {
    private static PageSegmenterFactory ourInstance = new PageSegmenterFactory();

    public static PageSegmenterFactory getInstance() {
        return ourInstance;
    }

    private PageSegmenterFactory() {
    }

    public IPageSegmenter create() {
        //TODO
        return new DummyPageSegmenter();
    }
}
