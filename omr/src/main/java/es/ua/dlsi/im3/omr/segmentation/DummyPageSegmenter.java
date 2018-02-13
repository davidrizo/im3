package es.ua.dlsi.im3.omr.segmentation;

import es.ua.dlsi.im3.omr.model.pojo.Region;
import es.ua.dlsi.im3.omr.model.pojo.RegionType;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * It returns fixed values based on a test page 12608.JPG
 */
public class DummyPageSegmenter implements IPageSegmenter {

    @Override
    public List<Region> segment(File imageFile) {
        LinkedList<Region> segments = new LinkedList<>();
        segments.add(new Region(RegionType.title, 1630, 47, 2122, 118));
        segments.add(new Region(RegionType.title, 2520, 28, 2642, 130));
        segments.add(new Region(RegionType.staff, 1627, 151, 2960, 300));
        segments.add(new Region(RegionType.lyrics, 1627, 322, 2960, 446));
        segments.add(new Region(RegionType.staff, 1595, 455, 3006, 629));
        segments.add(new Region(RegionType.lyrics, 1595, 723, 3006, 723));
        return segments;
    }
}
