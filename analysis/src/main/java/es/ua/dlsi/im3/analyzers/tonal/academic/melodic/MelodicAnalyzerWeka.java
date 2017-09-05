package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.analyzers.tonal.TonalAnalysis;
import es.ua.dlsi.im3.analyzers.tonal.TonalAnalysisException;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.IProgressObserver;
import es.ua.dlsi.im3.core.score.*;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

/**
 * @author drizo
 * @date 26/11/2011
 */
public class MelodicAnalyzerWeka extends MelodicAnalyzerMachineLearning {

    Logger logger = Logger.getLogger(MelodicAnalyzerWeka.class.getName());

    private Attribute midiNOTE; // for debugging

    private Attribute prevIntervalName;
    private Attribute prevIntervalMode;
    private Attribute prevIntervalDir;
    private Attribute nextIntervalName;
    private Attribute nextIntervalMode;
    private Attribute nextIntervalDir;
    private Attribute tied;
    // 20160314 private Attribute figureAndDots;
    private Attribute ratio;
    private Attribute instability;
    private Attribute nextInstability;
    // private Attribute instabilityRatio;
    private Attribute prevInstability;
    // private Attribute onsetInTime;

    private Attribute belongsToChord;
    private Attribute prevBelongsToChord;
    private Attribute nextBelongsToChord;
    //private Attribute belongsToKey;
    private Attribute prevNoteMelodicTag;
    private Attribute nextNoteMelodicTag;

    private Attribute meterNumerator;
    private FastVector attrs;
    private Attribute melodictag;
    // Class<? extends Classifier> classifierClass;
    private Classifier classifier;
    MelodicAnalysisNoteKinds[] classes;
    /*
     * FastVector createValidIntervalValues() { FastVector fv = new
	 * FastVector<String>(); for (int i=0; i<8; i++) { fv.add(new
	 * Integer(i).toString()); } return fv; }
	 */

    private boolean resample;
    private Instances learningData;
    // private boolean learnt;
    public static boolean debug = false;
    private boolean checkPosterioriConditions;
    private boolean analysisContextFeatures;
    private boolean meterFeatures;
    private boolean harmonicFeatures;

    private Instances dataToClassify;

    private EClassificationMode classificationMode;

    // public MelodicAnalyzerWeka(CMNSong song, String name, Class<? extends
    // Classifier> classifierClass, boolean resample) {
    public MelodicAnalyzerWeka(String name, Classifier classifier, boolean resample, double authority,
                               boolean checkPosterioriConditions, EClassificationMode classificationMode) {
        super(name, authority);
        this.classificationMode = classificationMode;
        this.resample = resample;
        // this.classifierClass = classifierClass;
        this.classifier = classifier;
        this.checkPosterioriConditions = checkPosterioriConditions;
        this.initAttributes();
    }

    public MelodicAnalyzerWeka(String name, Classifier classifier, boolean resample, double authority,
                               boolean checkPosterioriConditions, boolean harmonicFeatures, boolean meterFeatures,
                               boolean analysisContextFeatures, EClassificationMode classificationMode) {
        super(name, authority);
        this.classificationMode = classificationMode;
        this.resample = resample;
        // this.classifierClass = classifierClass;
        this.classifier = classifier;
        this.checkPosterioriConditions = checkPosterioriConditions;
        this.harmonicFeatures = harmonicFeatures;
        this.meterFeatures = meterFeatures;
        this.analysisContextFeatures = analysisContextFeatures;
        this.initAttributes();
    }

    public void setCheckPosterioriConditions(boolean b) {
        this.checkPosterioriConditions = b;
    }

    public boolean isResample() {
        return resample;
    }

    public void setResample(boolean resample) {
        this.resample = resample;
    }

    public boolean isAnalysisContextFeatures() {
        return analysisContextFeatures;
    }

    public void setAnalysisContextFeatures(boolean analysisContextFeatures) {
        this.analysisContextFeatures = analysisContextFeatures;
    }

    public boolean isMeterFeatures() {
        return meterFeatures;
    }

    public void setMeterFeatures(boolean meterFeatures) {
        this.meterFeatures = meterFeatures;
    }

    public boolean isHarmonicFeatures() {
        return harmonicFeatures;
    }

    public void setHarmonicFeatures(boolean harmonicFeatures) {
        this.harmonicFeatures = harmonicFeatures;
    }

    FastVector createValidIntervalModes() {
        FastVector fv = new FastVector();
        for (IntervalMode m : IntervalMode.values()) {
            fv.addElement(m.name());
        }
        return fv;
    }

    FastVector createValidIntervalDirs() {
        FastVector fv = new FastVector();
        for (MotionDirection m : MotionDirection.values()) {
            fv.addElement(m.name());
        }
        return fv;
    }

    FastVector createValidBooleanValues() {
        FastVector fv = new FastVector();
        fv.addElement(Boolean.toString(true));
        fv.addElement(Boolean.toString(false));
        return fv;
    }

    final boolean valueToBoolean(Instance instance, Attribute attr) {
        return instance.value(attr) == 0; // value 0 is the index of the element
        // in boolean values
    }

    // TODO Ver sincronizaci�n de MelodicAnalyzerFeaturesExtractor con esto
    void initAttributes() {
        // Attribute prevInterval = new Attribute("prevInterval",
        // createValidIntervalValues());
        prevIntervalName = new Attribute(MelodicFeatures.PREV_INTERVAL_NAME.name());
        prevIntervalMode = new Attribute(MelodicFeatures.PREV_INTERVAL_MODE.name(), createValidIntervalModes());
        prevIntervalDir = new Attribute(MelodicFeatures.PREV_INTERVAL_DIR.name(), createValidIntervalDirs());
        // Attribute nextInterval = new Attribute("nextInterval",
        // createValidIntervalValues());
        nextIntervalName = new Attribute(MelodicFeatures.NEXT_INTERVAL_NAME.name());
        nextIntervalMode = new Attribute(MelodicFeatures.NEXT_INTERVAL_MODE.name(), createValidIntervalModes());
        nextIntervalDir = new Attribute(MelodicFeatures.NEXT_INTERVAL_DIR.name(), createValidIntervalDirs());
        tied = new Attribute(MelodicFeatures.TIED_FROM_PREVIOUS.name(), createValidBooleanValues());
        // 20160314 figureAndDots = new Attribute(MelodicFeatures.DURATION.name());
        ratio = new Attribute(MelodicFeatures.RATIO.name());
        instability = new Attribute(MelodicFeatures.INSTABILITY.name());
        nextInstability = new Attribute(MelodicFeatures.NEXT_INSTABILITY.name());
        // instabilityRatio = new
        // Attribute(MelodicFeatures.INSTABILITY_RATIO.name());

        // onsetInTime = new Attribute(MelodicFeatures.ONSET_IN_TIME.name(),
        // createValidBooleanValues());
        // meterNumerator.setWeight(0); // we don't want it to be used to
        // classify
        attrs = new FastVector();
        Attribute[] attributesArray = new Attribute[]{prevIntervalName, prevIntervalMode, prevIntervalDir,
                nextIntervalName, nextIntervalMode, nextIntervalDir, tied,
                // 20160314 figureAndDots,
                ratio, instability, nextInstability};// , onsetInTime}; //,
        // instabilityRatio,
        // meterNumerator};
        for (Attribute attribute : attributesArray) {
            attrs.addElement(attribute);
        }
		/*
		 * if (debug) { midiNOTE = new
		 * Attribute(MelodicFeatures.MIDI_NOTE.name());
		 * attrs.addElement(midiNOTE); }
		 */

        if (meterFeatures) {
            meterNumerator = new Attribute(MelodicFeatures.METER_NUMERATOR.name());
            attrs.addElement(meterNumerator);
        }

        // nextIntervalDir, tied, figureAndDots, ratio, instability, nextInstability,
        // instabilityRatio, meterNumerator}));
        // Add class attribute.

        final FastVector classValues = new FastVector();
        classes = new MelodicAnalysisNoteKinds[MelodicAnalysisNoteKinds.values().length - 1];
        int i = 0;
        for (MelodicAnalysisNoteKinds ma : MelodicAnalysisNoteKinds.values()) {
            if (ma != MelodicAnalysisNoteKinds.TOBECHANGED) {
                classValues.addElement(ma.getAbbreviation());
                classes[i] = ma;
                i++;
            }
        }

        if (harmonicFeatures) {
            belongsToChord = new Attribute(MelodicFeatures.BELONGS_TO_CHORD.name());
            nextBelongsToChord = new Attribute(MelodicFeatures.NEXT_BELONGS_TO_CHORD.name());
            prevBelongsToChord = new Attribute(MelodicFeatures.PREV_BELONGS_TO_CHORD.name());
            // 20160601 belongsToKey = new
            // Attribute(MelodicFeatures.BELONGS_TO_KEY.name());
            attrs.addElement(belongsToChord);
            //attrs.addElement(belongsToKey);
        }

        if (analysisContextFeatures) {
            prevNoteMelodicTag = new Attribute(MelodicFeatures.PREVIOUS_NOTE_TAG.name(), classValues);
            nextNoteMelodicTag = new Attribute(MelodicFeatures.NEXT_NOTE_TAG.name(), classValues);

            attrs.addElement(prevNoteMelodicTag);
            attrs.addElement(nextNoteMelodicTag);
        }

        melodictag = new Attribute(MelodicFeatures.EXPECTED_ANALYSIS.name(), classValues);
        attrs.addElement(melodictag);

    }

    private void buildInstances(Instances data, ScoreSong lsong, List<Segment> sonorities, HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features) throws MelodicAnalysisException {
        ArrayList<AtomPitch> notes = lsong.getAtomPitches();
        for (AtomPitch n : notes) {
            AtomPitch scoreNote = (AtomPitch) n;
            NoteMelodicAnalysisFeatures noteFeatures = features.get(scoreNote);
            Instance instanceWithoutHarmonies = buildInstance(lsong, sonorities, scoreNote, noteFeatures, data, true);
            if (instanceWithoutHarmonies != null) {
                instanceWithoutHarmonies.setDataset(data);
                data.add(instanceWithoutHarmonies);
                logger.log(Level.FINEST, "Adding instance {0}", instanceWithoutHarmonies.toString());
            }
        }
    }

    private Instance buildInstance(ScoreSong song, List<Segment> sonorities, AtomPitch note, NoteMelodicAnalysisFeatures features, Instances dataset, boolean setClassValue)
            throws MelodicAnalysisException {
        //NoteMelodicAnalysisFeatures features = Utils.getComputedMelodicAnalysis(scoreNote).getFeatures();
        //NoteMelodicAnalysisFeatures features = featuresExtractor.computeFeatures(song, sonorities, currentAnalysis);
        if (features != null) {
            if (setClassValue && features.getExpectedAnalysis() == null) {
                logger.log(Level.WARNING, "Trying to add instance without class value {0}, skipping it ",
                        note.toString() + " in song " + song.toString());
                return null;
            } else {
                // Instance instance = new Instance(attrs.size() + 1);
                return buildInstance(dataset, setClassValue, features);
            }
        } else {
            return null;
        }
    }

    public Instance buildInstance(Instances dataset, boolean setClassValue, NoteMelodicAnalysisFeatures features) {
        int size = attrs.size();
        Instance instance = new DenseInstance(size); // since 3.7
        // Instance is
        // abstract - in
        // 3.6 we added
        // another
        // attribute ¿?
        instance.setValue(prevIntervalName, features.getPrevIntervalName());
        instance.setValue(prevIntervalMode, features.getPrevIntervalMode().toString());
        instance.setValue(prevIntervalDir, features.getPrevIntervalDir().toString());
        instance.setValue(nextIntervalName, features.getNextIntervalName());
        instance.setValue(nextIntervalMode, features.getNextIntervalMode().toString());
        instance.setValue(nextIntervalDir, features.getNextIntervalDir().toString());
        instance.setValue(tied, Boolean.toString(features.isTied()));
        // 20160314 instance.setValue(figureAndDots, features.getDuration());
        instance.setValue(ratio, features.getRatio());
        // instance.setValue(previnstability,
        // features.getInstability());
        instance.setValue(instability, features.getInstability());
        instance.setValue(nextInstability, features.getNextInstability());
        // instance.setValue(instabilityRatio,
        // features.getInstabilityRatio());
        // instance.setValue(onsetInTime,
        // Boolean.toString(features.isOnsetInTime()));
        if (meterFeatures) {
            instance.setValue(meterNumerator, features.getMeterNumerator()); // it
            // is
            // not
            // used
            // here
        }

        if (debug) {
            instance.setValue(midiNOTE, features.getMidiNote());
        }
        if (harmonicFeatures) {
            //instance.setValue(belongsToKey, features.getBelongsToKey());
            if (features.isContainsChordInformation()) {
                instance.setValue(belongsToChord, features.getBelongsToChord());
                instance.setValue(prevBelongsToChord, features.getPrevBelongsToChord());
                instance.setValue(nextBelongsToChord, features.getNextBelongsToChord());
            }
        }

        if (analysisContextFeatures) {
            if (features.getPreviousNoteAnalysis() != null) {
                instance.setValue(prevNoteMelodicTag, features.getPreviousNoteAnalysis().getAbbreviation());
            }
            if (features.getNextNoteAnalysis() != null) {
                instance.setValue(nextNoteMelodicTag, features.getNextNoteAnalysis().getAbbreviation());
            }
        }
        instance.setDataset(dataset);
        if (setClassValue) {
            instance.setClassValue(features.getExpectedAnalysis().getAbbreviation());
        }
        return instance;
    }

    @Override
    public String getNameAbbr() {
        return "Weka";
    }

    private MelodicAnalysisNoteKinds classify(ScoreSong song, Instance instance,
                                              MelodicAnalysisNoteKinds previousInstanceAnalysis, AtomPitch note) throws Exception {
        if (classifier == null) {
            throw new Exception("The classifier is null!!!");
        }
        if (instance == null) {
            throw new Exception("The instance is null!!!");
        }
        double[] distribution = classifier.distributionForInstance(instance);
        if (distribution == null || distribution.length == 0) {
            logger.log(Level.INFO, "Not classified");
            return MelodicAnalysisNoteKinds.NONE;
        } else {
			/*
			 * System.out.println("------PROBABILIDADES WEKA ???? ----"); for
			 * (double e : distribution) { System.out.print(e + "\t"); }
			 * System.out.println("------");
			 */
            int imax = -1;
            double max = Double.MIN_VALUE;
            for (int i = 0; i < distribution.length; i++) {
                boolean violates = false;
                if (checkPosterioriConditions) {
                    violates = violatesConditionsFor(song, classes[i], instance, previousInstanceAnalysis, note);
                }

                if ((imax == -1 || distribution[i] > max) && !violates) {
                    max = distribution[i];
                    imax = i;
                }
            }
            // return
            // MelodicAnalysisNoteKinds.abbrToMelodicAnalysis(melodictag.value(imax));
            if (imax == -1) {
                logger.log(Level.INFO, "Not classified, none meets conditions");
                return MelodicAnalysisNoteKinds.NONE;
            } else {
                return classes[imax];
            }
        }
        // double result = classifier.classifyInstance(instance);
    }


    public boolean isCheckPosterioriConditions() {
        return checkPosterioriConditions;
    }

    // TODO �Moverlo a NoteMelodicAnalysisDecoration?

    /**
     * @param noteKind
     * @param instance
     * @param prev     previousInstanceAnalysis
     * @return
     */
    private boolean violatesConditionsFor(ScoreSong song, MelodicAnalysisNoteKinds noteKind, Instance instance,
                                          MelodicAnalysisNoteKinds prev, AtomPitch note) throws IM3Exception {
        //TODO 2017
        return false;
		/*if (debug) {
			int midiNote = note.getScientificPitch().computeMidiPitch();
			System.out.println("MIDI " + midiNote);
		}
		Meter ts = song.getActiveMeterAtTime(note.getTime());
		float beatFloat = ts.getBeat(note.getTime());
		boolean isOnsetInTime = (beatFloat == ((float) (int) beatFloat)); // if
																			// it
																			// has
																			// not
																			// decimals

		int linstability = (int) instance.value(this.instability);
		int lnextInstability = (int) instance.value(this.nextInstability);
		int lprevIntervalName = (int) instance.value(this.prevIntervalName);
		int lnextIntervalName = (int) instance.value(this.nextIntervalName);
		// boolean isOnsetInTime = valueToBoolean(instance, this.onsetInTime);
		if (harmonicFeatures && !instance.isMissing(this.belongsToChord)) {
			float belongsDegree = (float) instance.value(this.belongsToChord);
			// if (belongsDegree != 0.0) { // it is 0.0 when not used
			if (!isOnsetInTime && belongsDegree < ScaleMembershipDegree.medium.getValue()
					&& noteKind == MelodicAnalysisNoteKinds.HARMONIC) {
				logger.info("A note in weak beat not belonging to chord cannot be HT");
				return true; // a note not belonging to chord cannot be HT
			} else if (belongsDegree == ScaleMembershipDegree.high.getValue()
					&& noteKind != MelodicAnalysisNoteKinds.HARMONIC) {
				logger.info("A note belonging to chord cannot be NHT");
				return true; // a note belonging to chord cannot be NHT
			}
			// }
		}

		// TODO Test unitario de esto
		boolean ok = true;
		switch (noteKind) {
		case PASSING_TONE:
			ok = (prev != MelodicAnalysisNoteKinds.APPOGIATURA && prev != MelodicAnalysisNoteKinds.SUSPENSION
					&& prev != MelodicAnalysisNoteKinds.ANTICIPATION
					&& prev != MelodicAnalysisNoteKinds.NEIGHBOUR_TONE);
			break;
		case NEIGHBOUR_TONE:
			ok = (prev != MelodicAnalysisNoteKinds.APPOGIATURA && prev != MelodicAnalysisNoteKinds.SUSPENSION
					&& prev != MelodicAnalysisNoteKinds.ANTICIPATION && prev != MelodicAnalysisNoteKinds.PASSING_TONE);
			break;
		case ANTICIPATION:
		case APPOGIATURA:
		case SUSPENSION:
			ok = (prev != MelodicAnalysisNoteKinds.APPOGIATURA && prev != MelodicAnalysisNoteKinds.SUSPENSION
					&& prev != MelodicAnalysisNoteKinds.ANTICIPATION && prev != MelodicAnalysisNoteKinds.PASSING_TONE
					&& prev != MelodicAnalysisNoteKinds.NEIGHBOUR_TONE);
			break;
		}

		// System.out.println("PREVIO: " + prev + ", este " + noteKind + ",
		// viola=" + (!ok));
		if (!ok) {
			logger.info("Violation occurred");
			return true; // a violation has occurred
		}

		// TODO (*) Quitar estas reglas y probar
		switch (noteKind) {
		case PASSING_TONE:
			return (lprevIntervalName >= 3 && lprevIntervalName <= 6)
					|| (lnextIntervalName >= 3 && lnextIntervalName <= 6);
		case NEIGHBOUR_TONE:
			return (lprevIntervalName >= 4 && lprevIntervalName <= 7)
					|| (lnextIntervalName >= 4 && lnextIntervalName <= 7);
		case SUSPENSION:
			return linstability >= lnextInstability
					&& (lprevIntervalName != 1 && (lnextIntervalName >= 3 && lnextIntervalName <= 7));
		case APPOGIATURA:
			return linstability >= lnextInstability && (lnextIntervalName >= 3 && lnextIntervalName <= 7);
		default:
			return false;
		}*/
    }

    /**
     * If this rule is met, we don't need to classify using any classifier
     *
     * @param noteKind
     * @param instance
     * @return
     */
    private boolean meetsBasicSimpleRule(MelodicAnalysisNoteKinds noteKind, Instance instance) {
        // return true;
        if (meterFeatures) {
            int mn = (int) instance.value(this.meterNumerator);
            int ins = (int) instance.value(this.instability);
            int pi = (int) instance.value(this.prevIntervalName);
            int ni = (int) instance.value(this.nextIntervalName);
            switch (noteKind) {
                case HARMONIC:
                    return (ins <= mn && (pi >= 3 || ni >= 3));
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void initLearning() {
        try {
            learningData = new Instances("Data1", attrs, 0); // TODO �?
            learningData.setClassIndex(learningData.numAttributes() - 1);
            ///// basket classifier.buildClassifier(learningData); // a default
            ///// empty classifier
        } catch (Exception ex) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new IM3RuntimeException(ex);
        }
    }

    @Override
    protected void readTrainingFeatures(ScoreSong lsong, List<Segment> sonorities, HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features, TonalAnalysis tonalAnalysis) throws IM3Exception {
        try {
            if (features.isEmpty()) {
                throw new IM3Exception("There are no computed features for training");
            }
            buildInstances(learningData, lsong, sonorities, features);
        } catch (MelodicAnalysisException ex) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new IM3Exception(ex);
        }
    }

    @Override
    protected void learnWithReadTrainingFeatures() throws MelodicAnalysisException {
        // Train classifier.
        try {
            // classifier = classifierClass.newInstance();

            // resample data
            if (resample) {
                Resample resampleFilter = new Resample();
                resampleFilter.setBiasToUniformClass(1);
                resampleFilter.setSampleSizePercent(100);
                resampleFilter.setNoReplacement(false);
                resampleFilter.setInvertSelection(false);
                resampleFilter.setRandomSeed(0);
                resampleFilter.setInputFormat(learningData);
                Instances newData = Filter.useFilter(learningData, resampleFilter);
				/*
				 * resampleFilter.setInputFormat(data); logger.info(
				 * "Resampling with " + resampleFilter.toString() +
				 * ", source input #instances = " + data.size()); for (int i =
				 * 0; i < data.numInstances(); i++) {
				 * resampleFilter.input(data.instance(i)); }
				 * resampleFilter.batchFinished(); Instances newData =
				 * resampleFilter.getOutputFormat(); Instance processed; int []
				 * vals = new int[20]; while ((processed =
				 * resampleFilter.output()) != null) { newData.add(processed);
				 * vals[(int) processed.classValue()]++; } for (int ii=0;
				 * ii<vals.length; ii++) { System.out.println("Clase #" +
				 * vals[ii]); } logger.info("Resampling with " +
				 * resampleFilter.toString() + ", output #instances = " +
				 * newData.size()); logger.info("Training with " +
				 * classifier.getClass().getName());
				 */
                learningData = newData;
            }
            classifier.buildClassifier(learningData);

			/*2017 if (classifier instanceof JRipLocal) {
				JRipLocal jripLocal = (JRipLocal) classifier; // TODO Poder
																// hacerlo con
																// los dem�s
																// tambi�n
				FastVector rules = jripLocal.getRuleset();
			}*/
        } catch (Exception e) {
            throw new MelodicAnalysisException(e);
        }
    }

    /**
     * Used to speed up process
     */
    public void initInstancesObjectUsedToClassify() {
        dataToClassify = new Instances("Data2", attrs, 0); // TODO �?
        dataToClassify.setClassIndex(dataToClassify.numAttributes() - 1);
    }

    public HashMap<MelodicAnalysisNoteKinds, Double> analyzeNote(NoteMelodicAnalysisFeatures features) throws TonalAnalysisException {
        if (dataToClassify == null) {
            throw new TonalAnalysisException("Invoke initInstancesObjectUsedToClassify first");
        }
        Instance instance = buildInstance(dataToClassify, false, features);
        try {
            double[] distribution = classifier.distributionForInstance(instance);
            HashMap<MelodicAnalysisNoteKinds, Double> result = new HashMap<>();
            for (int i = 0; i < distribution.length; i++) {
                result.put(classes[i], distribution[i]);
            }
            return result;
        } catch (Exception e) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, "Cannot classify", e);
            throw new TonalAnalysisException(e);
        }

    }

    @Override
    protected MelodicAnalysis doMelodicAnalysis(ScoreSong song, List<Segment> sonorities, HashMap<AtomPitch, NoteMelodicAnalysisFeatures> features, IProgressObserver o) throws MelodicAnalysisException {
        MelodicAnalysis melodicAnalysis = new MelodicAnalysis(song); //TODO 2017 ¿esto no debería venir del tonal analysis que hemos construido antes?
        if (learningData == null) {
            initLearning(); // for systems built from scratch
        }

        Instances dataToClassify = new Instances("Data2", attrs, 0); // TODO �?
        dataToClassify.setClassIndex(dataToClassify.numAttributes() - 1);
        long maxProgress;
        try {
            maxProgress = (long) song.getNumDurationalSymbols();
        } catch (IM3Exception ex) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new MelodicAnalysisException(ex);
        }

        long progress = 0;

        for (ScorePart part : song.getParts()) {
            for (ScoreLayer layer : part.getLayers()) {
                TreeSet<AtomPitch> notes = layer.getAtomPitchesSortedByTime();
                MelodicAnalysisNoteKinds previousInstanceAnalysis = MelodicAnalysisNoteKinds.NONE;
                AtomPitch lastCMNNote = null; // URGENT TODO Contar con los DIVISI
                for (AtomPitch n : notes) {
                    try {
                        NoteMelodicAnalysisFeatures noteFeatures = features.get(n);
                        Instance instance = buildInstance(song, sonorities, n, noteFeatures, dataToClassify, false);
                        //Instance instance = buildInstance(song, scoreNote, dataToClassify, false);

                        MelodicAnalysisNoteKinds ma = MelodicAnalysisNoteKinds.NONE;
                        Confidence confidence = Confidence.INDETERMINATE;
                        boolean found = false;
                        String rule = "";

                        //2017 NoteMelodicAnalysisDecoration propagatedMelodicAnalysis = Utils.getComputedMelodicAnalysis(scoreNote);

                        // System.out.println("Note i=" + i + " " +
                        // scoreNote.toMinString() + " prev state " +
                        // found.getState());
						/*if (propagatedMelodicAnalysis.isManuallyChangedOrPropagated()) {
							found = true;
							ma = propagatedMelodicAnalysis.getManualOrPropagatedAnalysis().getKind();
							rule = propagatedMelodicAnalysis.getManualOrPropagatedAnalysis().getRule();
						} else {
							for (int ii = 0; ii < classes.length; ii++) {
								if (this.meetsBasicSimpleRule(classes[ii], instance)) {
									found = true;
									ma = classes[ii];
									rule = "Basic simple rules";
									confidence = Confidence.SURE;
								}
							}
						}*/
                        if (!found) {
                            ma = classify(song, instance, previousInstanceAnalysis, n);
                            if (ma != MelodicAnalysisNoteKinds.NONE) {
                                // code written using inverse engineering from
                                // JRip weka code (distributionForInstance)
                                // because we have not found the way to extract
                                // the rule
                                found = false;
                                // System.out.println(">>>>>>>>"+classifier.getRuleset().size());
                                // System.out.println("\t" +
                                // classifier.toString());

								/*
								 * for (int i=0;
								 * i<MelodicAnalysisNoteKinds.values().length;
								 * i++) { System.out.println("---->" +
								 * MelodicAnalysisNoteKinds.values()[i].toString
								 * ()); RuleStats rs =
								 * classifier.getRuleStats(i); FastVector rules
								 * = rs.getRuleset(); StringBuilder sb = new
								 * StringBuilder(); for(int k=0; k<rules.size();
								 * k++){ double[] simStats =
								 * rs.getSimpleStats(k);
								 * sb.append(((RipperRule)rules.elementAt(k)).
								 * toString(dataToClassify.classAttribute()) +
								 * " ("+simStats[0]+"/"+simStats[4]+")\n"); }
								 * rule = sb.toString();
								 * 
								 * System.out.println("\t" + rule); }
								 */
								/*2017 if (classifier instanceof JRipLocal) {
									JRipLocal jripLocal = (JRipLocal) classifier; // TODO
																					// Poder
																					// hacerlo
																					// con
																					// los
																					// dem�s
																					// tambi�n

									for (int i = 0; !found && i < jripLocal.getRuleset().size(); i++) {
										Rule jrule = (Rule) (jripLocal.getRuleset().elementAt(i));
										if (jrule.covers(instance)) {
											found = true;
											// System.out.println("Rule found: "
											// + rule.toString());

											rule = ((RipperRule) jrule).toString(dataToClassify.classAttribute());
										}
									}
								} else if (classifier instanceof J48) {
									J48 j48 = (J48) classifier;
								}*/
                            }
                        }

                        // JRipNoteMelodicAnalysisDecoration dec = new
                        // JRipNoteMelodicAnalysisDecoration(this, scoreNote,
                        // rule, instance);
                        // dec.addComputedMelodicAnalysis(new
                        // NoteMelodicAnalysis(ma, rule, confidence, ""));
                        // //TODO - Rule
						/*Utils.getComputedMelodicAnalysis(scoreNote).addComputedMelodicAnalysis(this,
								new NoteMelodicAnalysis(this, ma, rule, confidence, "")); // TODO
																							// -
																							// Rule*/


                        NoteMelodicAnalysis nma = new NoteMelodicAnalysis(n, this, noteFeatures, ma, rule, confidence, "");
                        melodicAnalysis.addAnalysis(n, nma);
                        lastCMNNote = n;
                        previousInstanceAnalysis = ma;
                        // scoreNote.addDecoration(dec);
                        // TODO//,
                        // classifier.distributionForInstance(instance));
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new MelodicAnalysisException(e);
                    }
                    if (o != null) {
                        o.setCurrentProgress(progress++, maxProgress);
                    }
                }
            }
        }
        return melodicAnalysis;
    }

    // see http://weka.wikispaces.com/Serialization
    @Override
    public void saveLearntModel(File file) throws MelodicAnalysisException {
        try {
            // serialize model
            weka.core.SerializationHelper.write(new FileOutputStream(file), classifier);
        } catch (Exception ex) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new MelodicAnalysisException(ex);
        }
    }

    /**
     * @param file
     * @throws MelodicAnalysisException
     */
    @Override
    public void loadLearntModel(File file) throws MelodicAnalysisException {
        try {
            // deserialize model
            classifier = (Classifier) weka.core.SerializationHelper.read(new FileInputStream(file));
            this.learnt = true;
        } catch (Exception ex) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new MelodicAnalysisException(ex);
        }
    }

    /**
     * We save the instances instead of the trained model because we need to be
     * able to use them later to increase the training dataset
     *
     * @param file
     */
    public void loadInstances(File file) throws MelodicAnalysisException {
        try {
            learningData = (Instances) weka.core.SerializationHelper.read(new FileInputStream(file));
            learnWithReadTrainingFeatures();
            learnt = true;
        } catch (Exception ex) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new MelodicAnalysisException(ex);
        }
    }

    /**
     * We save the instances instead of the trained model because we need to be
     * able to use them later to increase the training dataset
     *
     * @param file
     */
    public void saveInstances(File file) throws MelodicAnalysisException {
        try {
            // serialize model
            weka.core.SerializationHelper.write(new FileOutputStream(file), learningData);
        } catch (Exception ex) {
            Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
            throw new MelodicAnalysisException(ex);
        }
    }

	/*@Override
	protected void applyManualChanges() throws MelodicAnalysisException {
		super.applyManualChanges();

		if (learningData == null) {
			initLearning();
			try {
				classifier.buildClassifier(learningData);
			} catch (Exception ex) {
				Logger.getLogger(MelodicAnalyzerWeka.class.getName()).log(Level.SEVERE, null, ex);
				throw new MelodicAnalysisException(ex);
			}
		}

		ArrayList<Instance> newInstances = new ArrayList<>();

		// retrain the system with adding the new instances
		// TODO See buildInstances!!!!
		try {
			for (AtomPitch scoreNote : this.manualChangedAnalyses) {
				ScoreSong lsong = (ScoreSong) scoreNote.getVoice().getPart().getScoreSong();
				Instance inst = buildInstance(lsong, scoreNote, learningData, true);
				if (inst != null) {
					newInstances.add(inst);
					logger.log(Level.FINEST, "Adding NEW instance {0}", inst.toString());
				}
			}

			UpdateableClassifier uc;
			if (classifier instanceof UpdateableClassifier) {
				logger.log(Level.INFO, "Updating classifier with {0} new instances", newInstances.size());
				uc = (UpdateableClassifier) classifier;
				for (Instance newInstance : newInstances) {
					newInstance.setDataset(learningData);
					learningData.add(newInstance);
					uc.updateClassifier(newInstance);
				}
			} else {
				for (Instance newInstance : newInstances) {
					newInstance.setDataset(learningData);
					learningData.add(newInstance);
				}
				logger.log(Level.INFO,
						"Completely retraining classifier with {0} new instances, total instances are {1}",
						new Object[] { newInstances.size(), learningData.size() });
				classifier.buildClassifier(learningData);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error retraining", e);
			throw new MelodicAnalysisException(e);
		}

	}*/

}
