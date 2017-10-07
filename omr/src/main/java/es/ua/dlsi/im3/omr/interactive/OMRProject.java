package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScorePart;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.omr.interactive.components.ScoreImageFile;
import es.ua.dlsi.im3.omr.mensuralspanish.ISymbolRecognizer;
import es.ua.dlsi.im3.omr.mensuralspanish.MensuralSymbols;
import es.ua.dlsi.im3.omr.mensuralspanish.StringToMensuralSymbolFactory;
import es.ua.dlsi.im3.omr.mensuralspanish.SymbolRecognizerFactory;
import es.ua.dlsi.im3.omr.model.ScoreImageTagsFileParser;
import es.ua.dlsi.im3.omr.traced.BimodalDatasetReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project
 */
public class OMRProject {
    File trainingFile;
    private final ObservableList<ScoreImageFile> files;
    private ISymbolRecognizer recognizer;

    public OMRProject(File trainingFile) throws IM3Exception {
        this.trainingFile = trainingFile;
        files = FXCollections.observableArrayList();
        readTrainingFile(this.trainingFile);
    }

    private void readTrainingFile(File trainingFile) throws IM3Exception {
        // TODO: 7/10/17 Factory con tipos de símbolos
        BimodalDatasetReader<MensuralSymbols> reader = new BimodalDatasetReader<>();
        recognizer = SymbolRecognizerFactory.getInstance().buildRecognizer(getStaff(), reader, new StringToMensuralSymbolFactory());
        try {
            recognizer.learn(trainingFile);
        } catch (IOException e) {
            throw new IM3Exception("Cannot train", e);
        }

    }

    private Staff getStaff() throws IM3Exception {
        // TODO: 7/10/17
        ScoreSong song = new ScoreSong();
        Pentagram staff = new Pentagram(song, "1", 1);
        staff.setNotationType(NotationType.eMensural);
        ScorePart part = song.addPart();
        part.addStaff(staff);
        part.addScoreLayer(staff);
        return staff;
    }

    // TODO: 7/10/17 Copiar los ficheros al path del proyecto
    public void addImage(File imageFile) throws Exception {
        ScoreImageTagsFileParser parser = new ScoreImageTagsFileParser();
        ScoreImageFile sif = new ScoreImageFile(imageFile);
        File txtFile = new File(imageFile.getParentFile(), imageFile.getName() + ".txt");
        if (txtFile.exists()) {
            sif.addTagsFile(parser.parse(txtFile, sif.getBufferedImage()));
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Loaded tags file");
        }
        files.add(sif);
    }

    ObservableList<ScoreImageFile> filesProperty() {
        return files;
    }

    public String getTrainingModelSymbolCount() {
        return Long.toString(recognizer.getNumberOfTrainingSymbols());
    }
}
