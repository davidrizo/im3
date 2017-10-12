package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.omr.interactive.OMRController;
import es.ua.dlsi.im3.omr.old.mensuraltagger.components.ScoreImageFile;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OMRPage {
    /**
     * Used for unit tests
     */
    static boolean SKIP_JAVAFX = false;
    private final File imagesFolder;
    private final OMRProject omrProject;
    /**
     * File name relative to project path
     */
    String imageRelativeFileName;
    File imageFile;
    List<OMRStaff> staves;

    /**
     * Used for the GUI
     */
    private ObservableObjectValue<Image> image;
    /**
     * Used to extract pixels from it
     */
    BufferedImage bufferedImage;

    ScoreSong scoreSong;

    public OMRPage(OMRProject omrProject, File imagesFolder, String imageRelativeFileName, ScoreSong scoreSong) throws IM3Exception {
        this.imagesFolder = imagesFolder;
        this.omrProject = omrProject;
        this.imageRelativeFileName = imageRelativeFileName;
        loadImageFile();
        staves = new ArrayList<>();
        this.scoreSong = scoreSong;
    }

    void loadImageFile() throws IM3Exception {
        this.imageFile = new File(imagesFolder, imageRelativeFileName);
        if (!imageFile.exists()) {
            throw new IM3Exception("The image file " + imageFile.getAbsolutePath() + " does not exist");
        }

        Image img = null;
        try {
            if (!SKIP_JAVAFX) {
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

    public void addStaff(ToggleGroup staffToggleGroup, int leftTopX, int leftTopY, int bottomRightX, int bottomRightY) throws IM3Exception {
        OMRStaff staff = new OMRStaff(this, scoreSong, leftTopX, leftTopY, bottomRightX, bottomRightY);
        staves.add(staff);
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

    public void addStaff(OMRStaff staff) {
        staves.add(staff);
    }

    public String getImageRelativeFileName() {
        return imageRelativeFileName;
    }


    /**
     * Hide all staves but shown
     * @param shownStaff
     */
    public void onStaffShown(OMRStaff shownStaff) {
        for (OMRStaff staff: staves) {
            if (staff != shownStaff) {
                staff.selectedProperty().set(false);
            }


        }
    }

    public OMRController getOMRController() {
        return omrProject.getOMRController();
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
