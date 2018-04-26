package es.ua.dlsi.im3.omr.muret.model;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;

public class OMRModel {
    ObjectProperty<OMRProject> currentProject;

    public OMRModel() {
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

    public void createProject(File projectFolder, NotationType notationType) throws IM3Exception {
        OMRProject project = new OMRProject(projectFolder);
        project.setNotationType(notationType);
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
        OMRProject project = io.load(projectFolder);
        currentProject.setValue(project);
    }
}
