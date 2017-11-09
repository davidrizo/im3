package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.omr.interactive.OMRController;
import org.junit.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

public class OMRInputOutputTest {
    /* Creates a randomly generated two color JPEG and writes it to a file.
    * From http://it.toolbox.com/blogs/lim/how-to-generate-jpeg-images-from-java-41449
     */
    public static void generate(File file) throws Exception {
        Random random = new Random(56743793);

        int x, y = 0;
        //image block size in pixels, 1 is 1px, use smaller values for
        //greater granularity
        int PIX_SIZE = 5;
        //image size in pixel blocks
        int X = 100;
        int Y = 100;

        BufferedImage bi = new BufferedImage(PIX_SIZE * X, PIX_SIZE * Y,
            BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();


        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                x = i * PIX_SIZE;
                y = j * PIX_SIZE;

                //this is a writing condition, my choice here is purely random
                // just to generate some pattern
                // this condition
                if ((i * j) % 6 == 0) {
                    g.setColor(Color.GRAY);
                } else if ((i + j) % 5 == 0) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.WHITE);
                }//end else

                //fil the rectangles with the pixel blocks in chosen color
                g.fillRect(y, x, PIX_SIZE, PIX_SIZE);
            }//end for j
        }//end for i

        g.dispose();
        saveToFile(bi, file);
    }//end method


    /**
     * Saves jpeg to file
     */

    public static void saveToFile(BufferedImage img, File file) throws IOException {
        ImageWriter writer = null;

        java.util.Iterator iter = ImageIO.getImageWritersByFormatName("jpg");

        if (iter.hasNext()) {
            writer = (ImageWriter) iter.next();
        }
        ImageOutputStream ios = ImageIO.createImageOutputStream(file);

        writer.setOutput(ios);

        ImageWriteParam param = new JPEGImageWriteParam(java.util.Locale.getDefault());

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        param.setCompressionQuality(0.98f);

        writer.write(null, new IIOImage(img, null, null), param);
    }//end method

    private void test(OMRProject project) {

    }

    @Test
    public void saveLoad() throws Exception {
        OMRPage.SKIP_JAVAFX = true;
        String filename = "omrproject_" + new Date().toInstant().toEpochMilli();
        File projectFolder = TestFileUtils.createTempFolder(filename);
        if (projectFolder.exists()) {
            projectFolder.delete();
        }
        File trainFile = TestFileUtils.getFile("/testdata/bimodal/bimodal_tiny.train");
        OMRProject project = new OMRProject(projectFolder, trainFile, null);

        File image1 = TestFileUtils.createTempFile("img1.jpg");
        File image2 = TestFileUtils.createTempFile("img2.jpg");
        generate(image1);
        generate(image2);

        project.addPage(image1);
        project.addPage(image2);

        InputOutput io = new InputOutput();
        io.save(project);

        assertTrue(new File(projectFolder, filename + ".mrt").exists());
        assertTrue(new File(projectFolder, OMRProject.IMAGES_FOLDER + File.separator + "img1.jpg").exists());
        assertTrue(new File(projectFolder, OMRProject.IMAGES_FOLDER + File.separator + "img2.jpg").exists());


        // --- Load it
        OMRController controller = new OMRController();
        OMRProject loaded = io.load(controller, projectFolder, trainFile);
        assertEquals("Loaded images", 2, loaded.pagesProperty().size());
    }

}