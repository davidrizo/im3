package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class ImageUtils {
    private static ImageUtils instance = null;

    /**
     * Used to avoid loading twice the JAI library (to load TIFF)
     */
    private static boolean JAI_CHECKED = false;

    private void checkJAI() throws IM3Exception {
        if (!JAI_CHECKED) {
            Instant start = Instant.now();

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Loading JAI");
            JAI_CHECKED = true;

            Iterator<ImageReader> reader = ImageIO.getImageReadersByFormatName("TIFF");
            if (reader == null || !reader.hasNext()) { // pom.xml needs jai-imageio-core for loading TIFF files
                throw new IM3Exception("TIFF format not supported");
            }

            Instant end = Instant.now();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "JAI loaded in {0}", TimeUtils.getTimeElapsed(start, end));
        }
    }

    private ImageUtils() {
    }
    public static ImageUtils getInstance() {
        synchronized (ImageUtils.class) {
            if (instance == null) {
                instance = new ImageUtils();
            }
        }
        return instance;
    }


    //TODO Test unitario
    public int [][] readGrayScaleImage(BufferedImage img) throws IM3Exception {
        return readGrayScaleImage(img, 0, 0, img.getWidth()-1, img.getHeight()-1);
    }

    //TODO Test
    /**
     * Extract just a region of the image
     * @param img
     * @param fromX
     * @param fromY
     * @param toX Included
     * @param toY Included
     * @return
     */
    public int [][] readGrayScaleImage(BufferedImage img, int fromX, int fromY, int toX, int toY) throws IM3Exception {
        if (fromX < 0) {
            throw new IM3Exception("Negative fromX: " + fromX);
        }
        if (fromY < 0) {
            throw new IM3Exception("Negative fromY: " + fromY);
        }
        if (toX > img.getWidth()) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "toX (" + toX + ") >= image width (" + img.getWidth() + "), adapting to max value" );
            toX = img.getWidth()-1;
        }
        if (toY >= img.getHeight()) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "toY (" + toY + ") >= image height (" + img.getHeight()  + "), adapting to max value" );
            toY = img.getHeight()-1;
        }
        int [][] result = new int[toX-fromX+1][toY-fromY+1];
        for (int i=0; i<result.length; i++) {
            for (int j=0; j<result[i].length; j++) {
                int gray = 0;
                try {
                    gray = getPixelGrayscale(img, i+fromX, j+fromY);
                } catch (ArrayIndexOutOfBoundsException ae) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                            "Accesing a position (" + (i+fromX) + ", " + (j+fromY) + ") out the image boundaries (" + img.getWidth() + ", " + img.getHeight() + ")");
                    throw new IM3Exception(ae);
                }
                result[i][j] = gray;
            }
        }
        return result;
    }

    public final int getPixelGrayscale(BufferedImage image, int x, int y) {
        int p = image.getRGB(x, y);
        //int a = (p>>24)&0xff;
        int r = (p>>16)&0xff;
        int g = (p>>8)&0xff;
        int b = p&0xff;
        int avg = (r+g+b)/3;
        return avg;
    }

    public BufferedImage rescaleToGray(BufferedImage image, int w, int h) {
        BufferedImage scaledImage = new BufferedImage(w, h, image.getType());
        Graphics g = scaledImage.createGraphics();
        g.drawImage(image, 0, 0, w, h, null);
        g.dispose();
        return scaledImage;
    }

    public BufferedImage createImageFromIntArray(int[] imageData, int width, int height)
    {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte [] newData = ((DataBufferByte) newImage.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < imageData.length; i++)
        {
            newData[i] = (byte) imageData[i];
        }
        return newImage;
    }

    public BufferedImage createImageFromDoubleArray(double[] imageData, int width, int height)
    {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte [] newData = ((DataBufferByte) newImage.getRaster().getDataBuffer()).getData();

        for (int i = 0; i < imageData.length; i++)
        {
            newData[i] = (byte) imageData[i];
        }
        return newImage;
    }

    public int[][] readGrayScaleImage(BufferedImage bufferedImage, BoundingBox boundingBox) throws IM3Exception {
        return readGrayScaleImage(bufferedImage,
                (int)boundingBox.getFromX(),
                (int)boundingBox.getFromY(),
                (int)boundingBox.getToX(),
                (int)boundingBox.getToY());
    }

    public BufferedImage generateBufferedImage(File imageFile) throws IM3Exception {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot read image " + imageFile.getAbsolutePath(), e);
            throw new IM3Exception("Cannot read image", e);
        }
        return bufferedImage;
    }

    public BufferedImage extractBufferedImage(File imageFile, BoundingBox boundingBox) throws IM3Exception {
        BufferedImage subimage = null;
        String imgWidth="image not read";
        String imgHeight="image not read";
        try {
            BufferedImage fullImage = ImageIO.read(imageFile);
            return extractBufferedImage(fullImage, boundingBox);
        } catch (Throwable e) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cannot read image {0} of size {1}x{2}, extracting subimage fromX={3}, fromY={4}, width={5}, height={6}",
                    new Object[]{imageFile.getAbsolutePath(),
                            imgWidth, imgHeight,
                        (int)boundingBox.getFromX(), (int)boundingBox.getFromY(), (int)boundingBox.getWidth(), (int)boundingBox.getHeight()});
            throw new IM3Exception("Cannot read image '" + imageFile.getAbsolutePath() + "'", e);
        }
    }

    public BufferedImage extractBufferedImage(BufferedImage fullImage, BoundingBox boundingBox) throws IM3Exception {
        BufferedImage subimage = null;
        String imgWidth="image not read";
        String imgHeight="image not read";
        try {
            imgWidth = new Integer(fullImage.getWidth()).toString();
            imgHeight = new Integer(fullImage.getHeight()).toString();

            int maxWidth = fullImage.getWidth() - (int)boundingBox.getFromX();
            int maxHeight = fullImage.getHeight() - (int)boundingBox.getFromY();

            int x = (int)boundingBox.getFromX();
            int y = (int)boundingBox.getFromY();
            int w = (int)Math.min(maxWidth, boundingBox.getWidth());
            int h = (int)Math.min(maxHeight, boundingBox.getHeight());
            subimage = fullImage.getSubimage(x,
                    y,
                    w,
                    h);

        } catch (Throwable e) {
            throw new IM3Exception("Cannot extract buffered image", e);
        }
        return subimage;
    }

    public void scaleToFitHeight(File inputImage, File outputImage, int height) throws IM3Exception {
        Image scaledImage = null;
        String extension = FileUtils.getExtension(outputImage);
        try {
            BufferedImage fullImage = ImageIO.read(inputImage);
            scaledImage = fullImage.getScaledInstance(-1, height, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new IM3Exception("Cannot process input image '" + inputImage.getAbsolutePath() + "'");
        }

        try {
            BufferedImage resized = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            ImageIO.write(resized, extension, outputImage);
        } catch (IOException e) {
            throw new IM3Exception("Cannot process output image '" + outputImage.getAbsolutePath() + "'");
        }
    }

    public BufferedImage crop(BufferedImage inputImage, int fromX, int fromY, int toX, int toY) {
        fromX = Math.max(0, fromX);
        fromY = Math.max(0, fromY);
        toX = Math.min(inputImage.getWidth(), toX);
        toY = Math.min(inputImage.getHeight(), toY);

        return inputImage.getSubimage(fromX, fromY, toX - fromX, toY - fromY);
    }


    public BufferedImage rotate(BufferedImage image, double degrees) {
        //https://blog.idrsolutions.com/2019/05/image-rotation-in-java/
        final double rads = Math.toRadians(degrees);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
        final int h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, image.getType());
        final AffineTransform at = new AffineTransform();
        //at.translate(w / 2, h / 2); // it rotates from the bottom right corner
        at.rotate(rads,0, 0);
        //at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
        final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(image,rotatedImage);
        return rotatedImage;
    }

}
