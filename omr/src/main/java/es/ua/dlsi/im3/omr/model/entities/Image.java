package es.ua.dlsi.im3.omr.model.entities;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.utils.TimeUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Image name, relative to the path where images are saved
     */
    private String imageRelativeFileName;
    /**
     * Ordered in the sequence of images
     */
    private int order;
    /**
     * Pages contained in the image, sorted first X, then Y
     */
    private SortedSet<Page> pages;
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
                Objects.equals(imageRelativeFileName, image.imageRelativeFileName) &&
                Objects.equals(pages, image.pages);
    }

    @Override
    public int hashCode() {

        return Objects.hash(imageRelativeFileName, order, pages);
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
}
