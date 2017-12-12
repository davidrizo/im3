package es.ua.dlsi.im3.omr.segmentation;

import es.ua.dlsi.im3.omr.model.pojo.Region;

import java.io.File;
import java.util.List;

/**
 * It splits the page into regions that will contain different types of content (title, staves...)
 */
public interface IPageSegmenter {
    List<Region> segment(File imageFile);
}
