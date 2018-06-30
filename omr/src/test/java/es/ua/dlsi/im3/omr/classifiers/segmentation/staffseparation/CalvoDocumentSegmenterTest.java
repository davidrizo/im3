package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.model.entities.Region;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class CalvoDocumentSegmenterTest {
    private void test(String filename, int expectedRegions) throws IM3Exception {
        File imageFile = TestFileUtils.getFile(filename);
        CalvoDocumentSegmenter segmenter = new CalvoDocumentSegmenter(imageFile);
        List<Region> pages = segmenter.segment();

        assertEquals(filename, expectedRegions, pages.size());
    }


    @Test
    public void segment() throws Exception {
        test("/testdata/images/mensural/manuscript/16-1544_ES-VC_1-3-1_00014.tif", 3); // there are actually 4 staves
        test("/testdata/images/mensural/manuscript/12612.JPG", 7); // there are actually 12 staves in two pages
        test("/testdata/images/mensural/manuscript/12608.JPG", 6); // it returns 6 regions!!
    }
}