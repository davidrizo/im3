package es.ua.dlsi.im3.omr.imageprocessing;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;

import java.io.File;
import java.io.IOException;

public class StaffNormalizerTest {

    //@Test
    public void normalize() throws IM3Exception, IOException {
        File input = TestFileUtils.getFile("/testdata/imageprocessing/48.jpg");
        File outputCalvo = TestFileUtils.createTempFile("calvo48.jpg");
        File outputCardoso = TestFileUtils.createTempFile("cardoso48.jpg");

        //TODO De momento sólo funciona en el ordenador de David - está pendiente de migración
        if (System.getProperty("user.home").equals("/Users/drizo")) {
            CardosoStaffNormalizer cardosoStaffNormalizer = new CardosoStaffNormalizer();
            cardosoStaffNormalizer.normalize(input, outputCardoso);

            JCalvoStaffNormalizer calvoStaffNormalizer = new JCalvoStaffNormalizer();
            calvoStaffNormalizer.normalize(input, outputCalvo);
        }
    }
}