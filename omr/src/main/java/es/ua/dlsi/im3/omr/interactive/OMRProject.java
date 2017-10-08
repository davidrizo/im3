package es.ua.dlsi.im3.omr.interactive;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefC1;
import es.ua.dlsi.im3.core.score.clefs.ClefC3;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.gui.score.ScoreSongView;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project
 */
public class OMRProject {
    File trainingFile;
    private final ObservableList<ScoreImageFile> files;
    private ISymbolRecognizer recognizer;
    ScoreSong song;
    Staff staff; // TODO: 8/10/17
    private Pentagram modernStaff;

    public OMRProject(File trainingFile) throws IM3Exception {
        this.trainingFile = trainingFile;
        createSong();
        files = FXCollections.observableArrayList();
        readTrainingFile(this.trainingFile);
    }

    private void createSong() throws IM3Exception {
        song = new ScoreSong();
        staff = new Pentagram(song, "1", 1);
        staff.setNotationType(NotationType.eMensural);
        song.addStaff(staff);
        ScorePart part = song.addPart();
        part.addStaff(staff);
        ScoreLayer layer = part.addScoreLayer(staff);

        // FIXME: 8/10/17 Esto es trampa :) Tengo que quitarlo ya
        staff.addClef(new ClefC3());
        staff.addTimeSignature(new TimeSignatureCommonTime(NotationType.eMensural));

        ArrayList<SingleFigureAtom> atoms = new ArrayList<>();
        atoms.add(new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.A, 3)));
        atoms.add(new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.A, 3)));
        atoms.add(new SimpleNote(Figures.SEMIBREVE, 1, new ScientificPitch(PitchClasses.A, 3)));
        atoms.add(new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.D, 4)));
        atoms.add(new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.C_SHARP, 4)));
        atoms.add(new SimpleNote(Figures.MINIM, 0, new ScientificPitch(PitchClasses.D, 4)));
        atoms.add(new SimpleNote(Figures.SEMIBREVE, 0, new ScientificPitch(PitchClasses.A, 3)));
        for (SingleFigureAtom a: atoms) {
            layer.add(a);
            staff.addCoreSymbol(a);
        }

        /// -----
        MensuralToModern transducer = new MensuralToModern();
        modernStaff = new Pentagram(song, "2", 2);
        modernStaff.setNotationType(NotationType.eModern);
        song.addStaff(modernStaff);
        ScorePart modernPart = song.addPart();
        ScoreLayer modernLayer = modernPart.addScoreLayer(modernStaff);
        transducer.convertIntoStaff(staff, modernStaff, modernLayer);
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

    public Staff getStaff() {
        // TODO: 7/10/17
        return staff;
    }

    public Staff getModernStaff() {
        return modernStaff;
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

    public ScoreSong getSong() {
        return song;
    }

    ObservableList<ScoreImageFile> filesProperty() {
        return files;
    }

    public String getTrainingModelSymbolCount() {
        return Long.toString(recognizer.getNumberOfTrainingSymbols());
    }
}
