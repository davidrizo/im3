package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.pojo.Page;
import es.ua.dlsi.im3.omr.model.pojo.Region;

import java.net.URL;
import java.util.List;

/**
 * It splits the documento in pages, and in turn each page into regions that will contain different types of content (title, staves...)
 */
public interface IDocumentSegmenter {
    List<Page> segment(URL imageFile) throws IM3Exception;
}
