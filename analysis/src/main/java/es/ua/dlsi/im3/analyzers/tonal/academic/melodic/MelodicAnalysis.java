package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.MelodicFunction;
import es.ua.dlsi.im3.core.score.ScoreSong;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * The result of a melodic analysis
 * Created by drizo on 13/6/17.
 */
public class MelodicAnalysis {
    ScoreSong song;

    HashMap<AtomPitch, NoteMelodicAnalysis> noteAnalyses;

    public MelodicAnalysis(ScoreSong song) {
        this.song = song;
        noteAnalyses = new HashMap<>();
    }

    public void addAnalysis(AtomPitch note, NoteMelodicAnalysis analysis) {
        // if ID is not generated, generate it
        if (note.__getID() == null) {
            song.getIdManager().assignNextID(note);
        }
        noteAnalyses.put(note, analysis);
    }

    public NoteMelodicAnalysis getAnalysis(AtomPitch note) {
        return noteAnalyses.get(note);
    }

    public void print() {
        TreeSet<AtomPitch> sortedAtomPitches = new TreeSet<>(AtomPitch.TIME_COMPARATOR);
        for (AtomPitch ap: noteAnalyses.keySet()) {
            sortedAtomPitches.add(ap);
        }

        for (AtomPitch ap: sortedAtomPitches) {
            NoteMelodicAnalysis a = noteAnalyses.get(ap);
            System.out.println("AtomPitch: " + ap + " --> " + a.getKind() + ", using rule " + a.getRule() + ", comment: " + a.getComment());
            if (a != null) {
                for (HashMap.Entry<MelodicFeatures, Comparable> f : a.getFeatures().getFeatures().entrySet()) {
                    System.out.println("\t" + f.getKey() + " = " + f.getValue());
                }
            } else {
                System.out.println("\tNo analysis");
            }
            System.out.println();
        }
    }

    //TODO Hay que ver cómo podemos guardar más información (regla, ....)
    /**
     * It puts the melodic analysis into the AtomPitch melodicFunction attribute
     */
    public void putInSong() {
        for (AtomPitch ap: song.getAtomPitches()) {
            NoteMelodicAnalysis nma = noteAnalyses.get(ap);
            if (nma != null) {
                MelodicFunction mf = MelodicAnalysisNoteKinds.melodicAnalysisKindToMelodicFunction(nma.getKind());
                if (mf != null) {
                    ap.setMelodicFunction(mf);
                }
            }
        }
    }
    private static final String LOADED_FROM_FILE = "Loaded from file";
    /**
     * It loads the melodic analysis froom the AtomPitch melodicFunction attribute
     */
    public void loadFromSong() {
        noteAnalyses.clear();
        for (AtomPitch ap: song.getAtomPitches()) {
            MelodicFunction mf = ap.getMelodicFunction();
            if (mf != null) {
                MelodicAnalysisNoteKinds ak = MelodicAnalysisNoteKinds.melodicFunctionToMelodicAnalysisKind(mf);
                if (ak != null) {
                    noteAnalyses.put(ap, new NoteMelodicAnalysis(ap, ak, LOADED_FROM_FILE));
                }
            }
        }
    }

    public void saveToSong() {
        for (Map.Entry<AtomPitch, NoteMelodicAnalysis> entry: noteAnalyses.entrySet()) {
            entry.getKey().setMelodicFunction(entry.getValue().getKind().toMelodicFunction());
        }
    }

    public HashMap<AtomPitch, NoteMelodicAnalysis> getNoteAnalyses() {
        return noteAnalyses;
    }
}
