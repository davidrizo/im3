package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.omr.model.pojo.Region;

import java.net.URL;
import java.util.List;

/**
 * It splits the page into regions that will contain different types of content (title, staves...)
 */
public interface IPageSegmenter {
    List<Region> segment(URL imageFile);
}
