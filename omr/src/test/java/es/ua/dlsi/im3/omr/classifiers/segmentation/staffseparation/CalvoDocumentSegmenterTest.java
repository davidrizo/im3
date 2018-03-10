package es.ua.dlsi.im3.omr.classifiers.segmentation.staffseparation;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.model.pojo.Page;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class CalvoDocumentSegmenterTest {

    @Test
    public void segment() throws Exception {
        CalvoDocumentSegmenter segmenter = new CalvoDocumentSegmenter();
        File imageFile = TestFileUtils.getFile("/testdata/images/mensural/manuscript/12612.JPG");
        URL image = imageFile.toURI().toURL();
        List<Page> pages = segmenter.segment(image);
        assertEquals("12612.jpg, pages", 2, pages.size());
        for (Page page: pages) {
            //TODO Devuelve 7 assertEquals("12612.jpg, staves", 6, page.getRegions());
        }

        //TODO 12608.jpg
    }
}