package es.ua.dlsi.im3.omr.segmentation;

import java.util.LinkedList;
import java.util.List;

/**
 * It returns fixed values based on a test page
 */
public class DummyPageSegmenter implements IPageSegmenter {

    @Override
    public List<PageSegment> segment() {
        LinkedList<PageSegment> segments = new LinkedList<>();
        segments.add(new PageSegment(SegmentType.titles, 1630, 47, 2122, 118));
        segments.add(new PageSegment(SegmentType.titles, 2520, 28, 2642, 130));
        segments.add(new PageSegment(SegmentType.staff, 1627, 151, 2960, 300));
        segments.add(new PageSegment(SegmentType.lyrics, 1627, 322, 2960, 446));
        segments.add(new PageSegment(SegmentType.staff, 1595, 455, 3006, 629));
        segments.add(new PageSegment(SegmentType.lyrics, 1595, 723, 3006, 723));
        return segments;
    }
}
