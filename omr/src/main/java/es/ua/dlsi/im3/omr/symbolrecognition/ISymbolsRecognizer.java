package es.ua.dlsi.im3.omr.symbolrecognition;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.pojo.Page;

import java.util.List;

public interface ISymbolsRecognizer {
    /**
     * It leaves the results in each region of the page
     * @param pages
     */
    void recognize(List<Page> pages) throws IM3Exception;
}
