package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.fonts.PatriarcaFont;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.model.entities.Instrument;
import es.ua.dlsi.im3.omr.model.entities.Image;
import es.ua.dlsi.im3.omr.model.entities.Project;
import es.ua.dlsi.im3.omr.model.entities.ProjectVersion;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class contains the same content than the es.ua.dlsi.im3.omr.model.entities.Project class. However, it is adapted
 * to work with observable properties to make it easy the coordination with JavaFX
 */
public class OMRProject {
    /**
     * Actual project folder
     */
    File projectFolder;
    /**
     * Images inside the project folder
     */
    File imagesFolder;

    /**
     * Transcribed song
     */
    ScoreSong scoreSong;
    /**
     * Default notation type used for the staff creation
     */
    NotationType notationType;

    /**
     * Default layout font for manuscript
     */
    LayoutFont manuscriptLayoutFont;

    /**
     * Used by GUI for binding
     */
    ObservableSet<OMRImage> imagesProperty;

    /**
     * Global set of instruments
     */
    private OMRInstruments instruments;

    /**
     * Comments about the project
     */
    private StringProperty comments;

    /**
     * Last changed date
     */
    private Date lastChangedDate;

    /**
     * User name that changed it
     */
    private String changedBy;

    /**
     * @param projectFolder In new files, if the project does not exist it will be created
     * @throws IM3Exception
     */
    public OMRProject(File projectFolder) throws IM3Exception {
        this.projectFolder = projectFolder;
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        imagesFolder = new File(projectFolder, Project.IMAGES_FOLDER);
        imagesFolder.mkdir();

        imagesProperty = FXCollections.observableSet(new TreeSet<>());
        instruments = new OMRInstruments();
        this.comments = new SimpleStringProperty();
    }

    public String getComments() {
        return comments.get();
    }

    public StringProperty commentsProperty() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments.set(comments);
    }

    /**
     * It adds a new image file to the project
     * @param file
     * @return
     * @throws IM3Exception
     */
    public OMRImage addImage(File file) throws IM3Exception {
        // copy the image file into the images folder
        File targetFile = new File(imagesFolder, file.getName());
        try {
            FileUtils.copy(file, targetFile);
        } catch (IOException e) {
            throw new IM3Exception("Cannot copy input file " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        }

        OMRImage page = new OMRImage(this, targetFile);
        int nextNumber;
        if (imagesProperty.isEmpty()) {
            nextNumber = 1;
        } else {
            Iterator<OMRImage> iterator = imagesProperty.iterator();
            OMRImage lastImage = null;
            while (iterator.hasNext()) {
                lastImage = iterator.next();
            }
            nextNumber = lastImage.getOrder()+1;
        }
        page.setOrder(nextNumber);
        imagesProperty.add(page);
        // TODO: 11/10/17 Debe añadir una página al layout del scoreSOng
        return page;
    }

    public void addImage(OMRImage image) {
        imagesProperty.add(image);
    }

    private void setProjectFolder(File projectFolder) {
        this.projectFolder = projectFolder;
        this.imagesFolder = new File(projectFolder, Project.IMAGES_FOLDER);
    }

    public String getName() {
        return projectFolder.getName();
    }

    public ObservableSet<OMRImage> imagesProperty() {
        return imagesProperty;
    }

    public OMRInstruments getInstruments() {
        return instruments;
    }

    public void deleteImage(OMRImage image) throws IM3Exception {
        imagesProperty.remove(image);
        image.deleteFile();
    }

    public File getProjectFolder() {
        return projectFolder;
    }

    public File getImagesFolder() {
        return imagesFolder;
    }

    public URL getImagesFolderURL() {
        try {
            return imagesFolder.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IM3RuntimeException(e);
        }
    }

    public ScoreSong getScoreSong() {
        return scoreSong;
    }

    public void setScoreSong(ScoreSong scoreSong) {
        this.scoreSong = scoreSong;
    }

    /**
     * It gets or adds the instrument
     * @param name
     * @return
     */
    public OMRInstrument addInstrument(String name) {
        return instruments.addInstrument(name);
    }

    public Project createPOJO() throws IM3Exception {
        Project pojoProject = new Project(ProjectVersion.v1);
        pojoProject.setNotationType(notationType);
        pojoProject.setComments(comments.get());
        for (OMRInstrument instrument: instruments.getInstrumentSet()) {
            Instrument pojoInstrument = new Instrument(instrument.getName());
            pojoProject.getInstruments().add(pojoInstrument);
        }

        for (OMRImage image: imagesProperty) {
            Image pojoImage = image.createPOJO();
            pojoProject.getImages().add(pojoImage);
        }
        return pojoProject;
    }

    public NotationType getNotationType() {
        return notationType;
    }

    public void setNotationType(NotationType notationType) throws IM3Exception {
        this.notationType = notationType;
        if (notationType == NotationType.eMensural) {
            manuscriptLayoutFont = new PatriarcaFont();
        } else if (notationType == NotationType.eModern) {
            manuscriptLayoutFont = new BravuraFont();
        } else {
            throw new IM3Exception("Unuspported notation type '" + notationType + "'");
        }

    }


    public LayoutFont getManuscriptLayoutFont() {
        return manuscriptLayoutFont;
    }

    /**
     * It returns a string with possible project problems
     * @return
     */
    public String checkIntegrity() {
        StringBuilder stringBuilder = new StringBuilder();
        for (OMRImage omrImage: imagesProperty) {
            for (OMRPage omrPage: omrImage.getPages()) {
                for (OMRRegion omrRegion: omrPage.getRegions()) {
                    // check all symbols lie inside its assigned region
                    boolean symbolsOutsideRegionBoundingBox = false;
                    for (OMRSymbol omrSymbol: omrRegion.symbolsProperty()) {
                        if (!omrRegion.containsAbsoluteCoordinate(omrSymbol.getCenterX(), omrSymbol.getCenterY())) {
                            symbolsOutsideRegionBoundingBox = true;
                            break;
                        }
                    }
                    if (symbolsOutsideRegionBoundingBox) {
                        stringBuilder.append(omrImage.toString() + ", region " + omrRegion + " contains symbols outside bounding box");
                        stringBuilder.append('\n');
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    public Date getLastChangedDate() {
        return lastChangedDate;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setLastChangedDate(Date lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}
