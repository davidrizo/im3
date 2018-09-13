package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.layout.DiplomaticLayout;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.core.utils.FileUtils;
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
import java.util.*;

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
    //TODO ¿cómo codificamos las decisiones editoriales? - ver MEI 3.0, capítulos 10 y 11 - tener en cuenta que no haremos grandes cambios

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

    private String name;

    private String composer;

    OMRScore scores;


    /**
     * @param projectFolder In new files, if the project does not exist it will be created
     * @throws IM3Exception
     */
    public OMRProject(File projectFolder) {
        this.name = projectFolder.getName();
        this.projectFolder = projectFolder;
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        imagesFolder = new File(projectFolder, Project.IMAGES_FOLDER);
        imagesFolder.mkdir();

        imagesProperty = FXCollections.observableSet(new TreeSet<>());
        instruments = new OMRInstruments();
        this.comments = new SimpleStringProperty();
        this.scores = new OMRScore();
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
        // copy the image file into the imagesold folder
        File targetFile = new File(imagesFolder, file.getName());
        try {
            FileUtils.copy(file, targetFile);
        } catch (IOException e) {
            throw new IM3Exception("Cannot copy input file " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        }

        OMRImage page = new OMRImage(this, targetFile);
        int nextNumber = -1;
        if (imagesProperty.isEmpty()) {
            nextNumber = 1;
        } else {
            Iterator<OMRImage> iterator = imagesProperty.iterator(); // it does not return any order
            OMRImage lastImage = null;
            while (iterator.hasNext()) {
                lastImage = iterator.next();
            }
            nextNumber = Math.max(nextNumber, lastImage.getOrder());
        }
        page.setOrder(nextNumber+1);
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
        return name;
    }

    public String getProjectFolderName() {
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

    public ScoreSong getDiplomaticEdition() {
        return scores.getDiplomaticEdition();
    }

    public void setDiplomaticEdition(ScoreSong diplomaticEdition) {
        scores.setDiplomaticEdition(diplomaticEdition);
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
        Project pojoProject = new Project(ProjectVersion.v1, scores.getNotationType());
        pojoProject.setName(name);
        pojoProject.setComposer(composer);
        pojoProject.setComments(comments.get());

        for (OMRImage image: imagesProperty) {
            Image pojoImage = image.createPOJO();
            pojoProject.getImages().add(pojoImage);
        }
        return pojoProject;
    }

    public NotationType getNotationType() {
        return scores.getNotationType();
    }

    public void setNotationType(NotationType notationType) throws IM3Exception {

        scores.setNotationType(notationType);
    }


    public LayoutFont getManuscriptLayoutFont() {
        return scores.manuscriptLayoutFont;
    }

    /**
     * It returns a string with possible project problems
     * @return
     */
    public String checkIntegrity() {
        StringBuilder stringBuilder = new StringBuilder();
        for (OMRImage omrImage: imagesProperty) {
            for (OMRPage omrPage: omrImage.getPages()) {
                for (OMRRegion omrRegion: omrPage.regionsProperty()) {
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

    /**
     * It removes all imagesold from the set and adds these ones with a new ordering
     * @param images
     */
    public void replaceImages(ArrayList<OMRImage> images) {
        this.imagesProperty.clear();
        for (OMRImage image: images) {
            this.imagesProperty.add(image);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public DiplomaticLayout getDiplomaticLayout(OMRInstrument instrumentHierarchical, OMRRegion owner) throws IM3Exception {
        if (scores.diplomaticLayout == null) {
            if (scores.getDiplomaticEdition() == null) {
                //TODO translate symbols to music and reverse
                //TODO URGENT QUITAR
                MEISongImporter importer = new MEISongImporter();
                scores.setDiplomaticEdition(importer.importSong(new File("/Users/drizo/Desktop/harpa.mei")));

            }

            Collection<Staff> staves = new ArrayList<>();

            Staff instrumentStaff = null;
            /*TODO Dejar este bucle for (Staff staff: diplomaticEdition.getStaves()) {
                //TODO Equivalencia Instrumento nombre?
                //Mejor asociar a layer? - habría que poner en IMCore el nombre del instrumento
                if (Objects.equals(staff.getName(), instrumentHierarchical.getName())) {
                    instrumentStaff = staff;
                    break;
                }
            }*/
            instrumentStaff = scores.getDiplomaticEdition().getStaves().iterator().next();
            if (instrumentStaff == null) {
                instrumentStaff = new Pentagram(scores.getDiplomaticEdition(), "1", 1); //TODO hierarchical order!!!
                scores.getDiplomaticEdition().addStaff(instrumentStaff);
                ScorePart scorePart = scores.getDiplomaticEdition().addPart();
                scorePart.addStaff(instrumentStaff);

            }

            //TODO ¿Dónde? synchronizeAgnosticSemantic(owner, instrumentStaff);


            scores.diplomaticLayout = new DiplomaticLayout(scores.getDiplomaticEdition(), staves); //TODO Cuando es mensural que salga la versión moderna también (usar hashmap y conversor como en Patriarca)
            scores.diplomaticLayout.layout(false);

        }
        //TODO Que este layout sólo tenga el StaffSystem que corresponde con la región
        return scores.diplomaticLayout;
    }

    public void synchronizeAgnosticSemantic() throws ImportException {
        //TODO URGENT QUITAR
        MEISongImporter importer = new MEISongImporter();
        scores.setDiplomaticEdition(importer.importSong(new File("/Users/drizo/Documents/EASD.A/docencia/alicante-2017-2018/inv/imagenes_patriarca/PATRIARCA2017/patriarca.mei")));
    }

}
