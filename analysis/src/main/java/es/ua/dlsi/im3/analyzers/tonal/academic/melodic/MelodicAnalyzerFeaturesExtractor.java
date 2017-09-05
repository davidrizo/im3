package es.ua.dlsi.im3.analyzers.tonal.academic.melodic;

import es.ua.dlsi.im3.analyzers.tonal.TonalAnalysis;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.harmony.ChordSpecification;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.harmony.RomanNumberChordSpecification;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * It obtains a set of features from the melodies
 *
 * @author drizo
 * @date 29/11/2011
 */
public class MelodicAnalyzerFeaturesExtractor {

    /**
     * If null, all features are used
     */
    private Set<MelodicFeatures> selectedFeatures;

    /**
     * @param features if null or empty, all features are used
     */
    public MelodicAnalyzerFeaturesExtractor(MelodicFeatures... features) {
        if (features == null || features.length == 0) {
            selectedFeatures = null;
        } else {
            selectFeatures(features);
        }
    }

    /**
     * @param song The song notes are decorated using the features
     * @return It returns the list of decorations (with features inside).
     * @throws IM3Exception
     */
    public HashMap<AtomPitch, NoteMelodicAnalysisFeatures> computeFeatures(ScoreSong song, List<Segment> sonorities, TonalAnalysis currentAnalysis)
            throws MelodicAnalysisException {
        HashMap<AtomPitch, NoteMelodicAnalysisFeatures> result = new HashMap<>();

        Segment lastSonority = sonorities.get(sonorities.size() - 1);

        for (Staff staff : song.getStaves()) {
            for (ScoreLayer layer : staff.getLayers()) {
                TreeSet<AtomPitch> notes = layer.getAtomPitchesSortedByTime();
                ArrayList<NoteMelodicAnalysisFeatures> vfeatures = new ArrayList<>(); // used
                // to
                // compute
                // nextBelongsToChord
                // and
                // prevBelongsToChord
                ArrayList<AtomPitch> vnotes = new ArrayList<>(notes);
                for (int i = 0; i < vnotes.size(); i++) {
                    AtomPitch note = vnotes.get(i);
                    AtomPitch prevNote = null;
                    AtomPitch nextNote = null;
                    Interval prevI = null;
                    Interval nextI = null;

                    try {
                        if (i > 0) {
                            prevNote = vnotes.get(i - 1);
                            if (!note.getTime().equals(prevNote.getTime()) // if not a chord
                                    && note.getTime().substract(prevNote.getEndTime()).isZero()) { // if there is not a rest between them
                                prevI = Interval.compute(prevNote.getScientificPitch(), note.getScientificPitch());
                            } else {
                                prevNote = null;
                            }
                        }

                        if (i < vnotes.size()-1) {
                            nextNote = vnotes.get(i + 1);
                            if (!note.getTime().equals(nextNote.getTime()) // if not a chord
                                    && nextNote.getTime().substract(note.getEndTime()).isZero()) { // if there is not a rest between them
                                nextI = Interval.compute(note.getScientificPitch(), nextNote.getScientificPitch());
                            } else {
                                nextNote = null;
                            }
                        }

                        if (prevI == null) {
                            prevI = new IntervalEmpty();
                        }

                        if (nextI == null) {
                            nextI = new IntervalEmpty();
                        }

                        boolean tied = note.isTiedFromPrevious();
                        double duration = computeRD(note);
                        double ratio = computeRatio(prevNote, note, nextNote);
                        if (Double.isInfinite(ratio) || Double.isNaN(ratio)) {
                            ratio = 0;
                        }
                        int instability = computeInstability(song, staff, note);

                        int prevInstability;
                        if (prevNote != null) {
                            prevInstability = computeInstability(song, staff, prevNote);
                        } else {
                            prevInstability = MeterStabilityRanks.MAX_INSTABILITY;
                        }

                        int nextInstability;
                        if (nextNote != null) {
                            nextInstability = computeInstability(song, staff, nextNote);
                        } else {
                            nextInstability = MeterStabilityRanks.MAX_INSTABILITY;
                        }

                        //// DMA
                        Measure measure = song.getMeasureActiveAtTime(note.getTime());
                        TimeSignature ts = staff.getRunningTimeSignatureAt(note.getTime());

                        double beat = getBeat(song, ts, note.getTime()); //TODO Estoy calculando muchísimas veces el beat


                        // the note is more stable than the surounding
                        boolean isStrong;
                        if (prevNote == null || nextNote == null) {
                            isStrong = true;
                        } else {
                            int strength = instability;
                            int strengthPrev = prevInstability;
                            int strengthNext = nextInstability;
                            isStrong = strength < strengthPrev && strength < strengthNext;
                        }

                        int pitchDistance = 0; // �C�mo lo inicializamos?
                        if (prevNote != null) {
                            pitchDistance = Math.abs(prevNote.getScientificPitch().computeMidiPitch()
                                    - note.getScientificPitch().computeMidiPitch());
                        }
                        int nextPitchDistance = 0; // �C�mo lo inicializamos?
                        if (nextNote != null) {
                            nextPitchDistance = Math.abs(nextNote.getScientificPitch().computeMidiPitch()
                                    - note.getScientificPitch().computeMidiPitch());
                        }

                        // true if it has not decimals
                        boolean onsetInTime = (beat == ((float) (int) beat));
                        boolean chromatism = pitchDistance == 1;
                        boolean nextChromatism = nextPitchDistance == 1;
                        int quarterPart = ((int) beat) + 1;
                        // ???
                        double instabilityRatio = computeInestabilityRatio(song, staff, prevNote, prevNote, nextNote);

                        int noteIndex = i + 1;

                        NoteMelodicAnalysis nma = currentAnalysis.getMelodicAnalysis(note);
                        MelodicAnalysisNoteKinds expectedAnalysis = null;
                        if (nma!= null) {
                            expectedAnalysis = nma.getKind();
                        }


                        NoteMelodicAnalysisFeatures features = new NoteMelodicAnalysisFeatures(note, noteIndex, expectedAnalysis,
                                prevI, nextI, tied, duration, ratio, instability, nextInstability, getTimeSignatureNumerator(ts),
                                isStrong, pitchDistance, nextPitchDistance, beat, chromatism, nextChromatism,
                                quarterPart, instabilityRatio, note.getScientificPitch().computeMidiPitch(),
                                selectedFeatures);


                        Harm harm = song.getHarmActiveAtTimeOrNull(note.getTime());
                        if (harm != null) {
                            MotionDirection direction = MotionDirection.UNDEFINED;
                            if (prevI != null) {
                                direction = prevI.getDirection();
                            }

                            Measure lastBar = song.getMeasureActiveAtTime(lastSonority.getFrom());
                            boolean isLastMeasure = song.getMeasureActiveAtTime(note.getTime()) == lastBar;

                            ScaleMembership btc = harm.belongsToChord(note.getScientificPitch().getPitchClass(), direction, isLastMeasure);
                            if (btc != null) {
                                features.addBelongsToChord(btc.getDegree().getValue());
                            } else {
                                features.addBelongsToChord(0);
                            }

                            /*System.out.println("TO-DO NO ESTAMOS USANDO V/V ... nos quedamos sólo con el primero"); //TODO V/V
                            ChordSpecification cs = harm.getChordSpecifications().get(0);
                            if (!(cs instanceof RomanNumberChordSpecification)) {
                                throw new MelodicAnalysisException("Cannot compute chords with non roman number chord specifications: " + cs.getClass());
                            }
                            RomanNumberChordSpecification rms = (RomanNumberChordSpecification) cs;
                            if (rms.getRoot() == null) {
                                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Harmony " + harm + " has not degree");
                            } else {
                                Measure lastBar = song.getMeasureActiveAtTime(lastSonority.getFrom());
                                boolean isLastMeasure = song.getMeasureActiveAtTime(note.getTime()) == lastBar;
                                ScaleMembership btc = harm.belongsToChordInKey(key, note.getScientificPitch().getPitchClass(), direction, false, isLastMeasure);

                                if (btc != null) {
                                    features.addBelongsToChord(btc.getDegree().getValue());
                                } else {
                                    //if (harmony.getDegreeString().equals("I") && key.getPitchClass().equals(note.getPitchClass())) {
                                    //	harmony.belongsToChordInKey(key, note.getPitchClass(), direction); // para depurar
                                }
                                features.addBelongsToChord(0);
                            }*/
                        }



                        /*2017 TODO AÑADIR INFORMACION ACORDES y TONALIDAD Harm harmony = currentAnalysis.getHarmAtTimeOrNull(note.getTime());
                        //Harmony harmony = song.getHarmonyActiveAtTimeOrNull(note.getTime());
                        if (harmony != null && harmony.getKey() != null) {
                            Key key = harmony.getKey();
                            MotionDirection direction = MotionDirection.UNDEFINED;
                            if (prevI != null) {
                                direction = prevI.getDirection();
                            }

                            System.out.println("NO ESTAMOS USANDO V/V ... nos quedamos sólo con el primero");
                            ChordSpecification cs = harmony.getChordSpecifications().get(0);
                            if (!(cs instanceof RomanNumberChordSpecification)) {
                                throw new MelodicAnalysisException("Cannot compute chords with non roman number chord specifications: " + cs.getClass());
                            }
                            RomanNumberChordSpecification rms = (RomanNumberChordSpecification) cs;
                            if (rms.getRoot() == null) {
                                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Harmony " + harmony + " has not degree");
                            } else {
                                Measure lastBar = song.getMeasureActiveAtTime(lastSonority.getFrom());
                                boolean isLastMeasure = song.getMeasureActiveAtTime(note.getTime()) == lastBar;
                                ScaleMembership btc = harmony.belongsToChordInKey(key, note.getScientificPitch().getPitchClass(), direction, false, isLastMeasure);

                                if (btc != null) {
                                    features.addBelongsToChord(btc.getDegree().getValue());
                                } else {
                                //if (harmony.getDegreeString().equals("I") && key.getPitchClass().equals(note.getPitchClass())) {
								//	harmony.belongsToChordInKey(key, note.getPitchClass(), direction); // para depurar
								}
                                    features.addBelongsToChord(0);
                                }
                            }
                        }*/

                        if (prevNote != null) {
                            NoteMelodicAnalysis prevA = currentAnalysis.getMelodicAnalysis(prevNote);
                            if (prevA != null) {
                                features.addPreviousNoteAnalysis(
                                        MelodicAnalysisNoteKinds.abbrToMelodicAnalysis(prevA.getKind().getAbbreviation()));
                            }
                        }
                        if (nextNote != null) {
                            NoteMelodicAnalysis nextA = currentAnalysis.getMelodicAnalysis(nextNote);
                            if (nextA != null) {
                                features.addNextNoteAnalysis(
                                        MelodicAnalysisNoteKinds.abbrToMelodicAnalysis(nextA.getKind().getAbbreviation()));
                            }
                        }

                        result.put(note, features);
                        vfeatures.add(features);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new MelodicAnalysisException("Error in note #i " + i + " = " + note.toString() + ", staff "
                                + staff.getName() + "#" + staff.getNumberIdentifier() + " in song " + song.toString(), e);
                    }
                }

                for (int i = 0; i < vfeatures.size(); i++) {
                    if (i > 0) {
                        vfeatures.get(i).addPrevBelongsToChord(vfeatures.get(i - 1).getBelongsToChord());
                    }
                    if (i < vfeatures.size() - 1) {
                        vfeatures.get(i).addNextBelongsToChord(vfeatures.get(i + 1).getBelongsToChord());
                    }
                }
            }
        }
        return result;
    }

    //TODO Comprobar
    /**
     * It returns the beat of an onset
     *
     * @param onset
     * @return Integer value if it starts in a beat, float value with decimals
     * if the onset is located between two beats. It starts from 0
     */
    public double getBeat(ScoreSong song, TimeSignature ts, Time onset) throws IM3Exception {
        if (!(ts instanceof ITimeSignatureWithDuration)) {
            throw new IM3Exception("Cannot get the beat of a time signature without duration");
        }

        Measure measure = song.getMeasureActiveAtTime(onset);
        Time offset = (onset.substract(measure.getTime()));
        return offset.getComputedTime();
    }

    private int getTimeSignatureNumerator(TimeSignature ts) throws IM3Exception {
        int numerator;
        if (ts instanceof TimeSignatureCommonTime) {
            numerator = 4;
        } else if (ts instanceof TimeSignatureCutTime) {
            numerator = 2;
        } else if (ts instanceof FractionalTimeSignature) {
            numerator = ((FractionalTimeSignature) ts).getNumerator();
        } else {
            throw new IM3Exception("Unsupported time signature: " + ts);
        }
        return numerator;
    }

    private int computeInstability(ScoreSong song, Staff staff, AtomPitch note) throws IM3Exception {
        TimeSignature ts = staff.getRunningTimeSignatureAt(note.getTime());
        int s = MeterStabilityRanks.stabilityRank(getTimeSignatureNumerator(ts), getBeat(song, ts, note.getTime()));
        return s;
    }

    private double computeRatio(AtomPitch prevNote, AtomPitch note, AtomPitch nextNote)
            throws IM3Exception {
        if (prevNote == null || nextNote == null) {
            return 0;
        } else {
            double RD = computeRD(note);
            double prevRD = computeRD(prevNote);
            double nextRD = computeRD(nextNote);
            return (RD / prevRD) * (RD / nextRD);

            // TODO Comprobar que es lo mismo - esto de bajo viene de Expert
            // System
			/*
			 * double ratio; if (prevI == null || nextI == null) { ratio = 0; }
			 * else { ratio = (snote.getFigure().getRatio() /
			 * prev.getFigure().getRatio()) * (snote.getFigure().getRatio() /
			 * next.getFigure().getRatio()); }
			 */
        }
    }

    // TODO Test
    private double computeInestabilityRatio(ScoreSong song, Staff staff, AtomPitch prevNote, AtomPitch note, AtomPitch nextNote)
            throws IM3Exception {
        if (prevNote == null || nextNote == null) {
            return 0;
        } else {
            double instab = computeInstability(song, staff, note);
            double prevINS = computeInstability(song, staff, prevNote);
            double nextINS = computeInstability(song, staff, nextNote);
            return (instab / prevINS) * (instab / nextINS);

            // TODO Comprobar que es lo mismo - esto de bajo viene de Expert
            // System
			/*
			 * double ratio; if (prevI == null || nextI == null) { ratio = 0; }
			 * else { ratio = (snote.getFigure().getRatio() /
			 * prev.getFigure().getRatio()) * (snote.getFigure().getRatio() /
			 * next.getFigure().getRatio()); }
			 */
        }
    }

    private double computeRD(AtomPitch note) throws IM3Exception {
        return note.getDuration().getComputedTime();
    }

    private void selectFeatures(MelodicFeatures... features) {
        selectedFeatures = new TreeSet<>();
        for (MelodicFeatures mf : features) {
            selectedFeatures.add(mf);
        }
    }

    public int getNumFeatures() {
        return selectedFeatures.size();
    }
}
