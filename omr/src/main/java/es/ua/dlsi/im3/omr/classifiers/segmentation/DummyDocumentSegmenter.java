package es.ua.dlsi.im3.omr.classifiers.segmentation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.model.entities.Region;
import es.ua.dlsi.im3.omr.model.entities.RegionType;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * It returns fixed values based on a test page 12608.JPG
 */
public class DummyDocumentSegmenter implements IDocumentSegmenter {

    public List<Region> segment(URL imageFile) throws IM3Exception {
        ArrayList<Region> result = new ArrayList<>();
        result.add(new Region(RegionType.title, 1630, 47, 2122, 118));
        result.add(new Region(RegionType.title, 2520, 28, 2642, 130));
        result.add(new Region(RegionType.staff, 1627, 151, 2960, 300));
        result.add(new Region(RegionType.lyrics, 1627, 322, 2960, 446));
        result.add(new Region(RegionType.staff, 1595, 455, 3006, 629));
        result.add(new Region(RegionType.lyrics, 1595, 723, 3006, 723));
        return result;
    }
}
