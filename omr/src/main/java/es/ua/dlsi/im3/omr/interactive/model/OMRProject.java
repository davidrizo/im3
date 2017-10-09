package es.ua.dlsi.im3.omr.interactive.model;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OMRProject {
    static final String IMAGES_FOLDER = "images";
    /**
     * Used by GUI for binding
     */
    transient private ObservableList<OMRPage> pagesProperty;
    private List<OMRPage> pages;
    transient File projectFolder;
    transient File imagesFolder;
    transient File xmlFile;

    // Used for the persistor
    public OMRProject() throws IM3Exception {
    }

    public OMRProject(File projectFolder) throws IM3Exception {
        pagesProperty = FXCollections.observableArrayList();
        this.projectFolder = projectFolder;
        this.xmlFile = new File(projectFolder, createXMLFilename(projectFolder));
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        imagesFolder = new File(projectFolder, IMAGES_FOLDER);
        imagesFolder.mkdirs();
        save();
    }

    static String createXMLFilename(File projectFolder) {
        return FileUtils.getFileWithoutPath(projectFolder.getName()) + ".mrt";
    }

    private static XStream createXStream() {
        XStream xstream = new XStream();
        return xstream;
    }
    public static OMRProject load(File projectFolder) throws IM3Exception {
        XStream xStream = createXStream();
        File xmlFile = new File(projectFolder, createXMLFilename(projectFolder));
        OMRProject project = (OMRProject) xStream.fromXML(xmlFile);
        project.setProjectFolder(projectFolder);
        project.xmlFile = xmlFile;
        try {
            project.loadFiles();
        } catch (IM3Exception e) {
            throw new IM3Exception(e);
        }
        return project;
    }

    public void save() throws IM3Exception {
        XStream xStream = createXStream();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(xmlFile);
        } catch (FileNotFoundException e) {
            throw new IM3Exception(e);
        }
        pages = new ArrayList<>();
        pages.addAll(pagesProperty);
        xStream.toXML(this, fos);
    }

    public void addPage(File file) throws IM3Exception {
        // copy the image file into the images folder
        File targetFile = new File(imagesFolder, file.getName());
        try {
            FileUtils.copy(file, targetFile);
        } catch (IOException e) {
            throw new IM3Exception("Cannot copy input file " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        }

        OMRPage page = new OMRPage(imagesFolder, file.getName());
        pagesProperty.add(page);
        save();
    }

    /**
     * Used by the OMRInputOutput
     */
    void loadFiles() throws IM3Exception {
        pagesProperty = FXCollections.observableArrayList();
        for (OMRPage page: pages) {
            pagesProperty.add(page);
            page.loadImageFile(this.imagesFolder);
        }
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
        save();
    }
}
