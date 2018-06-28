package es.ua.dlsi.im3.omr.imageprocessing;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class StaffNormalizerTest {

    @Test
    public void normalize() throws IM3Exception, IOException {
        StaffNormalizer staffNormalizer = new StaffNormalizer();
        File input = TestFileUtils.getFile("/testdata/imageprocessing/48.jpg");
        File output = TestFileUtils.createTempFile("processed48.jpg");


        //TODO De momento sólo funciona en el ordenador de David - está pendiente de migración
        if (System.getProperty("user.home").equals("/Users/drizo")) {
            staffNormalizer.normalize(input, output);
        }
    }
}