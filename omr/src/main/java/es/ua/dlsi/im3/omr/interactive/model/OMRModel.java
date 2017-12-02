package es.ua.dlsi.im3.omr.interactive.model;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;

import static com.sun.deploy.util.SessionState.save;

public class OMRModel {
    public static OMRModel instance = null;

    public static final OMRModel getInstance() {
        synchronized (OMRModel.class) {
            if (instance == null) {
                instance = new OMRModel();
            }
        }
        return instance;
    }

    ObjectProperty<OMRProject> currentProject;

    private OMRModel() {
        currentProject = new SimpleObjectProperty<>();
    }


    public OMRProject getCurrentProject() {
        return currentProject.get();
    }

    public void setCurrentProject(OMRProject currentProject) {
        this.currentProject.setValue(currentProject);
    }

    public ObjectProperty<OMRProject> currentProjectProperty() {
        return currentProject;
    }

    public void clearProject() {
        currentProject.setValue(null);
    }

    public void createProject(File projectFolder, File trainingFile) throws IM3Exception {
        OMRProject project = new OMRProject(projectFolder, trainingFile);
        currentProject.setValue(project);
        InputOutput io = new InputOutput();
        save(); // create structure
    }

    public void save() throws IM3Exception {
        if (currentProject.isNull().get()) {
            throw new IM3Exception("No current project");
        }

        InputOutput io = new InputOutput();
        io.save(currentProject.get());
    }

    public void openProject(File projectFolder, File trainingFile) throws IM3Exception {
        InputOutput io = new InputOutput();
        OMRProject project = io.load(projectFolder, trainingFile);
        currentProject.setValue(project);
    }
}
