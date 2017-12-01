package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.old.mensuraltagger.components.ScoreImageFile;
import javafx.beans.property.SimpleObjectProperty;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageFile {
    private static boolean JAI_CHECKED = false;
    File imageFile;
    int order;
    Set<Instrument> instrumentList;
    private BufferedImage bufferedImage;

    public ImageFile(File imageFile) throws IM3Exception {
        this.imageFile = imageFile;
        instrumentList = new TreeSet<>(); // we mantain it ordered
        loadImageFile();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return imageFile.getName() + " (" + order + ")";
    }

    void loadImageFile() throws IM3Exception {
        if (!JAI_CHECKED) {
            JAI_CHECKED = true;

            Iterator<ImageReader> reader = ImageIO.getImageReadersByFormatName("TIFF");
            if (reader == null || !reader.hasNext()) { // pom.xml needs jai-imageio-core for loading TIFF files
                throw new IM3Exception("TIFF format not supported");
            }
        }

        if (!imageFile.exists()) {
            throw new IM3Exception("The image file " + imageFile.getAbsolutePath() + " does not exist");
        }

        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new IM3Exception(e);
        }
        if (bufferedImage == null) {
            throw new IM3Exception("Buffered image not loaded");
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void addInstrument(Instrument instrument) {
        this.instrumentList.add(instrument);
    }
    public void removeInstrument(Instrument instrument) {
        this.instrumentList.remove(instrument);
    }

    public Set<Instrument> getInstrumentList() {
        return instrumentList;
    }

    public String getFileName() {
        return imageFile.getName();
    }
}
