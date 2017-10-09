package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.omr.interactive.components.ScoreImageFile;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.image.Image;
import javafx.stage.Screen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OMRPage {
    /**
     * Used for JUnit tests
     */
    static boolean SKIP_JAVAVFX_IMAGE = false;
    /**
     * File name relative to project path
     */
    String imageRelativeFileName;
    transient File imageFile;
    List<OMRStaff> staves;

    /**
     * Used for the GUI
     */
    private transient ObservableObjectValue<Image> image;
    /**
     * Used to extract pixels from it
     */
    transient BufferedImage bufferedImage;

    public OMRPage(File projectFolder, String imageRelativeFileName) throws IM3Exception {
        this.imageRelativeFileName = imageRelativeFileName;
        loadImageFile(projectFolder);
        staves = new ArrayList<>();
    }

    void loadImageFile(File imagesFolder) throws IM3Exception {
        this.imageFile = new File(imagesFolder, imageRelativeFileName);
        if (!imageFile.exists()) {
            throw new IM3Exception("The image file " + imageFile.getAbsolutePath() + " does not exist");
        }

        Image img = null;
        try {
            if (!SKIP_JAVAVFX_IMAGE) {
                img = new Image(imageFile.toURI().toURL().toString());
                this.image = new SimpleObjectProperty<>(img);
                Logger.getLogger(ScoreImageFile.class.getName()).log(Level.INFO, "Loading image {0}, width={1}, height={2}",
                        new Object[]{imageFile.getAbsolutePath(), img.getWidth(), img.getHeight()});

            } else {
                this.image = new SimpleObjectProperty<>();
            }
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new IM3Exception(e);
        }
    }

    public void addStaff(int leftTopX, int leftTopY, int bottomRightX, int bottomRightY) {
        OMRStaff staff = new OMRStaff(this, leftTopX, leftTopY, bottomRightX, bottomRightY);
    }

    public ObservableObjectValue<Image> imageProperty() {
        return image;
    }

    public File getImageFile() {
        return imageFile;
    }

    public List<OMRStaff> getStaves() {
        return staves;
    }
}
