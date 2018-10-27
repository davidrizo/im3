package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ImageUtilsTest {

    @Test
    public void scaleToFitHeight() throws IM3Exception, IOException {
        File input = TestFileUtils.getFile("/testdata/core/utils/java-duke.png");
        File output = TestFileUtils.createTempFile("minjavaduke.png");
        BufferedImage inputBI = ImageUtils.getInstance().generateBufferedImage(input);
        assertEquals("Input width", 375, inputBI.getHeight());
        assertEquals("Input height", 375, inputBI.getWidth());
        ImageUtils.getInstance().scaleToFitHeight(input, output, 200);
        BufferedImage outputBI = ImageUtils.getInstance().generateBufferedImage(output);
        assertEquals("Output width", 200, outputBI.getHeight());
        assertEquals("Output height", 200, outputBI.getWidth());

    }
}