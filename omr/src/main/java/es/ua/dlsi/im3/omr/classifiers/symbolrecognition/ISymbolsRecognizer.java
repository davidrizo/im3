package es.ua.dlsi.im3.omr.classifiers.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.pojo.Page;

import java.net.URL;
import java.util.List;

public interface ISymbolsRecognizer {
    /**
     * It leaves the results in each region of the page
     * @param pages
     */
    void recognize(URL imagesURL, List<Page> pages) throws IM3Exception;
}
