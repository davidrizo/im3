package es.ua.dlsi.im3.omr.muret;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowMessage;
import es.ua.dlsi.im3.omr.classifiers.endtoend.AgnosticSequenceRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.BimodalSymbolFromImageAndStrokesDataRecognizerFactory;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.GrayscaleImageData;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.IBimodalSymbolFromImageDataAndStrokesRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.SymbolImageAndPointsPrototype;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import es.ua.dlsi.im3.omr.muret.model.InputOutput;
import es.ua.dlsi.im3.omr.muret.model.OMRProject;
import es.ua.dlsi.im3.omr.muret.old.OMRApp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {
    Classifiers classifiers;

    ObjectProperty<OMRProject> currentProject;

    public Model() {
        currentProject = new SimpleObjectProperty<>();
        classifiers = new Classifiers();
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

    public void openProject(File projectFolder) throws IM3Exception {
        InputOutput io = new InputOutput();
        OMRProject project = io.load(projectFolder);
        currentProject.setValue(project);
    }

    public Classifiers getClassifiers() {
        return classifiers;
    }
}
