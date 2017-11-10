package es.ua.dlsi.im3.omr.segmentation;

import java.util.List;

/**
 * It splits the page into regions that will contain different types of content (title, staves...)
 */
public interface IPageSegmenter {
    List<PageSegment> segment();
}
