package es.ua.dlsi.im3.omr.model.entities;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBoxXY;
import es.ua.dlsi.im3.core.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

/**
 * An image file
 */
public class Image implements Comparable<Image> {
    /**
     * Width of the scaled image
     */
    public final static int RESIZE_W = 30;
    /**
     * Height of the scaled image
     */
    public final static int RESIZE_H = 30;

    /**
     * Image name, relative to the path where imagesold are saved
     */
    private String imageRelativeFileName;
    /**
     * Ordered in the sequence of imagesold
     */
    private int order;
    /**
     * Pages contained in the image, sorted first X, then Y
     */
    private SortedSet<Page> pages;
    /**
     * Instrument, it may be null if not the same for all pages
     */
    private Instrument instrument;
    /**
     * Comments about the image
     */
    private String comments;

    public Image() {
        pages = new TreeSet<>();
    }

    public Image(String imageRelativeFileName) {
        this.imageRelativeFileName = imageRelativeFileName;
        pages = new TreeSet<>();
    }

    public String getImageRelativeFileName() {
        return imageRelativeFileName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setImageRelativeFileName(String imageRelativeFileName) {
        this.imageRelativeFileName = imageRelativeFileName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public SortedSet<Page> getPages() {
        return pages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return order == image.order &&
                Objects.equals(instrument, image.instrument) &&
                Objects.equals(imageRelativeFileName, image.imageRelativeFileName) &&
                Objects.equals(pages, image.pages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageRelativeFileName, order, pages, instrument);
    }

    public void addPage(Page page) {
        this.pages.add(page);
    }

    @Override
    public int compareTo(Image o) {
        int diff = order - o.order;
        if (diff == 0) {
            diff = imageRelativeFileName.compareTo(o.imageRelativeFileName);
        }
        return diff;
    }

    public BoundingBoxXY computeBoundingBox(File fileImagesFolder) throws IM3Exception {
        File imageFile = new File(fileImagesFolder, imageRelativeFileName);
        BufferedImage subimage = ImageUtils.getInstance().generateBufferedImage(imageFile);
        return new BoundingBoxXY(0, 0, subimage.getWidth(), subimage.getHeight());
    }
    /**
     * Get rescaled pixels to RESIZE_W and RESIZE_H
     * @param fileImagesFolder
     * @param boundingBox
     * @return
     * @throws IM3Exception
     */
    public int[] getGrayscaleImagePixelsNormalized(File fileImagesFolder, BoundingBox boundingBox) throws IM3Exception {
        File imageFile = new File(fileImagesFolder, imageRelativeFileName);
        BufferedImage subimage = ImageUtils.getInstance().generateBufferedImage(imageFile, boundingBox);
        BufferedImage scaledImage = ImageUtils.getInstance().rescaleToGray(subimage, Image.RESIZE_W, Image.RESIZE_H);
        int[][] imagePixels = ImageUtils.getInstance().readGrayScaleImage(scaledImage);

        if (imagePixels.length != Image.RESIZE_W) {
            throw new IM3Exception("Expected width " +  Image.RESIZE_W + " and found " + imagePixels.length);
        }
        if (imagePixels[0].length != Image.RESIZE_H) {
            throw new IM3Exception("Expected height " +  Image.RESIZE_H + " and found " + imagePixels[0].length);
        }
        int [] result = new int[Image.RESIZE_W * Image.RESIZE_H];
        int index=0;
        for (int i=0; i<imagePixels.length; i++) {
            for (int j=0; j<imagePixels[i].length; j++) {
                result[index++] = imagePixels[i][j];
            }
        }
        return result;
    }

    /**
     * Get rescaled pixels to RESIZE_W and RESIZE_H
     * @param fileImagesFolder
     * @param boundingBox
     * @return
     * @throws IM3Exception
     */
    public int[][] getGrayscaleImagePixels(File fileImagesFolder, BoundingBox boundingBox) throws IM3Exception {
        File imageFile = new File(fileImagesFolder, imageRelativeFileName);
        BufferedImage subimage = ImageUtils.getInstance().generateBufferedImage(imageFile, boundingBox);
        int[][] imagePixels = ImageUtils.getInstance().readGrayScaleImage(subimage);
        return imagePixels;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }
}
