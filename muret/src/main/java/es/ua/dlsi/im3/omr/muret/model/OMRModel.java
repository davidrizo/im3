package es.ua.dlsi.im3.omr.muret.model;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.patternmatching.NearestNeighbourClassesRanking;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowMessage;
import es.ua.dlsi.im3.omr.classifiers.symbolrecognition.*;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.Strokes;
import es.ua.dlsi.im3.omr.muret.OMRApp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OMRModel {
    ObjectProperty<OMRProject> currentProject;
    IBimodalSymbolFromImageDataAndStrokesRecognizer bimodalSymbolFromImageDataAndStrokesRecognizer;

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
        bimodalSymbolFromImageDataAndStrokesRecognizer = BimodalSymbolFromImageAndStrokesDataRecognizerFactory.getInstance().create(AgnosticVersion.v2, trainingFolder);

        int trainingSamples = bimodalSymbolFromImageDataAndStrokesRecognizer.getTrainingSetSize();
        if (trainingSamples == 0) {
            ShowError.show(OMRApp.getMainStage(),"The training set is empty, you can continue, but without automatic classification of symbols");
        } else {
            //TODO Flash message
            ShowMessage.show(OMRApp.getMainStage(), "Using " + trainingSamples + " samples to classify");
        }
        InputOutput io = new InputOutput();
        OMRProject project = io.load(projectFolder);
        currentProject.setValue(project);
    }

    public NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> classifySymbolFromImage(GrayscaleImageData grayScaleImage, Strokes strokes) throws IM3Exception {
        Instant t0 = Instant.now();
        NearestNeighbourClassesRanking<AgnosticSymbol, SymbolImageAndPointsPrototype> result = bimodalSymbolFromImageDataAndStrokesRecognizer.recognize(grayScaleImage, strokes);
        Instant t1 = Instant.now();
        long seconds = t1.getEpochSecond() - t0.getEpochSecond();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Seconds to classify {0}", seconds);
        return result;
    }
}
