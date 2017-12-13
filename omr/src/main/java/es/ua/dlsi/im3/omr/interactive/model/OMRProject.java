package es.ua.dlsi.im3.omr.interactive.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.mensural.BinaryDurationEvaluator;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.IGraphicalToScoreSymbolFactory;
import es.ua.dlsi.im3.omr.interactive.OMRMainController;
import es.ua.dlsi.im3.omr.mensuralspanish.*;
import es.ua.dlsi.im3.omr.model.pojo.Instrument;
import es.ua.dlsi.im3.omr.model.pojo.Page;
import es.ua.dlsi.im3.omr.model.pojo.Project;
import es.ua.dlsi.im3.omr.segmentation.DummyPageSegmenter;
import es.ua.dlsi.im3.omr.segmentation.IPageSegmenter;
import es.ua.dlsi.im3.omr.traced.BimodalDatasetReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.io.File;
import java.io.IOException;

public class OMRProject {
    /**
     * Where images will be stored inside the project folder
     */
    static final String IMAGES_FOLDER = "images";

    private File trainingFile;
    File projectFolder;
    File imagesFolder;
    File xmlFile;


    ScoreSong scoreSong;
    //OMRMainController omrController;
    private ISymbolRecognizer recognizer;
    private ImitationLayout imitationLayout;
    private ScoreSongView scoreSongView;
    IGraphicalToScoreSymbolFactory graphicalToScoreSymbolFactory;
    IPageSegmenter pageSegmenter;

    /**
     * Used by GUI for binding
     */
    private ObservableList<OMRPage> pagesProperty;
    private ObservableSet<OMRInstrument> instrumentsProperty;


    public OMRProject(File projectXMLFile) throws IM3Exception {
        //TODO - loading
    }

    public OMRProject(File projectFolder, File trainingFile) throws IM3Exception {
        //this.omrController = controller;
        this.projectFolder = projectFolder;
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        imagesFolder = new File(projectFolder, IMAGES_FOLDER);
        imagesFolder.mkdirs();
        this.xmlFile = new File(projectFolder, InputOutput.createXMLFilename(projectFolder));

        pagesProperty = FXCollections.observableArrayList();
        instrumentsProperty = FXCollections.observableSet();

        //-- POR AQUI
        /*TODO YA

        // TODO: 9/11/17 Debemos poder parametrizar esto, que pueda ser también moderno
        graphicalToScoreSymbolFactory = new MensuralGraphicalToScoreSymbolFactory();
        pageSegmenter = new DummyPageSegmenter();

        this.trainingFile = trainingFile;
        readTrainingFile(this.trainingFile);

        // FIXME: 11/10/17 Esto debe ser interactivo
        scoreSong = new ScoreSong(new BinaryDurationEvaluator(new Time(2)));
        createScoreView();*/
    }

    private void createScoreView() throws IM3Exception {
        //TODO Fonts
        imitationLayout = new ImitationLayout(scoreSong, LayoutFonts.capitan);
        scoreSongView = new ScoreSongView(scoreSong, imitationLayout);
    }

    public OMRPage addPage(File file) throws IM3Exception {
        // copy the image file into the images folder
        File targetFile = new File(imagesFolder, file.getName());
        try {
            FileUtils.copy(file, targetFile);
        } catch (IOException e) {
            throw new IM3Exception("Cannot copy input file " + file.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        }

        OMRPage page = new OMRPage(this, imagesFolder, file.getName(), scoreSong);
        int nextNumber;
        if (pagesProperty.isEmpty()) {
            nextNumber = 1;
        } else {
            nextNumber = pagesProperty.get(pagesProperty.size()-1).getOrder()+1;
        }
        page.setOrder(nextNumber);
        pagesProperty.add(page);
        // TODO: 11/10/17 Debe añadir una página al layout del scoreSOng
        return page;
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

    public ObservableSet<OMRInstrument> instrumentsProperty() {
        return instrumentsProperty;
    }

    public void deletePage(OMRPage page) throws IM3Exception {
        pagesProperty.remove(page);
        File targetFile = new File(imagesFolder, page.getImageRelativeFileName());
        if (!targetFile.delete()) {
            throw new IM3Exception("Cannot remove file " + targetFile.getAbsolutePath() + " from disk");
        }
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

    /*public OMRMainController getOMRController() {
        return omrController;
    }*/

    public ScoreSongView getScoreSongView() {
        return scoreSongView;
    }

    public ImitationLayout getImitationLayout() {
        return imitationLayout;
    }

    private void readTrainingFile(File trainingFile) throws IM3Exception {
        // TODO: 7/10/17 Factory con tipos de símbolos
        BimodalDatasetReader<MensuralSymbols> reader = new BimodalDatasetReader<>();
        recognizer = SymbolRecognizerFactory.getInstance().buildRecognizer(reader, new StringToMensuralSymbolFactory());
        try {
            recognizer.learn(trainingFile);
        } catch (IOException e) {
            throw new IM3Exception("Cannot train", e);
        }
    }

    public ISymbolRecognizer getRecognizer() {
        return recognizer;
    }

    public IGraphicalToScoreSymbolFactory getGraphicalToScoreSymbolFactory() {
        return graphicalToScoreSymbolFactory;
    }

    public OMRInstrument addInstrument(String name) {
        OMRInstrument instrument = new OMRInstrument(name);
        instrumentsProperty.add(instrument);
        return instrument;
    }

    public OMRInstrument findInstrument(String name) throws IM3Exception {
        for (OMRInstrument instrument: instrumentsProperty) {
            if (instrument.getName().equals(name)) {
                return instrument;
            }
        }
        throw new IM3Exception("Instrument " + name + " not found");
    }

    public Project createPOJO() {
        Project pojoProject = new Project();
        for (OMRInstrument instrument: instrumentsProperty()) {
            Instrument pojoInstrument = new Instrument(instrument.getName());
            pojoProject.getInstruments().add(pojoInstrument);
        }

        for (OMRPage page: pagesProperty()) {
            Page pojoPage = page.createPOJO();
            pojoProject.getPages().add(pojoPage);
        }
        return pojoProject;
    }
}
