package es.ua.dlsi.im3.omr.interactive.model;

import java.io.File;

public class ImageFile {
    File imageFile;
    int order;

    public ImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return imageFile + " (" + order + ")";
    }
}
