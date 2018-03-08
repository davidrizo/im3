package es.ua.dlsi.im3.omr.old.mensuraltagger;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.MensuralToModern;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefC3;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.old.mensuraltagger.components.ScoreImageFile;
import es.ua.dlsi.im3.omr.mensuralspanish.ISymbolRecognizer;
import es.ua.dlsi.im3.omr.mensuralspanish.SymbolRecognizerFactory;
import es.ua.dlsi.im3.omr.model.ScoreImageTagsFileParser;
import es.ua.dlsi.im3.omr.model.Symbol;
import es.ua.dlsi.im3.omr.classifiers.traced.BimodalDatasetReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project
 */
public class OMRModel {
    File trainingFile;
    private final ObservableList<ScoreImageFile> files;
    private ISymbolRecognizer recognizer;
    ScoreSong song;
    Staff staff; // TODO: 8/10/17
    private Pentagram modernStaff;

    public OMRModel(File trainingFile) throws IM3Exception {
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
        MensuralToModern transducer = new MensuralToModern(null); // FIXME: 7/11/17 null
        modernStaff = new Pentagram(song, "2", 2);
        modernStaff.setNotationType(NotationType.eModern);
        song.addStaff(modernStaff);
        ScorePart modernPart = song.addPart();
        ScoreLayer modernLayer = modernPart.addScoreLayer(modernStaff);
        transducer.convertIntoStaff(staff, modernStaff, modernLayer, Intervals.UNISON_PERFECT, createModernClef(staff.getClefAtTime(Time.TIME_ZERO))); // FIXME: 7/11/17 null
    }

    // TODO: 8/11/17 Que salga de alguna regla
    private Clef createModernClef(Clef mensuralClef) {
        Clef modernClef = mensuralClef.clone();
        return modernClef;
    }

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

    public ArrayList<AgnosticSymbol> recognize(Symbol symbol) throws IM3Exception {
        ArrayList<AgnosticSymbol> result = new ArrayList<>();
        /*TODO ArrayList<AgnosticSymbol> recognized = recognizer.recognize(symbol);
        // add all symbols not recognized
        TreeSet<MensuralSymbols> symbols = new TreeSet<>();
        for (AgnosticSymbol positionedSymbolType : recognized) {
            symbols.add(positionedSymbolType.getSpecificSymbol());
            result.add(positionedSymbolType);
        }
        for (MensuralSymbols st: MensuralSymbols.values()) {
            if (!symbols.contains(st)) {
                result.add(new AgnosticSymbol(st, PositionsInStaff.LINE_3));
            }
        }
*/
        return result;
    }
 }
