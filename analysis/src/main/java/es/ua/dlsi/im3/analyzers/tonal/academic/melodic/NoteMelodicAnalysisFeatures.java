package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.Interval;
import es.ua.dlsi.im3.core.score.IntervalMode;
import es.ua.dlsi.im3.core.score.MotionDirection;

import java.util.HashMap;
import java.util.Set;

import java.util.logging.Logger;

/**
 * Features used to classify a note in a melodic analysis
 *
 * @author drizo
 * @date 29/11/2011
 *
 */
public class NoteMelodicAnalysisFeatures {

	private static final int MAX_SEMITONES = 0;

	Logger logger = Logger.getLogger(NoteMelodicAnalysisFeatures.class.getName());

	protected MelodicAnalysisNoteKinds expectedAnalysis;
	/**
	 * If null, all features are used
	 */
	private Set<MelodicFeatures> selectedFeatures;

	protected HashMap<MelodicFeatures, Comparable> features;

	protected Interval prevInterval;
	protected Interval nextInterval;
	protected boolean tied;
	protected double duration;
	protected int instability;
	protected double ratio;
	protected int noteIndex;
	private int meterNumerator;
	private int nextInstability;

	private int midiNote; // for debug

	// DMA
	double beatFloat;
	int beatOffset;
	double nextDur;
	int intervalName;
	int nextIntervalName;
	double prevDur;
	boolean chromatism;
	boolean nextChromatism;

	int quarterPart; // starting from 1
	int pitchDistance;
	int nextPitchDistance;

	private boolean strong; // strong respect the surroundings

	private double instabilityRatio;
	// DMA End

	float belongsToChord;
	float prevBelongsToChord;
	float nextBelongsToChord;
	//float belongsToKey;
	MelodicAnalysisNoteKinds previousNoteAnalysis;
	MelodicAnalysisNoteKinds nextNoteAnalysis;
	private boolean containsChordInformation;
	private boolean onsetinTime;


	public NoteMelodicAnalysisFeatures(AtomPitch note, int noteIndex, MelodicAnalysisNoteKinds expectedAnalysis, Interval prevInterval, Interval nextInterval,
									   boolean tied, double duration, double ratio, int instability, int nextInstability, int meterNumerator,
									   // FROM DMA
									   boolean isStrong, int pitchDistance2, int nextPitchDistance2, double beatFloat2, boolean chromatism2,
									   boolean nextChromatism2, int quarterPart, double instabilityRatio, int midiNote,
									   Set<MelodicFeatures> selectedFeatures) throws MelodicAnalysisException {
		super();
		this.expectedAnalysis = expectedAnalysis;
		this.containsChordInformation = false;
		this.selectedFeatures = selectedFeatures;
		features = new HashMap<>();
		this.noteIndex = noteIndex;
		this.prevInterval = prevInterval;
		addFeature(MelodicFeatures.PREV_INTERVAL_NAME, prevInterval);
		addFeature(MelodicFeatures.PREV_INTERVAL_DIR, prevInterval.getDirection());
		addFeature(MelodicFeatures.PREV_INTERVAL_MODE, prevInterval.getMode());
		this.nextInterval = nextInterval;
		addFeature(MelodicFeatures.NEXT_INTERVAL_NAME, nextInterval);
		addFeature(MelodicFeatures.NEXT_INTERVAL_DIR, nextInterval.getDirection());
		addFeature(MelodicFeatures.NEXT_INTERVAL_MODE, nextInterval.getMode());

		this.tied = tied;
		addFeature(MelodicFeatures.TIED_FROM_PREVIOUS, tied);
		this.duration = duration;
		// 20160314 addFeature(MelodicFeatures.DURATION, figureAndDots);
		this.ratio = ratio;
		addFeature(MelodicFeatures.RATIO, ratio);
		this.instability = instability;
		addFeature(MelodicFeatures.INSTABILITY, instability);
		this.nextInstability = nextInstability;
		addFeature(MelodicFeatures.NEXT_INSTABILITY, nextInstability);

		this.instabilityRatio = instabilityRatio;
		// 20160314 addFeature(MelodicFeatures.INSTABILITY_RATIO,
		// instabilityRatio);

		if (note == null) {
			throw new MelodicAnalysisException("The note is null");
		}
		this.meterNumerator = meterNumerator;
		addFeature(MelodicFeatures.METER_NUMERATOR, meterNumerator);
		// addFeature(MelodicFeatures.EXPECTED_ANALYSIS, this.expectedAnalysis);

		// From DMA
		this.strong = isStrong;
		// 20160314 addFeature(MelodicFeatures.STRONG, strong);
		this.pitchDistance = pitchDistance2;
		// 20160314 addFeature(MelodicFeatures.PITCH_DISTANCE, pitchDistance);
		this.nextPitchDistance = nextPitchDistance2;
		// 20160314 addFeature(MelodicFeatures.NEXT_PITCH_DISTANCE,
		// nextPitchDistance);
		this.beatFloat = beatFloat2;
		// 20160314 addFeature(MelodicFeatures.BEAT_FLOAT, beatFloat);
		this.chromatism = chromatism2;
		// 20160314 addFeature(MelodicFeatures.CHROMATISM, chromatism);
		this.nextChromatism = nextChromatism2;
		// 20160314 addFeature(MelodicFeatures.NEXT_CHROMATISM, nextChromatism);
		this.quarterPart = quarterPart;
		// 20160314 addFeature(MelodicFeatures.QUARTER_PART, quarterPart);
		// System.out.println("Used features:" + features.toString() + " vs.
		// selected " + selectedFeatures.toString());

		// this.onsetinTime = onsetinTime;
		// addFeature(MelodicFeatures.ONSET_IN_TIME, onsetinTime);
		this.midiNote = midiNote;
		addFeature(MelodicFeatures.MIDI_NOTE, midiNote);
	}

	private void addFeature(MelodicFeatures mf, Comparable value) { // TODO Test
																	// unitario
		if (selectedFeatures == null || selectedFeatures.contains(mf)) { // if
																			// null,
																			// all
																			// are
																			// used
			if (mf == null) {
				throw new IM3RuntimeException("Cannot add a null feature");
			}
			if (value == null) {
				throw new IM3RuntimeException("Cannot add a null feature value");
			}
			features.put(mf, value);
		}
	}

	/**
	 * Package visibility for NoteMelodicAnalysisDecorationForILP
	 *
	 * @return interval_mode, direction
	 */
	String buildIntervalStringForILP(Interval interval) {
		return interval.getName() + "_" + interval.getMode() + "," + interval.getDirection().name().toLowerCase();
	}

	public String getPrevIntervalStringForILP() {
		return ((prevInterval == null) ? "0P,withrest" : buildIntervalStringForILP(prevInterval));
	}

	public int getPrevIntervalName() {
		return (prevInterval == null) ? 0 : prevInterval.getName();
	}

	public IntervalMode getPrevIntervalMode() {
		return (prevInterval == null) ? IntervalMode.UNDEFINED : prevInterval.getMode();
	}

	public MotionDirection getPrevIntervalDir() {
		return (prevInterval == null) ? MotionDirection.UNDEFINED : prevInterval.getDirection();
	}

	public String getNextIntervalStringForILP() {
		return ((nextInterval == null) ? "0P,withrest" : buildIntervalStringForILP(nextInterval));
	}

	public int getNextIntervalName() {
		return (nextInterval == null) ? 0 : nextInterval.getName();
	}

	public IntervalMode getNextIntervalMode() {
		return (nextInterval == null) ? IntervalMode.UNDEFINED : nextInterval.getMode();
	}

	public MotionDirection getNextIntervalDir() {
		return (nextInterval == null) ? MotionDirection.UNDEFINED : nextInterval.getDirection();
	}

	public boolean isTied() {
		return tied;
	}

	/**
	 * @return the figureAndDots
	 */
	public final double getDuration() {
		return duration;
	}

	/**
	 * @return the instability
	 */
	public final int getInstability() {
		return instability;
	}

	/**
	 * @return the expectedAnalysis
	 */
	public final MelodicAnalysisNoteKinds getExpectedAnalysis() {
		return expectedAnalysis;
	}

	/**
	 * @return the ratio
	 */
	public final double getRatio() {
		return ratio;
	}

	public int getMeterNumerator() {
		return meterNumerator;
	}

	public HashMap<MelodicFeatures, Comparable> getFeatures() {
		return features;
	}

	public int getNextInstability() {
		return nextInstability;
	}

	/**
	 * @return the noteIndex
	 */
	public final int getNoteIndex() {
		return noteIndex;
	}

	////// DMA
	public void setBeatFloat(float beatFloat) {
		this.beatFloat = beatFloat;
		float f = beatFloat - (int) beatFloat;
		beatOffset = (int) (f * 10);
		// this.addFeature(MelodicFeatures.BEAT_FLOAT, beatFloat);
		// this.addFeature(MelodicFeatures.BEAT, getBeat());
		// this.addFeature(MelodicFeatures.BEAT_OFFSET, getBeatOffset());
	}

	public int getBeat() {
		return 1 + (int) beatFloat;
	}

	/**
	 * @return the beatOffset
	 */
	public final int getBeatOffset() {
		return beatOffset;
	}

	/**
	 * @return the intervalName
	 */
	public final int getIntervalName() {
		return intervalName;
	}

	/**
	 * @param intervalName
	 *            the intervalName to set
	 */
	public final void setIntervalName(int intervalName) {
		this.intervalName = intervalName;
		// addFeature(MelodicFeatures.NEXT_INTERVAL_NAME, intervalName);
	}

	/**
	 * @return the nextInterval
	 */
	public final Interval getNextInterval() {
		return nextInterval;
	}

	/**
	 * @param nextInterval
	 *            the nextInterval to set
	 */
	public final void setNextInterval(Interval nextInterval) {
		this.nextInterval = nextInterval;
		// this.addFeature(MelodicFeatures.NEXT_INTERVAL,
		// nextInterval==null?"":nextInterval);
	}

	/**
	 * @return the nextDur
	 */
	public final double getNextDur() {
		return nextDur;
	}

	/**
	 * @param nextDur
	 *            the nextDur to set
	 */
	public final void setNextDur(double nextDur) {
		this.nextDur = nextDur;
		// this.addFeature(MelodicFeatures.NEXT_DURATION, nextDur);
	}

	/**
	 * @param nextIntervalName
	 *            the nextIntervalName to set
	 */
	public final void setNextIntervalName(int nextIntervalName) {
		this.nextIntervalName = nextIntervalName;
		// this.addFeature(MelodicFeatures.NEXT_INTERVAL_NAME,
		// nextIntervalName);
	}

	/**
	 * @return the prevDur
	 */
	public final double getPrevDur() {
		return prevDur;
	}

	/**
	 * @param prevDur
	 *            the prevDur to set
	 */
	public final void setPrevDur(double prevDur) {
		this.prevDur = prevDur;
		// this.addFeature(MelodicFeatures.PREV_DURATION, prevDur);
	}

	/**
	 * @return the chromatism
	 */
	public final boolean isChromatism() {
		return chromatism;
	}

	/**
	 * @param chromatism
	 *            the chromatism to set
	 */
	public final void setChromatism(boolean chromatism) {
		this.chromatism = chromatism;
		// this.addFeature(MelodicFeatures.CHROMATISM, chromatism);
	}

	/**
	 * @return the nextChromatism
	 */
	public final boolean isNextChromatism() {
		return nextChromatism;
	}

	/**
	 * @param nextChromatism
	 *            the nextChromatism to set
	 */
	public final void setNextChromatism(boolean nextChromatism) {
		this.nextChromatism = nextChromatism;
		// this.addFeature(MelodicFeatures.NEXT_CHROMATISM, nextChromatism);
	}

	/**
	 * @return the quarterPart
	 */
	public final int getQuarterPart() {
		return quarterPart;
	}

	/**
	 * @param quarterPart
	 *            the quarterPart to set
	 */
	public final void setQuarterPart(int quarterPart) {
		this.quarterPart = quarterPart;
		// this.addFeature(MelodicFeatures.QUARTER_16TH_PART, quarterPart);
	}

	/**
	 * @return the pitchDistance
	 */
	public final int getPitchDistance() {
		return pitchDistance;
	}

	/**
	 * @param pitchDistance
	 *            the pitchDistance to set
	 */
	public final void setPitchDistance(int pitchDistance) {
		this.pitchDistance = pitchDistance;
		// this.addFeature(MelodicFeatures.PITCH_DISTANCE, pitchDistance);
	}

	/**
	 * @return the nextPitchDistance
	 */
	public final int getNextPitchDistance() {
		return nextPitchDistance;
	}

	/**
	 * @param nextPitchDistance
	 *            the nextPitchDistance to set
	 */
	public final void setNextPitchDistance(int nextPitchDistance) {
		this.nextPitchDistance = nextPitchDistance;
		// this.addFeature(MelodicFeatures.NEXT_PITCH_DISTANCE,
		// nextPitchDistance);
	}

	//// End DMA
	public boolean hasSameSelectedFeatures(NoteMelodicAnalysisFeatures maf) {
		return features.equals(maf.getFeatures());
	}

	public double getInstabilityRatio() {
		return instabilityRatio;
	}

	public void setInstabilityRatio(double instabilityRatio) {
		this.instabilityRatio = instabilityRatio;
	}

	public Interval getPrevInterval() {
		return prevInterval;
	}

	public int getPrevIntervalSemitones() {
		return prevInterval == null ? MAX_SEMITONES : prevInterval.getSemitones();
	}

	public int getNextIntervalSemitones() {
		return nextInterval == null ? MAX_SEMITONES : nextInterval.getSemitones();
	}

	/**
	 * Avoid negative values by adding 64 to all values, and return 0 for empty
	 *
	 * @return
	 */
	public double getPrevIntervalPositiveSemitones() {
		return prevInterval == null ? 0 : prevInterval.getSemitones() + 21;
	}

	/**
	 * Avoid negative values by adding 64 to all values, and return 0 for empty
	 *
	 * @return
	 */
	public double getNextIntervalPositiveSemitones() {
		return nextInterval == null ? 0 : nextInterval.getSemitones() + 33;
	}

	public float getBelongsToChord() {
		return belongsToChord;
	}

	/*
	 * public void setBelongsToChord(float belongsToChord) { this.belongsToChord
	 * = belongsToChord; containsChordInformation = true; }
	 */

	public boolean isContainsChordInformation() {
		return this.containsChordInformation;
	}

	public void addBelongsToChord(float belongsToChord) {
		this.belongsToChord = belongsToChord;
		containsChordInformation = true;
		addFeature(MelodicFeatures.BELONGS_TO_CHORD, belongsToChord);
	}

	public void addPrevBelongsToChord(float pbelongsToChord) {
		this.prevBelongsToChord = pbelongsToChord;
		addFeature(MelodicFeatures.PREV_BELONGS_TO_CHORD, pbelongsToChord);
	}

	public void addNextBelongsToChord(float pbelongsToChord) {
		this.nextBelongsToChord = pbelongsToChord;
		addFeature(MelodicFeatures.NEXT_BELONGS_TO_CHORD, pbelongsToChord);
	}
	
	

	/*public float getBelongsToKey() {
		return belongsToKey;
	}*/

	/*
	 * 20160601 public void setBelongsToKey(float belongsToKey) {
	 * this.belongsToKey = belongsToKey; }
	 * 
	 * public void addBelongsToKey(float belongsToKey) { this.belongsToKey =
	 * belongsToKey; addFeature(MelodicFeatures.BELONGS_TO_KEY, belongsToKey);
	 * 
	 * }
	 */

	public MelodicAnalysisNoteKinds getPreviousNoteAnalysis() {
		return previousNoteAnalysis;
	}

	public void setPreviousNoteAnalysis(MelodicAnalysisNoteKinds previousNoteAnalysis) {
		this.previousNoteAnalysis = previousNoteAnalysis;
		addFeature(MelodicFeatures.PREVIOUS_NOTE_TAG, previousNoteAnalysis);
	}

	public void addPreviousNoteAnalysis(MelodicAnalysisNoteKinds previousNoteAnalysis) {
		this.previousNoteAnalysis = previousNoteAnalysis;
		addFeature(MelodicFeatures.PREVIOUS_NOTE_TAG, previousNoteAnalysis);
	}

	public MelodicAnalysisNoteKinds getNextNoteAnalysis() {
		return nextNoteAnalysis;
	}

	public void setNextNoteAnalysis(MelodicAnalysisNoteKinds nextNoteAnalysis) {
		this.nextNoteAnalysis = nextNoteAnalysis;
	}

	public void addNextNoteAnalysis(MelodicAnalysisNoteKinds nextNoteAnalysis) {
		this.nextNoteAnalysis = nextNoteAnalysis;
		addFeature(MelodicFeatures.NEXT_NOTE_TAG, nextNoteAnalysis);

	}

	public boolean isStrong() {
		return strong;
	}

	public double getMidiNote() {
		return this.midiNote;
	}

	boolean isOnsetInTime() {
		return onsetinTime;
	}

	public float getPrevBelongsToChord() {
		return prevBelongsToChord;
	}

	public float getNextBelongsToChord() {
		return nextBelongsToChord;
	}

}
