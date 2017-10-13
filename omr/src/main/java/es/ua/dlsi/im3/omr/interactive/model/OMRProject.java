package es.ua.dlsi.im3.omr.interactive.model;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.mensural.BinaryDurationEvaluator;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.interactive.OMRController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OMRProject {
    static final String IMAGES_FOLDER = "images";
    /**
     * Used by GUI for binding
     */
    private ObservableList<OMRPage> pagesProperty;
    File projectFolder;
    File imagesFolder;
    File xmlFile;
    ScoreSong scoreSong;
    OMRController omrController;
    private ImitationLayout imitationLayout;
    private ScoreSongView scoreSongView;

    public OMRProject(File projectFolder, OMRController controller) throws IM3Exception {
        pagesProperty = FXCollections.observableArrayList();
        this.omrController = controller;
        this.projectFolder = projectFolder;
        this.xmlFile = new File(projectFolder, InputOutput.createXMLFilename(projectFolder));
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        imagesFolder = new File(projectFolder, IMAGES_FOLDER);
        imagesFolder.mkdirs();

        // FIXME: 11/10/17 Esto debe ser interactivo
        scoreSong = new ScoreSong(new BinaryDurationEvaluator(new Time(2)));
        createScoreView();
    }

    private void createScoreView() throws IM3Exception {
        //TODO Fonts
        imitationLayout = new ImitationLayout(scoreSong, LayoutFonts.capitan);
        scoreSongView = new ScoreSongView(scoreSong, imitationLayout);
    }

    public void addPage(File file) throws IM3Exception {
        // copy the image file into the images folder
        File targetFile = new File(imagesFolder, file.getName());
        try {
            FileUtils.copy(file, targetFile);
        } catch (IOException e) {
            throw new IM3Exception("Cannot copy input file " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        }

        OMRPage page = new OMRPage(this, imagesFolder, file.getName(), scoreSong);
        pagesProperty.add(page);
        // TODO: 11/10/17 Debe añadir una página al layout del scoreSOng
    }

    public void addPage(OMRPage page) {
        pagesProperty.add(page);
    }

    private void setProjectFolder(File projectFolder) {
        this.projectFolder = projectFolder;
        this.imagesFolder = new File(projectFolder, IMAGES_FOLDER);
    }

    public String getName() {
        return projectFolder.getName();
    }

    public ObservableList<OMRPage> pagesProperty() {
        return pagesProperty;
    }

    public void deletePage(OMRPage page) throws IM3Exception {
        pagesProperty.remove(page);
    }

    public File getProjectFolder() {
        return projectFolder;
    }

    public File getImagesFolder() {
        return imagesFolder;
    }

    public ScoreSong getScoreSong() {
        return scoreSong;
    }

    public OMRController getOMRController() {
        return omrController;
    }

    public ScoreSongView getScoreSongView() {
        return scoreSongView;
    }

    public ImitationLayout getImitationLayout() {
        return imitationLayout;
    }
}
