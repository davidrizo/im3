package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.IProgressObserver;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Segment;
import es.ua.dlsi.im3.core.utils.SonoritySegmenter;
import es.ua.dlsi.im3.interactivepm.AccuracyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author drizo
 * @date 26/11/2011
 */
public abstract class MelodicAnalyzer {

    //2017 boolean recomputeFeatures;
    static final Logger logger = Logger.getLogger(MelodicAnalyzer.class.getName());
    protected AccuracyManager accuracyManager;
    int[][] confusionMatrix; // indexed by the ordinal of MelodicAnalysKind
    int[] errors;// indexed by the ordinal of MelodicAnalysKind
    double baseline = -1; // classify everything as harmonic
    /**
     * Used for merging classifiers
     */
    double authority = 1.0;

    /**
     * For online training
     */
    protected TreeSet<AtomPitch> manualChangedAnalyses;

    protected TreeSet<AtomPitch> prefixValidatedAnalyses;

    protected boolean propagate = true;
    private boolean onlineTraining = true;

    private List<AtomPitch> atomPitches;
    private ArrayList<Segment> sonorities;

    String name;
    protected boolean prefixValidation;


    /**
     * It must be constructed from TonalAnalyzer2011
     *
     * @param name
     */
    protected MelodicAnalyzer(String name) {
        this.name = name;
    }

    protected MelodicAnalyzer(String name, double authority) {
        this.name = name;
        this.authority = authority;
        init();
    }

    private void init() {
        manualChangedAnalyses = new TreeSet<>();
        prefixValidatedAnalyses = new TreeSet<>();
    }

    public boolean isPropagate() {
        return propagate;
    }

    public void setPropagate(boolean propagate) {
        this.propagate = propagate;
        logger.log(Level.INFO, "Using propagate: {0}", propagate);
    }

    public boolean isOnlineTraining() {
        return onlineTraining;
    }

    public void setOnlineTraining(boolean onlineTraining) {
        this.onlineTraining = onlineTraining;
        logger.log(Level.INFO, "Using online trainning: {0}", onlineTraining);
    }

    public double getAuthority() {
        return authority;
    }

    public void setAuthority(double authority) {
        this.authority = authority;
        logger.log(Level.INFO, "Using authority: {0}", authority);
    }

    public MelodicAnalysis melodicAnalysis(ScoreSong song, IProgressObserver o)
            throws MelodicAnalysisException {
        SonoritySegmenter sonoritySegmenter = new SonoritySegmenter();
        sonorities = sonoritySegmenter.segmentSonorities(song);
        atomPitches = song.getAtomPitches();
        MelodicAnalysis melodicAnalysis = melodicAnalysis(song, o, sonorities);
        return melodicAnalysis;
    }

    protected abstract MelodicAnalysis melodicAnalysis(ScoreSong song, IProgressObserver o, List<Segment> alreadyComputedSonorities)
            throws MelodicAnalysisException;


    /**
     * Try to use at most 3 chars
     *
     * @return
     */
    public abstract String getNameAbbr();

    public String getName() {
        return name;
    }

    //TODO
    public double computeSuccessRateAndInitAccuracyManager(ScoreSong song, MelodicAnalysis melodicAnalysis, MelodicAnalysis expectedAnalysis) {
        confusionMatrix = new int[MelodicAnalysisNoteKinds.values().length][MelodicAnalysisNoteKinds.values().length];
        errors = new int[MelodicAnalysisNoteKinds.values().length];
        long count = 0;
        long ok = 0;
        long countHarmonic = 0;
        for (AtomPitch n : atomPitches) {
            NoteMelodicAnalysis expected = expectedAnalysis.getAnalysis(n);
            NoteMelodicAnalysis found = melodicAnalysis.getAnalysis(n);
            if (expected != null) {
                count++;
                if (found != null) {
                    confusionMatrix[expected.getKind().ordinal()][found.getKind().ordinal()]++;
                    if (found.equals(expected)) {
                        ok++;
                    } else {
                        errors[expected.getKind().ordinal()]++;
                    }
                }

                if (expectedAnalysis.equals(MelodicAnalysisNoteKinds.HARMONIC)) {
                    countHarmonic++;
                }
            }

        }

        if (this.accuracyManager == null) {
            this.accuracyManager = new AccuracyManager(count, count - ok);
        } else {
            this.accuracyManager.setCurrentErrorCount(count - ok);
        }

        this.baseline = (double) countHarmonic / (double) count;
        return this.accuracyManager.getCurrentSuccessRate();
    }

    public void resetAccuracyManager() {
        this.accuracyManager = null;
        logger.log(Level.INFO, "Reseting accuracy manager");
    }

    public AccuracyManager getAccuracyManager() throws IM3Exception {
        if (this.accuracyManager == null) {
            throw new MelodicAnalysisException("Accuracy manager is null");
        }
        return accuracyManager;
    }

    public void addInteraction(MelodicAnalysisInteraction interaction) throws IM3Exception {
        if (accuracyManager != null) {
            this.getAccuracyManager().addInteraction(interaction);
            logger.log(Level.FINE, "Adding interaction {0}", interaction.toString());
        }
    }

    private void clearInteractions() throws IM3Exception {
        if (accuracyManager != null) {
            this.getAccuracyManager().clearInteractions();
            logger.log(Level.INFO, "Clearing interactions");
        }
    }

    /**
     * After analyzing the work, it tags non valid rules given the a priori
     * rules
     *
     * @throws IM3Exception
     *             This method is now in violatesConditionsFor in
     *             MelodicAnalyzerWeka
     */
    /*
     * public void refineAnalysisFromAPrioriRules() throws IM3Exception {
	 * MelodicAnalysisAPrioriRules mar = new MelodicAnalysisAPrioriRules();
	 * TreeSet<ScoreSoundingElement> notes = song.getAllScoreSoundingElements();
	 * for (ScoreSoundingElement n : notes) { //URGENT TODO Contar con los
	 * DIVISI if (n instanceof CMNNote) { if (!mar.meetsConditions(noteKind,
	 * instability, meterNumerator, prevInterval, nextInterval)) {
	 * 
	 * } } } }
	 */

    /**
     * It looks for melodic analyses that have the same features as the one
     * manually changed
     *
     * param song
     * param prefixValidation If true, previous notes are accepted as validated
     * param noteAnalysis  Pattern to be searched
     * @return Number of changes made
     * @throws IM3Exception
     * @throws MelodicAnalysisException
     */
    /*public int propagateChangeDecision(ScoreSong song, boolean prefixValidation, NoteMelodicAnalysis noteAnalysis)
            throws MelodicAnalysisException {
        if (propagate) {
            logger.log(Level.INFO, "Propagating change decision {0} using prefix validation",
                    (prefixValidation ? "" : "NOT"));

            int changes = 0;
            AtomPitch fromNote = noteAnalysis.getNote();
            NoteMelodicAnalysisFeatures fromNoteFeatures = noteAnalysis.getFeatures();
            int i = 0;
            for (AtomPitch scoreNote: sortedAtomPitches) {
                if (fromNote != scoreNote) {
                    if (!manualChangedAnalyses.contains(scoreNote)) {
                        if (prefixValidation && scoreNote.getTime().compareTo(fromNote.getTime()) < 0) {
                            prefixValidatedAnalyses.add(scoreNote);
                            logger.fine("Validating prefix");
                        } else {
                            NoteMelodicAnalysis currentScoreNoteAnalysis = melodicAnalysis.getAnalysis(scoreNote);
                            if (currentScoreNoteAnalysis == null) {
                                throw new MelodicAnalysisException("The note " + scoreNote + " did not have a previous melodic analysis");
                            }

                            NoteMelodicAnalysisFeatures scoreNoteFeatures = currentScoreNoteAnalysis.getFeatures();
                            if (fromNoteFeatures.hasSameSelectedFeatures(scoreNoteFeatures)) {
                                if (prefixValidation && currentScoreNoteAnalysis.getStateMachine().getState() == NoteMelodicAnalysisStateMachine.State.ePrefixAccepted
                                            && (scoreNote.getTime().compareTo(fromNote.getTime())>0)) {
                                        currentScoreNoteAnalysis.invalidatePrefixWithPropagation(noteAnalysis.getKind());
                                        logger.fine("Invalidating prefix with propagation");
                                    } else {
                                        currentScoreNoteAnalysis.setPropagatedMelodicAnalysis(noteAnalysis.getKind());
                                        this.addManualChange(scoreNote);
                                        logger.fine("Propagating, changing an analysis");
                                    }
                                    changes++;
                                } else if (prefixValidation && found.getState() == State.ePrefixAccepted) {
                                    found.invalidatePrefix();
                                    logger.fine("Invalidating prefix");
                                } else if (found.getState() != State.ePropagated) {
                                    found.invalidate();
                                    // logger.finest("Invalidating prefix");
                                } else {
                                    logger.fine("Not modifying, previously propagated");
                                }
                            }
                        }
                        // System.out.println("Note i=" + i + " " +
                        // scoreNote.toMinString() + " curr state " +
                        // found.getState());
                        // System.out.println();
                    }
                    i++;
                }
            }*/
            // TODO Urgent - comentado para Leuven 2014
			/*
			 * this.addInteraction(new MelodicAnalysisInteraction()); //TODO
			 * A�adir tipo de cambio melodicAnalysis(null); // recompute melodic
			 * analysis (only for non propagated or changed notes - see
			 * NoteMelodicAnalsisDecoration state machine)
			 * this.computeSuccessRateAndInitAccuracyManager();
			 */
            /*return changes;
        } else {
            logger.log(Level.INFO, "Not propagating change decision");
            return 0;
        }
    }*/

    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public int[] getErrors() {
        return errors;
    }

    public double getBaseline() {
        if (baseline == -1) {
            throw new IM3RuntimeException("Invoke computeSuccessRate() first");
        }
        return baseline;
    }

    public void setPrefixValidation(boolean selected) {
        this.prefixValidation = selected;
        logger.log(Level.INFO, "Setting prefix validation to: {0}", selected);
    }
}
