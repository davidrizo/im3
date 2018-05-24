package es.ua.dlsi.im3.omr.muret.model;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.GrayscaleImageData;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.ISymbolFromImageDataRecognizer;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.SymbolFromImageDataRecognizerFactory;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.List;

public class OMRModel {
    ObjectProperty<OMRProject> currentProject;
    ISymbolFromImageDataRecognizer symbolFromImageDataRecognizer;

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

    //TODO Generalizar, que no se pueda entrenar sólo de un directorio
    public void openProject(File projectFolder, File trainingFolder) throws IM3Exception {
        symbolFromImageDataRecognizer = SymbolFromImageDataRecognizerFactory.getInstance().create(trainingFolder);

        InputOutput io = new InputOutput();
        OMRProject project = io.load(projectFolder);
        currentProject.setValue(project);
    }

    public List<AgnosticSymbol> classifySymbolFromImage(GrayscaleImageData grayScaleImage) throws IM3Exception {
        return symbolFromImageDataRecognizer.recognize(grayScaleImage);
    }
}
