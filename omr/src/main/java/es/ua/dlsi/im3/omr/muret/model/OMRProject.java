package es.ua.dlsi.im3.omr.muret.model;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.fonts.PatriarcaFont;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.score.ScoreSongView;
import es.ua.dlsi.im3.omr.IGraphicalToScoreSymbolFactory;
import es.ua.dlsi.im3.omr.mensuralspanish.*;
import es.ua.dlsi.im3.omr.model.pojo.Instrument;
import es.ua.dlsi.im3.omr.model.pojo.Page;
import es.ua.dlsi.im3.omr.model.pojo.Project;
import es.ua.dlsi.im3.omr.classifiers.traced.BimodalDatasetReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class OMRProject {
    /**
     * Where images will be stored inside the project folder
     */
    static final String IMAGES_FOLDER = "images";

    File projectFolder;
    File imagesFolder;

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
    private ObservableList<OMRPage> pagesProperty;
    private ObservableSet<OMRInstrument> instrumentsProperty;


    //TODO No sé si esto de bajo se usa
    //OMRMainController omrController;
    private ISymbolRecognizer recognizer;
    private ImitationLayout imitationLayout;
    private ScoreSongView scoreSongView;
    IGraphicalToScoreSymbolFactory graphicalToScoreSymbolFactory;
    private File trainingFile;


    //IPageSegmenter pageSegmenter;


    public OMRProject(File projectFolder, File trainingFile) throws IM3Exception {
        //this.omrController = controller;
        this.projectFolder = projectFolder;
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        imagesFolder = new File(projectFolder, IMAGES_FOLDER);
        imagesFolder.mkdirs();

        pagesProperty = FXCollections.observableArrayList();
        instrumentsProperty = FXCollections.observableSet();

        scoreSong = new ScoreSong();

        //-- POR AQUI
        /*TODO YA

        // TODO: 9/11/17 Debemos poder parametrizar esto, que pueda ser también moderno
        graphicalToScoreSymbolFactory = new MensuralGraphicalToScoreSymbolFactory();
        pageSegmenter = new DummyPageSegmenter();

        this.trainingFile = trainingFile;
        readTrainingFile(this.trainingFile);

        // FIXME: 11/10/17 Esto debe ser interactivo
        //TODO el tipo de notationType debe ser como el del proyecto
        scoreSong = new ScoreSong(new BinaryDurationEvaluator(new Time(2)));
        createScoreView();*/
    }


    private void createScoreView() throws IM3Exception {
        //TODO Fonts
       // imitationLayout = new ImitationLayout(scoreSong, LayoutFonts.capitan);
        // scoreSongView = new ScoreSongView(scoreSong, imitationLayout);
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

    /*public OMRMainController getOMRController() {
        return omrController;
    }*/

    public ScoreSongView getScoreSongView() {
        return scoreSongView;
    }

    public ImitationLayout getImitationLayout() {
        return imitationLayout;
    }

    // TODO No lo estamos usando
    private void readTrainingFile(File trainingFile) throws IM3Exception {
        // TODO: 7/10/17 Factory con tipos de símbolos
        BimodalDatasetReader reader = new BimodalDatasetReader();
        recognizer = SymbolRecognizerFactory.getInstance().buildRecognizer(reader);
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
        pojoProject.setNotationType(notationType);
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
}
