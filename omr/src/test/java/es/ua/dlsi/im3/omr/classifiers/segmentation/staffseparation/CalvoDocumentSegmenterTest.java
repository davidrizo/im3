package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.model.pojo.Page;
import es.ua.dlsi.im3.omr.model.pojo.Region;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class CalvoDocumentSegmenterTest {
    private void test(String filename, int expectedRegions) throws IM3Exception, MalformedURLException {
        CalvoDocumentSegmenter segmenter = new CalvoDocumentSegmenter();
        File imageFile = TestFileUtils.getFile(filename);
        URL image = imageFile.toURI().toURL();
        List<Region> pages = segmenter.segment(image);

        assertEquals(filename, expectedRegions, pages.size());
    }


    @Test
    public void segment() throws Exception {
        test("/testdata/images/mensural/manuscript/16-1544_ES-VC_1-3-1_00014.tif", 6); // there are actually 5 pages
        test("/testdata/images/mensural/manuscript/12612.JPG", 14); // there are actually 12 staves, but the algorithm finds another one at top
        test("/testdata/images/mensural/manuscript/12608.JPG", 12); // it returns 12 regions!!
    }
}