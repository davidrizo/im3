package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.harmony.*;
import es.ua.dlsi.im3.core.score.io.IScoreSongImporter;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.ligature.LigatureFactory;
import es.ua.dlsi.im3.core.score.mensural.meters.*;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.core.score.staves.AnalysisStaff;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.io.antlr.ErrorListener;
import es.ua.dlsi.im3.core.io.antlr.GrammarParseRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.ua.dlsi.im3.core.io.antlr.ParseError;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.math3.util.ArithmeticUtils;

/**
 * Note: It imports the root voice as a new part
 *
 * @author drizo
 */
public class KernImporter implements IScoreSongImporter {
    DurationEvaluator durationEvaluator;

    public KernImporter() {
        durationEvaluator = new DurationEvaluator();
    }

    public KernImporter(DurationEvaluator durationEvaluator) {
        this.durationEvaluator = durationEvaluator;
    }

    @Override
    public ScoreSong importSong(InputStream is) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static class Spine {
        public int spineIndex;
        public Spine splittedFrom;
        public Spine splittedInto;
        public Time tupletStartTime;
        public Time ligatureStartTime;
        public boolean ligatureEnded;
        Staff staff;
        ScoreLayer layer;
        String pendingStaffName;
        NotationType notationType;
        boolean rootSpine;
        boolean harmSpine;

        ArrayList<SingleFigureAtom> tupletElements;
        ArrayList<SimpleNote> ligatureNotes;
        ArrayList<Integer> tupletDurations;
        boolean inTuplet;
        int mcdTupleElementDuration; // greatest common divisor
        int mcm; // lowest common multiple
        int tupletDots;
        private int lastDuration; // duration of last note

        public Spine(int index) {
            spineIndex = index;
            tupletElements = new ArrayList<>();
            tupletDurations = new ArrayList<>();
            ligatureNotes = null;
            rootSpine = false;
            harmSpine = false;
        }
    }

    public static class Loader extends kernBaseListener {
        DurationEvaluator durationEvaluator;
        ScoreSong scoreSong;
        int currentSpineIndex;
        private Spine currentSpine;
        private Spine harmSpine = null;
        int currentRow = 0; // incremented in enterRecord - start from 1
        boolean measureInserted;
        Time lastTime;
        boolean timeNeedsUpdate;
        int ksNotesCount = 0;
        int lastDots;
        Figures lastFigure;
        private Figures lastDurationFigure; // used just in modernDuration and mensuralDuration rules
        private Integer lastDur;// used just in modernDuration and mensuralDuration rules

        SimpleChord chord;

        ArrayList<Spine> spines;
        ArrayList<Spine> spinesToRemove; // when a join is found

        HashMap<Integer, Staff> stavesByNumber = new HashMap<>();
        HierarchicalIDGenerator hierarchicalIDGenerator = new HierarchicalIDGenerator();


        // long currentTime = 0;
        //ScoreFigureAndDots<Figures> lastDuration = null;
        //private Time recordTime; // the time at the beginning of each record
        //Time minimumRecordDuration; // the minimum duratino of the current record

        private ScorePart globalPart;
        private ScorePart rootPart;
        TimeSignature currentMeter;
        // ArrayList<ScoreSoundingElement> measurescorenotes = new
        // ArrayList<>();
        Measure currentMeasure;
        private Spine rootSpine = null;
        private Mode keyChangeMode;
        private String keyString;
        private int octaveModif;
        private String noteName;
        //private SimpleNote lastRootNote;
        private SingleFigureAtom lastNoteOrChord;
        TreeSet<Double> figureBeatsSortedForTupletProcessing;
        private boolean inChord;

        private String lastMensuralPerfection;
        private boolean lastMensuralFigureColoured;

        private StemDirection lastStemDirection;

        // TODO refactorización private ScoreStaff rootStaff;
        private Measure lastMeasure = null;
        private Key lastHarmKey;
        private boolean kernSpineFound = false;
        private boolean eofReached = false;

        TreeSet<Integer> nonTupletDurations = new TreeSet<>(); // faster than a math op

        {
            nonTupletDurations.add(0);
            nonTupletDurations.add(1);
            nonTupletDurations.add(2);
            nonTupletDurations.add(4);
            nonTupletDurations.add(8);
            nonTupletDurations.add(16);
            nonTupletDurations.add(32);
            nonTupletDurations.add(64);
            nonTupletDurations.add(128);
            nonTupletDurations.add(256);
        }


        Loader(ScoreSong song, DurationEvaluator durationEvaluator) {
            //recordTime = Time.TIME_ZERO;
            scoreSong = song;
            this.durationEvaluator = durationEvaluator;
            //TODO Ahora va todo a la misma part
            globalPart = new ScorePart(scoreSong, 0);
            scoreSong.addPart(globalPart);
            prepareFiguresForTuplet();
        }

        // used for reading just harmonies
        Loader(Key harmKey) {
            //recordTime = Time.TIME_ZERO;
            this.lastHarmKey = harmKey;
        }

        /// -------- Helper methods -------
        boolean inHarmSpine() {
            //return spines.get(currentSpineIndex).harmSpine;
            return currentSpine.harmSpine;
        }

        boolean inRootSpine() {
            //return spines.get(currentSpineIndex).rootSpine;
            return currentSpine.rootSpine;
        }

        /*final boolean isRootBeforeHarmonies() {
            return rootSpine < harmSpine;
        }*/

		/*int gcd(int a, int b) {
            if (b == 0) {
				return a;
			} else {
				return gcd(b, a % b);
			}
		}

		static int mcm(int a, int b) {
			int m = gcd(a, b);
			return (m * (a / m) * (b / m));
		}*/


        private Staff getStaff() throws IM3Exception {
            Staff staff = currentSpine.staff;
            if (staff == null) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Adding staff 0 to part");
                staff = addStaff(stavesByNumber.size()+1); // avoid starting from 0
            }
            return staff;
        }

        Staff addStaff(int number) throws IM3Exception {
            Staff staff = new Pentagram(scoreSong, hierarchicalIDGenerator.nextStaffHierarchicalOrder(null), number);
            staff.setNotationType(currentSpine.notationType);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Adding staff #{0} to spine #{1}", new Object[]{number, currentSpineIndex});
            currentSpine.staff = staff;
            ScoreLayer layer = currentSpine.layer;
            if (layer == null) {
                throw new IM3RuntimeException("Spine " + currentSpineIndex + " has not a layer (maybe the **mens or **kern is missing)");
            }
            staff.addLayer(0, layer);
            if (currentSpine.splittedInto != null) {
                currentSpine.splittedInto.staff = staff;
                staff.addLayer(currentSpine.splittedInto.layer);
            }

            stavesByNumber.put(number, staff);
            if (!inRootSpine() && !inHarmSpine()) {
                // if an analysis spine, it is not added to the song
                scoreSong.addStaffAt(0, staff); // kern begins from bottom
            }
            staff.addPart(layer.getPart()); // TODO: 20/11/17 Parts when two parts in a staff - ahora está todo en el mismo part!!!!
            layer.getPart().addStaffAt(0, staff); // kern begins from bottom

            if (currentSpine.pendingStaffName != null) {
                staff.setName(currentSpine.pendingStaffName);
            }

            return staff;
        }

        Time getLastTime() {
            /*if (inHarmSpine()) {
                return lastTime;
            } else {
                try {
                    if (currentVoice == null || currentVoice.isEmpty()) {
                        lastTime = Time.TIME_ZERO;
                    } else {
                        lastTime = currentVoice.getDuration();
                    }
                    return lastTime;
                } catch (IM3Exception ex) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                    throw new GrammarParseRuntimeException(ex);
                }
            }*/
            if (lastTime == null) {
                return Time.TIME_ZERO;
            } else {
                return lastTime;
            }
        }

        //VoiceTemp getCurrentVoiceTemp() {
            /*if (inHarmSpine()) {
                // try to get the root voice
                if (rootSpine != -1) {
                    return rootVoiceTemp;
                } else {
                    // get the first available voiceTemp
                    for (int i = 0; i < this.voiceTemp.size(); i++) {
                        if (voiceTemp.get(i) != null) {
                            return voiceTemp.get(i);
                        }
                    }
                    Logger.getLogger(KernImporter.class.getName()).log(Level.WARNING,
                            "Cannot get a voice for the harm spine");
                    return null;
                }
            } else {
                return voiceTemp.get(currentSpineIndex);
            }*/
        //    return voiceTemp.get(currentSpineIndex);
        //}

        private TimeSignature currentTimeSignature() throws IM3Exception {
            Staff staff = getStaff();
            return staff.getRunningTimeSignatureAt(lastTime);

			/*2017 f (currentVoice.isEmpty()) {
				return scoreSong.getFirstMeter();
			} else {
				try {
					return scoreSong.getActiveMeterAtTime(currentVoice.getLastDurationalSymbol().getTime());
				} catch (IM3Exception ex) {
					Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
					throw new GrammarParseRuntimeException(ex);
				}
			}*/
        }

        private void addAtom(Time time, Atom atom) throws IM3Exception {
            currentSpine.layer.insert(time, atom);
            Staff staff = getStaff();
            //220522 staff.addTimedElement(atom);

        }


        //////////
        @Override
        public void enterHeader(kernParser.HeaderContext ctx) {
            super.enterHeader(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Enter Header {0}", ctx.getText());

            spines = new ArrayList<>();
            currentSpineIndex = 0;
        }


        @Override
        public void exitHeaderField(kernParser.HeaderFieldContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Exit Header Field {0}", ctx.getText());
            currentSpineIndex++;
        }

        /*private void prepareVoiceTemp() {
            voiceTemp.put(currentSpineIndex, new VoiceTemp());
            if (spines.get(currentSpineIndex) == null) {
                // 2017
                ScoreLayer v = null; //TODO Ahora va todo a globalPart
                try {
                    if (inRootSpine()) {
                        v = rootPart.addScoreLayer();
                        v.setDurationEvaluator(durationEvaluator);
                    } else {
                        v = globalPart.addScoreLayer();
                        v.setDurationEvaluator(durationEvaluator);
                    }
                } catch (IM3Exception ex) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                    throw new GrammarParseRuntimeException(ex);
                }

                spines.put(currentSpineIndex, v);
            }
        }*/

        /**
         *
         * @param part
         * @param index If null the spine will be inserted to the end
         * @return
         */
        private Spine prepareSpine(ScorePart part, Integer index) {
            Spine spine = new Spine(currentSpineIndex);
            if (index == null || index >= spines.size()){
                spines.add(spine);
            } else {
                spines.add(index, spine);
            }

            try {
                if (part != rootPart) { //TODO esto está muy mal programado ;(
                    spine.layer = part.addScoreLayer();
                } else {
                    spine.layer = part.getUniqueVoice();
                }
            } catch (IM3Exception e) {
                throw new IM3RuntimeException("Cannot add score layer: " + e);
            }
            spine.layer.setDurationEvaluator(durationEvaluator);

            return spine;
        }

        private Spine prepareSpine(ScorePart part) {
            return prepareSpine(part, null);
        }



        @Override
        public void exitSong(kernParser.SongContext ctx) {
            for (int i=0; i<ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                //System.out.println("CHIIIIIIDDDDDDDD " + child.getText()); // FIXME: 13/10/17
                if (child instanceof TerminalNode) {
                    TerminalNode typeNode = (TerminalNode) child;
                    if (typeNode.getSymbol().getType() == kernLexer.METADATACOMMENT) {
                        System.out.println("READING METADATA: " + typeNode.getText());
                    }
                }
            }

            try {
                scoreSong.processMensuralImperfectionRules();
            } catch (IM3Exception e) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.WARNING, "Cannot apply mensural imperfection rules", e);
                throw new GrammarParseRuntimeException(e);
            }
        }

        /**
         * **kern
         *
         * @param ctx
         */
        @Override
        public void enterHeaderKern(kernParser.HeaderKernContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Kern {0}", ctx.getText());
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Enter Header **krn {0}", ctx.getText());
            prepareSpine(globalPart).notationType = NotationType.eModern;
        }

        @Override
        public void exitHeaderKern(kernParser.HeaderKernContext ctx) {
            kernSpineFound = true; // for dealing with **harm only files
        }

        @Override
        public void enterHeaderMens(kernParser.HeaderMensContext ctx) {
            super.enterHeaderMens(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Enter Header **mens {0}", ctx.getText());
            prepareSpine(globalPart).notationType = NotationType.eMensural;
        }

        @Override
        public void exitHeaderMens(kernParser.HeaderMensContext ctx) {
            kernSpineFound = true; // for dealing with **harm only files
        }

        private Staff addAnalysisPart() {
            try {
                if (rootPart == null) {
                    rootPart = scoreSong.addAnalysisPart();
                    AnalysisStaff analysisStaff = new AnalysisStaff(scoreSong, "99999", 99999); //TODO
                    analysisStaff.setNotationType(NotationType.eModern); // always modern
                    rootPart.addStaff(analysisStaff);
                    scoreSong.addStaff(analysisStaff);
                    return analysisStaff;
                } else {
                    return scoreSong.getAnalysisStaff();
                }
            } catch (IM3Exception e) {
                throw new IM3RuntimeException("Cannot add an analysis part: " + e);
            }
        }

        @Override
        public void enterHeaderHarm(kernParser.HeaderHarmContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Harm {0}", ctx.getText());
            if (harmSpine != null) {
                throw new GrammarParseRuntimeException("Cannot set two harm spines, previous was " + harmSpine.spineIndex
                        + ", and new one is " + currentSpineIndex + " at row " + currentRow);
            }
            Staff analysisStaff = addAnalysisPart();
            harmSpine = prepareSpine(rootPart);
            harmSpine.harmSpine = true;
            harmSpine.staff = analysisStaff;
            harmSpine.layer.setStaff(analysisStaff);
            analysisStaff.addLayer(harmSpine.layer);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Setting harm spine in {0}", harmSpine);
        }

        @Override
        public void enterHeaderRoot(kernParser.HeaderRootContext ctx) {
            Staff analysisStaff = addAnalysisPart();
            rootSpine = prepareSpine(rootPart);
            rootSpine.rootSpine = true;
            rootSpine.staff = analysisStaff;
            rootSpine.layer.setStaff(analysisStaff);
            analysisStaff.addLayer(rootSpine.layer);
			/*
			 * Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST,
			 * "Root {0}", ctx.getText()); if (rootSpine != -1) { throw new
			 * GrammarParseRuntimeException(
			 * "Cannot set two root spines, previous was " + rootSpine +
			 * ", and new one is " + currentSpineIndex + " at row " +
			 * currentRow); }
			 * 
			 * try { rootPart = this.scoreSong.addAnalysisPart("Analysis / Root"
			 * ); //ScoreLayer v = new ScoreLayer(rootPart, currentSpineIndex);
			 * spines.put(currentSpineIndex, this.scoreSong.getAnalysisVoice());
			 * //rootStaff = rootPart.addStaff(); rootStaff =
			 * this.scoreSong.getAnalysisStaff();
			 * stavesForSpines.put(currentSpineIndex, rootStaff); } catch
			 * (IM3Exception ex) {
			 * Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
			 * null, ex); throw new GrammarParseRuntimeException(ex); }
			 * rootVoiceTemp = new VoiceTemp(); voiceTemp.put(currentSpineIndex,
			 * new VoiceTemp()); rootSpine = currentSpineIndex;
			 */
        }

        @Override
        public void enterRecord(kernParser.RecordContext ctx) {
            super.enterRecord(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Record {0}", ctx.getText());
            currentRow++;
            measureInserted = false;

            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Record #{0}", currentRow);
            currentSpineIndex = 0;
            lastNoteOrChord = null;
            timeNeedsUpdate = true;
            spinesToRemove = null;
            //minimumRecordDuration = Time.TIME_MAX;
        }

        @Override
        public void exitRecord(kernParser.RecordContext ctx) {
            super.exitRecord(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Record {0}", ctx.getText());
            /*if (!minimumRecordDuration.equals(Time.TIME_MAX)) {
                // if some duration symbols has been found
                recordTime = recordTime.add(minimumRecordDuration);
            }*/

            if (spinesToRemove != null) {
                for (Spine spine: spinesToRemove) {
                    spines.remove(spine);
                }
            }

            try {
                updateLastTime();
            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                throw new GrammarParseRuntimeException(ex);
            }
        }


        @Override
        public void enterField(kernParser.FieldContext ctx) {
            super.enterField(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Entering field {0}", ctx.getText());
            currentSpine = spines.get(currentSpineIndex);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Spine #{0}", currentSpineIndex);
            // currentScorePart = scoreSong.getPart(currentPartIndex);
            /*if (!inHarmSpine()) {
                currentVoice = spines.get(currentSpineIndex);
            } else {
                currentVoice = null;
            }*/

            //updateLastTime();
        }

        @Override
        public void exitFieldComment(kernParser.FieldCommentContext ctx) {
            if (currentSpine.staff == null) {
                // no staff yet - this field is the staff name
                String name = ctx.getText().substring(1).trim();
                currentSpine.pendingStaffName = name;
            }
        }

        private void updateLastTime() throws IM3Exception {
            lastTime = Time.TIME_MAX;
            if (!this.spines.isEmpty()) {
                for (Spine spine: spines) {
                    if (!spine.rootSpine && !spine.harmSpine && !spine.layer.isEmpty()) {
                        Time dur = spine.layer.getDuration();
                        lastTime = Time.min(lastTime, dur); // min because we are completing notes - it is not a mistake
                    }
                }
            }
            if (lastTime.equals(Time.TIME_MAX)) {
                lastTime = Time.TIME_ZERO;
            }
        }

        @Override
        public void exitField(kernParser.FieldContext ctx) {
            //updateLastTime();
            currentSpineIndex++;
        }

        @Override
        public void enterInterpretation(kernParser.InterpretationContext ctx) {
            super.enterInterpretation(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Enter Interpretation {0}", ctx.getText());
        }


        @Override
        public void exitInterpretation(kernParser.InterpretationContext ctx) {
            super.exitInterpretation(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Exit Interpretation {0}", ctx.getText());
            // TODO
        }

        @Override
        public void enterTandemInterpretation(kernParser.TandemInterpretationContext ctx) {
            super.enterTandemInterpretation(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Enter Tandem Interpretation {0}", ctx.getText());
        }


        @Override
        public void exitTandemInterpretation(kernParser.TandemInterpretationContext ctx) {
            super.exitTandemInterpretation(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Exit Tandem Interpretation {0}", ctx.getText());
            // TODO
        }


        @Override
        public void exitClef(kernParser.ClefContext ctx) {
            try {
                super.exitClef(ctx);
                Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST,
                        "Clef {0}", ctx.getText());

                Clef clef;
                switch (ctx.getText()) {
                    case "G2":
                        clef = new ClefG2();
                        break;
                    case "F2":
                        clef = new ClefF2();
                        break;
                    case "F3":
                        clef = new ClefF3();
                        break;
                    case "F4":
                        clef = new ClefF4();
                        break;
                    case "C1":
                        clef = new ClefC1();
                        break;
                    case "C2":
                        clef = new ClefC2();
                        break;
                    case "C3":
                        clef = new ClefC3();
                        break;
                    case "C4":
                        clef = new ClefC4();
                        break;
                    case "C5":
                        clef = new ClefC5();
                        break;
                    case "G1":
                        clef = new ClefG1();
                        break;
                    case "Gv2":
                        clef = new ClefG2QuindicesimaBassa();
                        break;
                    default:
                        throw new
                                RuntimeException("Invalid clef: " + ctx.getText()); //TODO Logger
                }
                Staff staff = getStaff();
                Logger.getLogger(KernImporter.class.getName()).log(Level.INFO,
                        "Setting clef {0} to staff {1}", new Object[]{clef.toString(), staff.getNumberIdentifier()});

                Time t = getLastTime();
                Clef otherClef = staff.getClefAtTime(t);
                if (otherClef != null) {
                    if (!otherClef.equals(clef)) {
                        throw new
                                GrammarParseRuntimeException("There is already a clef " +
                                otherClef.toString() + " at time " + t +
                                " while inserting " + clef.toString());
                    }
                } else {
                    clef.setTime(t);
                    staff.addElementWithoutLayer(clef);
                }
            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                throw new GrammarParseRuntimeException(ex);
            }
        }

        @Override
        public void enterKeysignature(kernParser.KeysignatureContext ctx) {
            super.enterKeysignature(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Beginning a key signature",
                    ctx.getText());
            ksNotesCount = 0;
        }


        @Override
        public void exitKeysignature(kernParser.KeysignatureContext ctx) {
            super.exitKeysignature(ctx);

            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Key Signature {0}", ctx.getText());
            try {
                Logger.getLogger(KernImporter.class.getName()).log(Level.INFO,
                        "Currently all key signatures are being encoded as UNKOWN");

                Time currentTime = getLastTime();
                Key ks;
                if (ctx.keysignatureNote().isEmpty()) {
                    ks = new Key(PitchClasses.C.getPitchClass(), Mode.UNKNOWN); // mode
                } else {
                    DiatonicPitch nn = DiatonicPitch.valueOf(ctx.keysignatureNote().get(0).noteNameLowerCase().getText().toUpperCase());
                    if (nn == DiatonicPitch.F) {
                        // sharps
                        ks = new Key(ctx.keysignatureNote().size(), Mode.UNKNOWN.name());
                    } else if (nn == DiatonicPitch.B) {
                        // flats
                        ks = new Key(-ctx.keysignatureNote().size(), Mode.UNKNOWN.name());
                    } else {
                        throw new GrammarParseRuntimeException("Unimplemented key signature support: " + ctx.getText());
                    }
                    // TODO Comprobar el contenido
                }

                /*if (inRootSpine()) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.WARNING, "Skipping key information in **root spine");
                } else if (inHarmSpine()) {
                    lastHarmKey = ks;
                } else {*/

                    Staff staff = getStaff();

                    addKeySignature(currentTime, staff, ks);
                    if (inHarmSpine() || inRootSpine()){
                        lastHarmKey = ks;
                        addKeySignature(currentTime, scoreSong.getAnalysisStaff(), ks);
                    }
                //}
            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                throw new GrammarParseRuntimeException(ex);
            }
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Recognized key signature with {0} notes",
                    ksNotesCount);

        }

        private void addKeySignature(Time currentTime, Staff staff, Key ks) throws IM3Exception {
            KeySignature otherKey = staff.getKeySignatureWithOnset(currentTime);
            if (otherKey != null) {
                //TODO Comprobar transpositores
                if (!otherKey.getConcertPitchKey().equals(ks)) {
                    throw new GrammarParseRuntimeException("There is already a key " + otherKey.toString()
                            + " at time " + currentTime + " while inserting " + ks.toString());
                }
            } else {
                KeySignature newKs = new KeySignature(currentSpine.notationType, ks);
                newKs.setTime(currentTime);
                staff.addElementWithoutLayer(newKs);

                Logger.getLogger(KernImporter.class.getName()).log(Level.INFO,
                        "Adding key signature {0} to staff {1}", new Object[]{newKs, staff});

                if (inHarmSpine() || inRootSpine()) { // parche
                    KeySignature newKsAnalysis = new KeySignature(currentSpine.notationType, ks);
                    newKsAnalysis.setTime(currentTime);
                    scoreSong.getAnalysisStaff().addElementWithoutLayer(newKsAnalysis);

                    Logger.getLogger(KernImporter.class.getName()).log(Level.INFO,
                            "Adding key signature {0} to staff {1}", new Object[]{newKs, staff});

                }
            }
        }

        @Override
        public void exitKeysignatureNote(kernParser.KeysignatureNoteContext ctx) {
            super.exitKeysignatureNote(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Key Signature note {0}", ctx.getText());

            //TODO Control de qué notas pone
            ksNotesCount++;
        }

        @Override
        public void exitMeterKnown(kernParser.MeterKnownContext ctx) {
            super.exitMeterKnown(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Meter {0}", ctx.getText());
            Time currentTime = getLastTime();

            //TODO Common time ..., compuestos, denominadores con puntillos
            // Ver kern.g4 | (TANDEM_MET LEFTPAR 'C' RIGHTPAR) // not found in documentation, e.g. met(C) for common time
            try {
                TimeSignature ts = new FractionalTimeSignature(new Integer(ctx.numerator().getText()), new Integer(ctx.denominator().getText()));
                ts.setTime(currentTime);
                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Recognized time signature {0}", ts);

                Staff staff = getStaff();
                TimeSignature presentMeter = staff.getTimeSignatureWithOnset(currentTime);

                if (presentMeter != null) {
                    if (!(presentMeter.equals(ts) || (presentMeter instanceof TimeSignatureCommonTime &&
                                ts instanceof FractionalTimeSignature && ((FractionalTimeSignature) ts).getNumerator() == 4
                                && ts instanceof FractionalTimeSignature && ((FractionalTimeSignature) ts).getDenominator() == 4
                            ||
                                presentMeter instanceof TimeSignatureCutTime &&
                                        ts instanceof FractionalTimeSignature && ((FractionalTimeSignature) ts).getNumerator() == 2
                                        && ts instanceof FractionalTimeSignature && ((FractionalTimeSignature) ts).getDenominator() == 2))) {
                        throw new GrammarParseRuntimeException("There is already a meter " + presentMeter.toString()
                                + " at time " + currentTime + " while inserting " + ts.toString());
                    }
                    currentMeter = presentMeter;
                } else {
                    ts.setTime(currentTime);
                    staff.addElementWithoutLayer(ts);
                    // TODO No soportamos cambios de compas entre spines, y tampoco lo comprobamos
                    currentMeter = ts;

                }
            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                throw new GrammarParseRuntimeException(ex);
            }
        }

        @Override
        public void exitMeterSign(kernParser.MeterSignContext ctx) {
            TimeSignature ts;
            if (currentSpine.notationType == null || currentSpine.notationType == NotationType.eModern) {
                switch (ctx.getText()) {
                    case "C":
                    case "c":
                        ts = new TimeSignatureCommonTime(currentSpine.notationType);
                        break;
                    case "C|":
                    case "c|":
                        ts = new TimeSignatureCutTime(currentSpine.notationType);
                        break;
                    default:
                        throw new GrammarParseRuntimeException("Unsupported meter sign: '" + ctx.getText() + "'");
                }
            } else if (currentSpine.notationType == NotationType.eMensural) {
                switch (ctx.getText()) {
                    case "c":
                        ts = new TimeSignatureCommonTime(currentSpine.notationType);
                        break;
                    case "C":
                        ts = new TempusImperfectumCumProlationeImperfecta();
                        break;
                    case "C·":
                        ts = new TempusImperfectumCumProlationePerfecta();
                        break;
                    case "O":
                        ts = new TempusPerfectumCumProlationeImperfecta();
                        break;
                    case "O·":
                        ts = new TempusPerfectumCumProlationePerfecta();
                        break;
                    case "C|":
                    case "c|":
                        ts = new TimeSignatureCutTime(currentSpine.notationType);
                        break;
                    default:
                        throw new GrammarParseRuntimeException("Unsupported meter sign: '" + ctx.getText() + "'");
                }

            } else {
                throw new GrammarParseRuntimeException("Invalid notation type: " + currentSpine.notationType);
            }
            Staff staff = null;
            try {
                staff = getStaff();
                Time currentTime = getLastTime();
                TimeSignature presentMeter = staff.getTimeSignatureWithOnset(currentTime);
                if (presentMeter != null) {
                    // replace if for new meter
                    staff.remove(presentMeter);
                }
                ts.setTime(currentTime);
                ts.setStaff(staff);
                staff.addElementWithoutLayer(ts);

            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                throw new GrammarParseRuntimeException(ex);
            }

        }

        @Override
        public void exitStaff(kernParser.StaffContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Staff {0}", ctx.getText());

            int number = new Integer(ctx.NUMBER().getText());
            try {
                Staff staff = stavesByNumber.get(number);
                if (staff == null) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Creating staff {0}", new Object[]{number});
                    staff = addStaff(number);
                } else {
                    currentSpine.staff = staff;
                    staff.addLayer(0, currentSpine.layer);
                }
                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Associating spine {0} to staff {1}", new
                        Object[]{currentSpineIndex, number});
            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
                        null, ex);
                throw new GrammarParseRuntimeException(ex);
            }
        }

        @Override
        public void exitKeyChange(kernParser.KeyChangeContext ctx) {
            try {
                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Key change {0}", ctx.getText());
                DiatonicPitch nn = DiatonicPitch.valueOf(keyString.toUpperCase());
                PitchClass pc;
                if (ctx.keyAccidental() != null) {
                    Accidentals acc = null;
                    switch (ctx.keyAccidental().getText()) {
                        case "n":
                            acc = Accidentals.NATURAL;
                            break;
                        case "-":
                            acc = Accidentals.FLAT;
                            break;
                        case "#":
                            acc = Accidentals.SHARP;
                            break;
                        default:
                            throw new GrammarParseRuntimeException(
                                    "Non valid accidental for key: " + ctx.keyAccidental().getText());
                    }
                    pc = new PitchClass(nn, acc);
                } else {
                    pc = new PitchClass(nn);
                }
                Time t = getLastTime();

                Key kc = new Key(pc, keyChangeMode);

                /*Harmony previousH = scoreSong.getHarmonyWithOnsetOrNull(t);
                if (previousH != null) {
                    if (previousH.getActiveKey() != null) {
                        if (!previousH.getActiveKey().equals(kc)) {
                            throw new GrammarParseRuntimeException(
                                    "Inserting two key changes (prev=" + previousH.getActiveKey().toString() + ", new="
                                            + kc.toString() + ") at the same time (t=" + t + ")");
                        }
                    } else {
                        previousH.setKey(kc);
                    }
                } else {
                    Harmony previousHarmony = scoreSong.getHarmonyActiveAtTimeOrNull(t);
                    Harmony h = new Harmony(previousHarmony, kc);
                    scoreSong.addHarmony(t, h);
                    lastHarmony = h;
                }*/

				/*
				 * if (previousKC != null && !previousKC.hasMode()) {
				 * Logger.getLogger(KernImporter.class.getName()).log(Level.
				 * FINE,
				 * "Removing key signature without mode to substituted by this key change"
				 * , ctx.getText()); scoreSong.removeKey(previousKC); insertKey
				 * = true; } else if (previousKC == null) { insertKey = true; }
				 * else { if (t == previousKC.getTime()) { if
				 * (!previousKC.equals(kc)) { throw new
				 * GrammarParseRuntimeException(
				 * "Two different keys (previous = " + previousKC.toString() +
				 * ", new one = " + kc.toString() +
				 * ") being inserted in the same time " + t); } else { insertKey
				 * = false; // already present } } else { insertKey = true; //
				 * different time } }
				 *
				 * if (insertKey) { scoreSong.addKey(t, kc); }
				 */
            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                throw new GrammarParseRuntimeException(ex.toString());
            }
        }

        @Override
        public void exitMajorKey(kernParser.MajorKeyContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Major key {0}", ctx.getText());
            keyChangeMode = Mode.MAJOR;
            keyString = ctx.getText();
        }

        @Override
        public void exitMinorKey(kernParser.MinorKeyContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Minor key {0}", ctx.getText());
            keyChangeMode = Mode.MINOR;
            keyString = ctx.getText();
        }

        @Override
        public void exitNoteRestChord(kernParser.NoteRestChordContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST,
                    "NoteRestChord {0}", ctx.getText());
            if (currentSpine.inTuplet) {
                try {
                    int dur = currentSpine.lastDuration;
                    currentSpine.tupletDurations.add(dur);
                    currentSpine.mcdTupleElementDuration = ArithmeticUtils.gcd(dur,
                            currentSpine.mcdTupleElementDuration);
                    currentSpine.mcm = ArithmeticUtils.lcm(dur, currentSpine.mcm);
                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                            "Adding element to tuplet with figureAndDots {0}", dur);

                    if (processTuplet()) { // tuplet complete
                        Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                "Tuplet processed");
                        currentSpine.inTuplet = false;
                        currentSpine.tupletDurations.clear();
                        currentSpine.tupletElements.clear();
                    }
                } catch (IM3Exception | NoMeterException ex) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
                            null, ex);
                    throw new GrammarParseRuntimeException(ex);
                }
            }
        }

        @Override
        public void exitModernDuration(kernParser.ModernDurationContext ctx) {
            Figures f;

            lastDur = new Integer(ctx.NUMBER().getText());
            switch (ctx.NUMBER().getText()) {
                case "0":
                    f = Figures.DOUBLE_WHOLE;
                    break;
                case "1":
                    f = Figures.WHOLE;
                    break;
                case "2":
                    f = Figures.HALF;
                    break;
                case "4":
                    f = Figures.QUARTER;
                    break;
                case "8":
                    f = Figures.EIGHTH;
                    break;
                case "16":
                    f = Figures.SIXTEENTH;
                    break;
                case "32":
                    f = Figures.THIRTY_SECOND;
                    break;
                case "64":
                    f = Figures.SIXTY_FOURTH;
                    break;
                case "128":
                    f = Figures.HUNDRED_TWENTY_EIGHTH;
                    break;
                case "256":
                    f = Figures.TWO_HUNDRED_FIFTY_SIX;
                    break;
                default: // tuplet
                    f = Figures.NO_DURATION;
                    // one temporal that will be modified later
            }
            lastDurationFigure = f;
        }

        @Override
        public void exitMensuralDuration(kernParser.MensuralDurationContext ctx) {
            Figures f;

            switch (ctx.getText().charAt(0)) {
                case 'X': f = Figures.MAXIMA; break;
                case 'L': f = Figures.LONGA; break;
                case 'S': f = Figures.BREVE; break;
                case 's': f = Figures.SEMIBREVE; break;
                case 'M': f = Figures.MINIM; break;
                case 'm': f = Figures.SEMIMINIM; break;
                case 'U': f = Figures.FUSA; break;
                case 'u': f = Figures.SEMIFUSA; break;
                default:
                    throw new GrammarParseRuntimeException("Mensural duration '" + ctx.getText() + "' not recognized");
            }

            lastMensuralFigureColoured = ctx.COLOURED() != null;
            lastMensuralPerfection = ctx.mensuralPerfection() == null?null:ctx.mensuralPerfection().getText();
            lastDurationFigure = f;
        }

        @Override
        public void exitDuration(kernParser.DurationContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "ScoreFigureAndDots {0}", ctx.getText());

            if (inHarmSpine()) {
//                int line = ctx.getChild(0).getA
                throw new GrammarParseRuntimeException("Unexpected duration in a harm spine: " + ctx.getText() + ", line " + ctx.getStart().getLine());
            }

            Figures f = lastDurationFigure;
            if (f == Figures.NO_DURATION) {
                    // one temporal that will be modified later
                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                            "ScoreFigureAndDots figureAndDots has to be a tuplet {0}", ctx.getText());
                    if (!currentSpine.inTuplet) {
                        currentSpine.inTuplet = true;
                        currentSpine.tupletStartTime = lastTime;
                        currentSpine.tupletElements.clear();
                        currentSpine.mcdTupleElementDuration = lastDur;
                        currentSpine.mcm = lastDur;
                        currentSpine.tupletDurations.clear();
                        Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                "Starting tuplet with element figureAndDots {0}", ctx.getText());
                    }
                currentSpine.lastDuration = lastDur;
            }
            int dots = ctx.augmentationDots().getText().length();
            if (!currentSpine.inTuplet) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Figure {0} with {1} dots",
                        new Object[]{f, dots});

                if (ctx.getParent().getRuleIndex() != kernParser.RULE_denominator) {
                    // not rule for meter
                    lastFigure = f;
                    lastDots = dots;
                }
            } else { // TODO Comprobar que no salimos de un tuplet sin dots y
                // pasamos a uno con dots...
                currentSpine.tupletDots = dots;
                lastFigure = f;
                lastDots = dots;
            }

            //minimumRecordDuration = Time.min(minimumRecordDuration, new Time(lastFigure.getDurationWithDots(lastDots)));
        }

        private void checkAllNoteNameEqual(String text) throws GrammarParseRuntimeException {
            // check all letters are equal
            for (int i = 1; i < text.length(); i++) {
                if (text.charAt(i) != text.charAt(0)) {
                    throw new GrammarParseRuntimeException(
                            "The characters for a note name should be the same for specifying the octave, and we have '"
                                    + text + "'");
                }
            }
        }

        @Override
        public void enterTrebleNotes(kernParser.TrebleNotesContext ctx) {
            super.enterTrebleNotes(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "TrebleNotes {0}", ctx.getText());
            String t = ctx.getText();
            checkAllNoteNameEqual(t);
            octaveModif = ctx.getText().length() - 1;
            noteName = t.substring(0, 1).toUpperCase();

        }

        @Override
        public void enterBassNotes(kernParser.BassNotesContext ctx) {
            super.enterBassNotes(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "BassNotes {0}", ctx.getText());
            String t = ctx.getText();
            checkAllNoteNameEqual(t);
            octaveModif = -ctx.getText().length();
            noteName = t.substring(0, 1).toUpperCase();
        }

        @Override
        public void exitStem(kernParser.StemContext ctx) {
            super.exitStem(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Stem {0}", ctx.getText());
            if (ctx.BACKSLASH() != null) {
                lastStemDirection = StemDirection.down;
            } else if (ctx.SLASH() != null) {
                lastStemDirection = StemDirection.up;
            } else {
                throw new GrammarParseRuntimeException("Either slash or backslash expected, and found '" + ctx.getText() + "'");
            }
        }

        @Override
        public void exitNote(kernParser.NoteContext ctx) {
            super.exitNote(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST,
                    "Note {0}", ctx.getText());

            int octave = 4 + octaveModif;

            // check all letters are equal
            DiatonicPitch nn = DiatonicPitch.valueOf(noteName);

            Accidentals acc = Accidentals.NATURAL;
            if (ctx.alteration() !=
                    null) {
                switch (ctx.alteration().getText()) {
                    case "n":
                        acc = Accidentals.NATURAL;
                        break;
                    case "--":
                        acc = Accidentals.DOUBLE_FLAT;
                        break;
                    case "-":
                        acc = Accidentals.FLAT;
                        break;
                    case "#":
                        acc = Accidentals.SHARP;
                        break;
                    case "##":
                        acc = Accidentals.DOUBLE_SHARP;
                        break;
                    default:
                        throw new
                                GrammarParseRuntimeException("Unimplemented accidental: " +
                                ctx.alteration().getText());
                }
            }
            try {
                Time currentTime = getLastTime();
                SingleFigureAtom sse = null;
                if (inChord) {
                    if (chord == null) {
                        chord = new SimpleChord(lastFigure, lastDots);
                        sse = chord;
                        processPossibleImperfection(chord, currentTime);
                        addAtom(currentTime, chord);
                        //harm.setTime(currentTime);
                        chord.setStaff(getStaff());
                        lastNoteOrChord = chord;
                        Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                "New chord at time {0}", chord.getTime());
                        if (currentSpine.inTuplet) {
                            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                    "Chord added to tuplet");
                            currentSpine.tupletElements.add(chord);
                        }
                    } else {
                        if (lastDots != chord.getAtomFigure().getDots() || !lastFigure.equals(chord.getAtomFigure().getFigure())) {
                            throw new
                                    GrammarParseRuntimeException("The chord figure ( " +
                                    chord.getAtomFigure().getFigure() + ") or dots " +
                                    chord.getAtomFigure().getDots() + ") are different from last ones (" +
                                    lastFigure + "), (" + lastDots + ") dots");

                        }
                    }
                    chord.addPitch(new ScientificPitch(new PitchClass(nn, acc), octave));
                } else {
                    Atom previous = currentSpine.layer.isEmpty() ? null : currentSpine.layer.getLastAtom();
                    ScientificPitch sp = new ScientificPitch(nn, acc, octave);
                    SimpleNote sn = new SimpleNote(lastFigure, lastDots, sp);
                    sse = sn;
                    processPossibleImperfection(sn, currentTime);
                    if (currentSpine.inTuplet) {
                        Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                "Score note added {0} to tuplet", sn.toString());
                        currentSpine.tupletElements.add(sn);
                    } else {
                        //currentVoice.add(currentTime, sn);
                        if (currentSpine.ligatureNotes == null) {
                            addAtom(currentTime, sn);
                        } else {
                            currentSpine.ligatureNotes.add(sn);

                            if (currentSpine.ligatureEnded) {
                                try {
                                    LigaturaBinaria ligature = LigatureFactory.createLigature(currentSpine.ligatureNotes);

                                    addAtom(currentSpine.ligatureStartTime, ligature);

                                } catch (IM3Exception e) {
                                    Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, "Error creating ligature", e);
                                    throw new GrammarParseRuntimeException(e);
                                }
                                currentSpine.ligatureNotes = null;
                                currentSpine.ligatureEnded = false;
                            }

                        }
                        lastNoteOrChord = sn;
                    }
                    if (currentSpine == rootSpine) {
                        //TODO - en teoría no debería hacer falta, el hash
                        // stavesForSpines debería contenerlo - lo ponemos para depurar un error sn.setStaff(rootStaff);
                        /*2017 if (currentVoice != scoreSong.getAnalysisVoice()) {
                            throw new GrammarParseRuntimeException("The analysis voice is different from the current voice in the root spine"
                            );
                        }*/
                    } else {
                        sn.setStaff(getStaff());
                    }

                    if (ctx.afterNote() != null &&
                            (!ctx.afterNote().tiemiddle().isEmpty() ||
                                    !ctx.afterNote().tieend().isEmpty())) {
                        Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                "Tie found");
                        if (previous == null) {
                            throw new
                                    GrammarParseRuntimeException(
                                    "Tie found but no previous note present");
                        }
                        if (previous instanceof SimpleNote) {
                            sn.tieFromPrevious((SimpleNote) previous);
                        } else {
                            throw new GrammarParseRuntimeException(
                                    "Tie found but previous element is not a note, it is a " +
                                            previous.getClass().toString());
                        }
                    }

                   if (ctx.afterNote() != null && ctx.afterNote().pause() != null && !ctx.afterNote().pause().isEmpty()) {
                        Logger.getLogger(KernImporter.class.getName()).log(Level.INFO,
                                "Pause found");
                        sn.getStaff().addFermata(sn.getAtomFigure());
                    }

                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                            "Score note added {0}", sn.toString());

                    //TODO Root spine
                    /*if (inRootSpine()) {
                        lastRootNote = sn;
                        if (!isRootBeforeHarmonies()) {
                            if (lastHarmony != null) {
                                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                        "Setting root {0} to harmony {1}", new
                                                Object[]{sn.getPitch().getPitchClass(), lastHarmony});
                                lastHarmony.setRoot(sn.getPitch().getPitchClass());
                            }
                        }
                    }*/
                }
                
                if (sse != null && lastStemDirection != null) {
                    sse.setExplicitStemDirection(lastStemDirection);
                }
            } catch
                    (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
                        null, ex);
                throw new
                        GrammarParseRuntimeException(ex.getMessage());
            }
        }

        @Override
        public void enterNote(kernParser.NoteContext ctx) {
            lastStemDirection = null;
        }

        @Override
        public void enterChord(kernParser.ChordContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Enter harm {0}", ctx.getText());
            inChord = true;
            chord = null;
        }

        @Override
        public void exitChord(kernParser.ChordContext ctx) {
            inChord = false;
            chord = null;
        }

        @Override
        public void exitRest(kernParser.RestContext ctx) {

            super.enterRest(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST,
                    "Rest {0}", ctx.getText());
            try {
                //ctx.noteName().
                // TODO Crear rest con ritmo lastDuration
                Staff staff = getStaff();
                Time currentTime = getLastTime();
                SimpleRest rest = new SimpleRest(lastFigure, lastDots);

                processPossibleImperfection(rest, currentTime);
                //currentVoice.add(currentTime, rest);
                addAtom(currentTime, rest);

                //2017 rest.setStaff(getStaff()); // Beniarbeig 2014
                //measurescorenotes.add(rest);

                if (ctx.pause() != null && !ctx.pause().isEmpty()) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                            "Pause found");
                    staff.addFermata(rest.getAtomFigure());
                }

                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                        "Rest added {0}", rest.toString());
                if
                        (currentSpine.inTuplet) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                            "Rest added {0} to tuplet");
                    currentSpine.tupletElements.add(rest);
                }
            } catch (IM3Exception ex) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
                        null, ex);
                throw new GrammarParseRuntimeException(ex);
            }

        }

        private void processPossibleImperfection(SingleFigureAtom atom, Time lastTime) throws IM3Exception {
            atom.getAtomFigure().setColored(lastMensuralFigureColoured);
            if (lastMensuralPerfection != null) {
                if (lastMensuralPerfection.equals("p")) {
                    atom.getAtomFigure().setExplicitMensuralPerfection(Perfection.perfectum);
                } else if (lastMensuralPerfection.equals("i")) {
                    atom.getAtomFigure().setExplicitMensuralPerfection(Perfection.imperfectum);
                } else {
                    throw new IM3Exception("Invalid perfection: '" + lastMensuralPerfection + "'");
                }

            } /*else { //TODO ¿Cómo se coordina esto con lo de arriba de la imperfección? - con este else?
                TimeSignature meter = currentSpine.staff.getRunningTimeSignatureAt(lastTime);
                if (meter instanceof TimeSignatureMensural) {
                    TimeSignatureMensural mmeter = (TimeSignatureMensural) meter;
                    Time figureDuration = mmeter.getDuration(atom.getAtomFigure().getFigure(), atom.getAtomFigure().getDots());
                    atom.getAtomFigure().setSpecialDuration(figureDuration);
                }
            }*/
            lastMensuralPerfection = null;
            lastMensuralFigureColoured = false;
        }

        private void prepareFiguresForTuplet() {
            figureBeatsSortedForTupletProcessing = new TreeSet<>();
            for (Figures f : Figures.values()) {
                // figureRatiosSortedForTupletProcessing.add(f.getRatio());
                figureBeatsSortedForTupletProcessing.add(new Double(f.getMeterUnit()));
            }
        }

        // see http://www2.siba.fi/muste1/index.php?id=100&la=en
        // TODO Probar dosillo en compas compuesto
        // TODO Seguramente esto no esta tampoco hecho en el MusicXMLImporter

        /**
         * @return True if it is processed
         * @throws IM3Exception
         * @throws NoMeterException
         */
        private boolean processTuplet() throws IM3Exception, NoMeterException {
            // int nelementsInTuplet = currentSpine.tupletElements.size();
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                    "Trying to process tuplet with {0} symbols of kern figureAndDots with m.c.d. {1} and m.c.m. {2}",
                    new Object[]{currentSpine.tupletElements.size(), currentSpine.mcdTupleElementDuration,
                            currentSpine.mcm});

            // this is like the first part of tupletComplete but also adds the span
            int groupDuration = 0;
            ArrayList<Integer> spans = new ArrayList<>();
            int nelementsInTuplet = 0;
            for (Integer d : currentSpine.tupletDurations) {
                int span = currentSpine.mcm / d; // inverse figureAndDots
                // relation (longer
                // value = shorter
                // figureAndDots)
                spans.add(span);
                groupDuration += (currentSpine.mcm * span);
                nelementsInTuplet += span;
            }

            // int nelementsInTuplet = groupDuration /
            // currentSpine.mcdTupleElementDuration; //TODO Ver si puede dar
            // esto un valor no entero
            // Figures tupletDurationFigure = Figures.findBeats(groupDuration);
            TimeSignature ts = currentTimeSignature();
            // double tupletDurationRatio = tupletDurationFigure.getRatio();

            // double individualFigureRatio;
            double individualDuration;
            if (ts.isCompound()) {
                if (nelementsInTuplet != 2 && nelementsInTuplet != 4 && nelementsInTuplet != 5
                        && nelementsInTuplet != 7) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                            "Tuplet not complete yet {0} symbols in compount meter, expecting 2, 4, 5, or 7",
                            nelementsInTuplet);
                    return false; // not complete yet
                }
                // individualFigureRatio =
                // figureRatiosSortedForTupletProcessing.floor(tupletDurationRatio
                // / (double) nelementsInTuplet);
                individualDuration = figureBeatsSortedForTupletProcessing
                        .ceiling((double) groupDuration / (double) nelementsInTuplet);
            } else {
                if (nelementsInTuplet != 3 && nelementsInTuplet != 5 && nelementsInTuplet != 6 && nelementsInTuplet != 7
                        && nelementsInTuplet != 9) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                            "Tuplet not complete yet {0} symbols in simple meter, expecting 3, 5, 6, 7, or 9",
                            nelementsInTuplet);
                    return false; // not complete yet
                }
                individualDuration = figureBeatsSortedForTupletProcessing
                        .floor((double) groupDuration / (double) nelementsInTuplet);
                // individualFigureRatio =
                // figureRatiosSortedForTupletProcessing.ceiling(tupletDurationRatio
                // / (double) nelementsInTuplet);
            }
            int actualGroupDuration = (int) (groupDuration / nelementsInTuplet - individualDuration); // TODO
            // Esto es empirico, hay que comprobarlo bien
            Figures individualDurationFigure = Figures.findMeterUnit((int) individualDuration, NotationType.eModern);
            // ScoreFigureAndDots individualFigureDuration = new
            // ScoreFigureAndDots(Figures.findRatio(individualFigureRatio),
            // currentSpine.tupletDots); //TODO Ver lo que dice de los
            // puntillos, si se usan tuplets con puntillos en principio s�lo
            // tenemos que poner aqu� los puntillos
            //2017 ScoreFigureAndDots individualFigureDuration = new ScoreFigureAndDots(individualDurationFigure, currentSpine.tupletDots); // TODO
            // Ver lo que dice de los puntillos, si se usan tuplets con puntillos en principio solo
            // tenemos que poner aqui los puntillos

            // int inSpaceOfNotes = (int) ((double)tupletDurationRatio /
            // individualFigureDuration.getFigure().getRatio());
            // int inSpaceOfNotes = (int) (groupDuration / individualDuration);
            int inSpaceOfNotes = (int) (individualDuration / actualGroupDuration); // TODO
            // esto es empirico - va a la inversa, menor valor mayor duracion

            if (nelementsInTuplet * actualGroupDuration != currentSpine.mcm) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                        "Tuplet not complete yet {0} symbols * {1} actualGroupDuration != {2} mcm",
                        new Object[]{nelementsInTuplet, actualGroupDuration, currentSpine.mcm});
                return false;
            }

            ArrayList<Atom> tupletElements = new ArrayList<>();
            for (int i = 0; i < currentSpine.tupletElements.size(); i++) {
                SingleFigureAtom sse = currentSpine.tupletElements.get(i);
                //int dur = currentSpine.tupletDurations.get(i);
                // int span = dur / currentSpine.mcdTupleElementDuration;
                //tupletElements.addElementAndChangeItsOnsetAndDuration(sse, spans.get(i));
                //System.out.println("DUR: " + dur + "\t SPAN: " + spans.get(i));

                int span = spans.get(i);
                Figures figure;
                if (span == 1) {
                    figure = individualDurationFigure;
                } else {
                    figure = Figures.findMeterUnit(individualDurationFigure.getMeterUnit() / span, NotationType.eModern);
                }
                sse.setFigure(figure);
                tupletElements.add(sse);
            }

            SimpleTuplet tuplet = new SimpleTuplet(nelementsInTuplet, inSpaceOfNotes, individualDurationFigure,
                    //currentSpine.tupletDots
                    tupletElements
            );

            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Tuplet processed: {0}", tuplet.toString());


            currentSpine.inTuplet = false;
            currentSpine.tupletElements.clear();
            currentSpine.tupletDots = 0;
            currentSpine.tupletDurations.clear();
            //currentVoice.add(tuplet); // 2017
            addAtom(currentSpine.tupletStartTime, tuplet); // 2017
            return true;
        }



		/*
		 * @Override public void exitRepeatToken(kernParser.RepeatTokenContext
		 * ctx) { super.exitRepeatToken(ctx);
		 * Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST,
		 * "Repeat token {0}", ctx.getText()); ScoreSoundingElement lastToken =
		 * currentVoice.getScoreSoundingElements().last(); if (lastToken ==
		 * null) { throw new GrammarParseException(
		 * "Cannot repeat an empty token in row #" + currentRow + ", spine #" +
		 * currentSpineIndex); } //ScoreSoundingElement repeated =
		 * (ScoreSoundingElement) lastToken.clone(); ScoreSoundingElement
		 * repeated; if (lastToken instanceof Rest) { try { repeated = new
		 * Rest(currentVoice, getLastTime(), (ScoreFigureAndDots)
		 * lastToken.getDurationInTicks().clone()); // automaticly added } catch
		 * (IM3Exception ex) {
		 * Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
		 * null, ex); throw new GrammarParseException(
		 * "Repetition (cloning) of class " + lastToken.getClass().toString() +
		 * " not supported"); } } else if (lastToken instanceof ScoreNote) { try
		 * { repeated = new ScoreNote(currentVoice, getLastTime(),
		 * ((ScoreNote) lastToken).getPitchAndOctave().clone(), (ScoreFigureAndDots)
		 * lastToken.getDurationInTicks().clone()); // automaticly added } catch
		 * (IM3Exception ex) {
		 * Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
		 * null, ex); throw new GrammarParseException(
		 * "Repetition (cloning) of class " + lastToken.getClass().toString() +
		 * " not supported"); } } else { throw new GrammarParseException(
		 * "Repetition (cloning) of class " + lastToken.getClass().toString() +
		 * " not supported"); }
		 * Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
		 * "Added repeated element {0}", repeated.toString()); }
		 */

        @Override
        public void exitMordent(kernParser.MordentContext ctx) {
            super.exitMordent(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Mordent {0}", ctx.getText());
            // TODO Mordente no implementado
        }

        ///
		/*
		 * ScorePart getCurrentPart() { ScorePart part = currentVoice.getPart();
		 * if (part == this.tempPart) { //currentPart =
		 * scoreSong.addPart(number); if (globalPart == tempPart) {
		 * Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
		 * "Creating global part"); globalPart = scoreSong.addPart(); } part =
		 * globalPart;
		 * //Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
		 * "Creating part {0}", number);
		 * Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
		 * "Associating spine {0} to global part", new
		 * Object[]{currentSpineIndex}); try {
		 * //part.addVoice(currentSpineIndex, currentVoice); if
		 * (currentVoice.getNumber() != currentSpineIndex) { throw new
		 * GrammarParseRuntimeException("Spine index != currentVoice.number");
		 * // Beniarbeig } tempPart.removeVoice(currentVoice);
		 * part.addVoice(currentVoice); currentVoice.setPart(part); //
		 * Beniarbeig 2014 } catch (IM3Exception ex) {
		 * Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE,
		 * null, ex); throw new GrammarParseRuntimeException(ex); } } return
		 * part; }
		 */

        @Override
        public void exitBarline(kernParser.BarlineContext ctx) {
            super.exitBarline(ctx);
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Barline {0}", ctx.getText());

            // only process last spine barline
            Time currentTime = getLastTime();
            Staff staff = null;
            try {
                staff = getStaff();
            } catch (IM3Exception e) {
                throw new GrammarParseRuntimeException(e);
            }

            if (staff.getNotationType() == NotationType.eMensural) {
                MarkBarline markBarline = new MarkBarline(currentTime);
                try {
                    staff.addElementWithoutLayer(markBarline);
                } catch (IM3Exception e) {
                    throw new GrammarParseRuntimeException(e);
                }
            } else {
                //System.out.println("exitBarLine: " + ctx.getText() + ", spine " + currentSpineIndex + ", currentTime " + currentTime + ", measureInserted " + measureInserted);
                if (kernSpineFound && scoreSong != null && !measureInserted && lastTime != null) {
                    measureInserted = true;
                    int barNumber;
                    if (ctx.NUMBER() == null) {
                        //try {
                            barNumber = scoreSong.getMeaureCount() + 1;
                            Logger.getLogger(KernImporter.class.getName()).log(Level.FINE,
                                    "Barline without number, assigning {0}", barNumber);
                        /*} catch (IM3Exception ex) {
                            Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                            throw new GrammarParseRuntimeException(ex);
                        }*/
                    } else {
                        barNumber = new Integer(ctx.NUMBER().getText());
                        Logger.getLogger(KernImporter.class.getName()).log(Level.FINE, "Barline with number {0}",
                                barNumber);
                    }
                    try {
                        if (lastMeasure != null) {
                            lastMeasure.setEndTime(lastTime);
                        }

                        if (lastMeasure == null && !lastTime.isZero()) { // probably this will be an anacrusis
                            lastMeasure = new Measure(scoreSong, 0);
                            scoreSong.addMeasure(Time.TIME_ZERO, lastMeasure);
                            lastMeasure.setEndTime(lastTime);
                        }

                        currentMeasure = new Measure(scoreSong, barNumber);
                        scoreSong.addMeasure(lastTime, currentMeasure);

                        /*TimeSignature ts = currentTimeSignature();
                        if (ts instanceof ITimeSignatureWithDuration) {
                            Time measureDuration = ((ITimeSignatureWithDuration) ts).getMeasureDuration();
                            currentMeasure.setEndTime(currentMeasure.getTime().add(measureDuration));
                        } else {
                            throw new ImportException("Cannot infer the measure duration with a time signature without duration (" + ts + ") at measure " + currentMeasure);
                        }*/

                        lastMeasure = currentMeasure;
                    } catch (Exception ex) {
                        Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, "Error creating measure " + barNumber, ex);
                        throw new GrammarParseRuntimeException(ex);
                    }
                }
            }
        }

        @Override
        public void exitSpineTerminator(kernParser.SpineTerminatorContext ctx) {
            Logger.getLogger(KernImporter.class.getName()).log(Level.FINEST, "Spine terminator for spine {0}",
                    currentSpineIndex);
        }


        @Override
        public void exitSpineSplit(kernParser.SpineSplitContext ctx) {
            super.exitSpineSplit(ctx);
            Spine newSpine = prepareSpine(globalPart, currentSpineIndex+1);
            newSpine.lastDuration = currentSpine.lastDuration;
            newSpine.staff = currentSpine.staff;
            newSpine.notationType = currentSpine.notationType;
            newSpine.splittedFrom = currentSpine;
            newSpine.splittedInto = newSpine;
            currentSpine.staff.addLayer(newSpine.layer);
            Logger.getLogger(KernImporter.class.getName()).log(Level.INFO, "Adding spine #" + currentSpineIndex);
            currentSpineIndex++; // important when a sequence of *^ is found
            // add a spine in a new layer
            /*getStaff()

            stavesForSpines.put(currentSpineIndex, staff);
            ScoreLayer layer = spines.get(currentSpineIndex);
            if (layer == null) {
                throw new IM3RuntimeException("Spine " + currentSpineIndex + " has not a layer (maybe the **mens or **kern is missing)");
            }
            staff.addLayer(0, layer);*/
        }

        @Override
        public void exitSpineJoin(kernParser.SpineJoinContext ctx) {
            super.exitSpineJoin(ctx);
            if (currentSpine.splittedFrom != null) {
                /*try {
                    updateLastTime();
                } catch (IM3Exception e) {
                    throw new IM3RuntimeException("Cannot update time: " + e);
                }*/
                if (spinesToRemove == null) {
                    spinesToRemove = new ArrayList<>();
                }
                spinesToRemove.add(currentSpine);
            }
        }

        @Override
        public void exitLigatureStart(kernParser.LigatureStartContext ctx) {
            currentSpine.ligatureNotes = new ArrayList<>();
            currentSpine.ligatureStartTime = lastTime;
        }

        @Override
        public void exitLigatureEnd(kernParser.LigatureEndContext ctx) {
            currentSpine.ligatureEnded = true;
        }


        //// ----------- HARM --------
        Harm harm;
        Harm alternateHarm;
        Harm lastHarm;
        boolean inAlternateHarm;
        ChordSpecification lastChordSpecification;
        ChordInversion lastInversion;
        QualifiedDegree lastDegree;
        ChordIntervalQuality lastChordQuality;
        ArrayList<ChordInterval> lastChordIntervals;
        ChordRootAlteration lastAlteration;

        @Override
        public void enterHarm(kernParser.HarmContext ctx) {
            super.enterHarm(ctx);
            harm = null;
        }

        @Override
        public void exitHarm(kernParser.HarmContext ctx) {
            super.exitHarm(ctx);
            if (scoreSong != null && kernSpineFound) { // if importing whole song, not only harmonies
                harm.setTime(getLastTime());
                try {
                    scoreSong.addHarm(harm);
                } catch (IM3Exception ex) {
                    Logger.getLogger(KernImporter.class.getName()).log(Level.SEVERE, null, ex);
                    throw new GrammarParseRuntimeException(ex);
                }
            }
        }

        @Override
        public void enterHarmSpecification(kernParser.HarmSpecificationContext ctx) {
            super.enterHarmSpecification(ctx);
            lastChordSpecification = null;
            lastInversion = null;
            lastDegree = null;
            lastAlteration = null;
        }

        @Override
        public void exitHarmSpecification(kernParser.HarmSpecificationContext ctx) {
            super.exitHarmSpecification(ctx);
            if (inAlternateHarm) {
                if (alternateHarm == null) {
                    alternateHarm = new Harm(lastHarmKey, lastChordSpecification);
                } else {
                    alternateHarm.addChordSpecification(lastChordSpecification);
                }
            } else {
                if (harm == null) {
                    harm = new Harm(lastHarmKey, lastChordSpecification);
                } else {
                    harm.addChordSpecification(lastChordSpecification);
                }
            }
        }

        @Override
        public void enterAlternateHarm(kernParser.AlternateHarmContext ctx) {
            super.enterAlternateHarm(ctx);
            //alternatedChordSpecification = lastChordSpecification;
            inAlternateHarm = true;
        }

        @Override
        public void exitAlternateHarm(kernParser.AlternateHarmContext ctx) {
            super.exitAlternateHarm(ctx);
            harm.setAlternate(alternateHarm);
            inAlternateHarm = false;
        }


        @Override
        public void exitImplicitChordSpecification(kernParser.ImplicitChordSpecificationContext ctx) {
            super.exitImplicitChordSpecification(ctx);
            lastChordSpecification.setImplicit(true);
        }

        @Override
        public void exitSpecialChordType(kernParser.SpecialChordTypeContext ctx) {
            super.exitSpecialChordType(ctx);
            TerminalNode typeNode = (TerminalNode) ctx.getChild(0);

            switch (typeNode.getSymbol().getType()) {
                case kernLexer.FRENCH:
                    lastChordSpecification = new FrenchChord();
                    break;
                case kernLexer.ITALIAN:
                    lastChordSpecification = new ItalianChord();
                    break;
                case kernLexer.NEAPOLITAN:
                    lastChordSpecification = new NeapolitanChord();
                    break;
                case kernLexer.TRISTAN:
                    lastChordSpecification = new TristanChord();
                    break;
                /*case kernLexer.GERMAN:
                    lastChordSpecification = new GermanChord();
                    break;*/
                default:
                    if (typeNode.getSymbol().getText().equals("Gn")) {
                        lastChordSpecification = new GermanChord();
                    } else {
                        throw new GrammarParseRuntimeException("Unknown special harm: " + ctx.getText());
                    }
            }
        }

        @Override
        public void exitSpecialChord(kernParser.SpecialChordContext ctx) {
            super.exitSpecialChord(ctx);
            SpecialChord lastSpecialChord = (SpecialChord) lastChordSpecification;
            lastSpecialChord.setEnharmonicSpelling(ctx.TILDE() != null);
            if (ctx.inversion() != null) {
                lastSpecialChord.setInversion(lastInversion);
            }
        }

        @Override
        public void exitInversion(kernParser.InversionContext ctx) {
            super.exitInversion(ctx);
            TerminalNode typeNode = (TerminalNode) ctx.getChild(0);

            switch (typeNode.getSymbol().getType()) {
                case kernLexer.FIRSTINVERSION:
                    lastInversion = ChordInversion.first;
                    break;
                case kernLexer.SECONDINVERSION:
                    lastInversion = ChordInversion.second;
                    break;
                case kernLexer.THIRDINVERSION:
                    lastInversion = ChordInversion.thrird;
                    break;
                case kernLexer.FOURTHINVERSION:
                    lastInversion = ChordInversion.fourth;
                    break;
                case kernLexer.FIFTHINVERSION:
                    lastInversion = ChordInversion.fifth;
                    break;
                case kernLexer.SIXTHINVERSION:
                    lastInversion = ChordInversion.sixth;
                    break;
                default:
                    throw new GrammarParseRuntimeException("Unknown inversion: " + ctx.getText());

            }
        }

        private ChordInterval [] getLastChordIntervals() {
            if (lastChordIntervals == null) {
                return null;
            }

            ChordInterval [] cis = new ChordInterval[lastChordIntervals.size()];
            for (int i=0; i<cis.length; i++) {
                cis[i] = lastChordIntervals.get(i);
            }
            return cis;
        }


        @Override
        public void exitNonFunctionalChord(kernParser.NonFunctionalChordContext ctx) {
            super.exitNonFunctionalChord(ctx);
            NonFunctionalChord nfc = new NonFunctionalChord();
            nfc.setIntervals(getLastChordIntervals());
            lastChordSpecification = nfc;
        }

        @Override
        public void enterDegree(kernParser.DegreeContext ctx) {
            super.enterDegree(ctx);
            lastDegree = new QualifiedDegree();
        }

        @Override
        public void exitMajorDegree(kernParser.MajorDegreeContext ctx) {
            super.exitMajorDegree(ctx);
            lastDegree.setDegreeType(DegreeType.major);
            lastDegree.setDegree(parseDegree(ctx.getText()));
        }

        @Override
        public void exitMinorDegree(kernParser.MinorDegreeContext ctx) {
            super.exitMinorDegree(ctx);
            lastDegree.setDegreeType(DegreeType.minor);
            lastDegree.setDegree(parseDegree(ctx.getText()));
        }

        @Override
        public void exitAugmentedDegree(kernParser.AugmentedDegreeContext ctx) {
            super.exitAugmentedDegree(ctx);
            lastDegree.setDegreeType(DegreeType.augmented);
            //already read in exitMajorDegree lastDegree.setDegree(parseDegree(ctx.getText()));
        }

        @Override
        public void exitDiminishedDegree(kernParser.DiminishedDegreeContext ctx) {
            super.exitDiminishedDegree(ctx);
            lastDegree.setDegreeType(DegreeType.diminished);
            // already read in exitMinorDegree lastDegree.setDegree(parseDegree(ctx.getText()));
        }

        private Degree parseDegree(String degree) {
            return Degree.valueOf(degree.toUpperCase());
        }


        @Override
        public void enterExtensions(kernParser.ExtensionsContext ctx) {
            lastChordIntervals = new ArrayList<>();
        }

        @Override
        public void exitExtensions(kernParser.ExtensionsContext ctx) {
            super.exitExtensions(ctx);
            for (kernParser.ExtensionContext ec: ctx.extension()) {
                ChordInterval chordInterval = new ChordInterval();
                if (ec.intervalQuality() != null) {
                    chordInterval.setQuality(lastChordQuality);
                }
                chordInterval.setInterval(Integer.parseInt(ec.NUMBER().getSymbol().getText()));
                lastChordIntervals.add(chordInterval);
            }
        }

        @Override
        public void enterExtension(kernParser.ExtensionContext ctx) {
            super.enterExtension(ctx);
            lastChordQuality = null;
        }

        @Override
        public void exitIntervalQuality(kernParser.IntervalQualityContext ctx) {
            super.exitIntervalQuality(ctx);

            TerminalNode typeNode = (TerminalNode) ctx.getChild(0);

            switch (typeNode.getSymbol().getType()) {
                case kernLexer.MAJOR_INTERVAL:
                    lastChordQuality = ChordIntervalQuality.major;
                    break;
                case kernLexer.MINOR_INTERVAL:
                    lastChordQuality = ChordIntervalQuality.minor;
                    break;
                case kernLexer.PERFECT_INTERVAL:
                    lastChordQuality = ChordIntervalQuality.perfect;
                    break;
                case kernLexer.DIMINISHED_INTERVAL:
                    if (ctx.getChildCount() == 2) {
                        lastChordQuality = ChordIntervalQuality.double_diminished;
                    } else {
                        lastChordQuality = ChordIntervalQuality.diminished;
                    }
                    break;
                case kernLexer.AUGMENTED_INTERVAL:
                    if (ctx.getChildCount() == 2) {
                        lastChordQuality = ChordIntervalQuality.double_augmented;
                    } else {
                        lastChordQuality = ChordIntervalQuality.augmented;
                    }
                    break;
                default:
                    throw new GrammarParseRuntimeException("Unknown quality: " + ctx.getText());

            }
        }

        @Override
        public void enterRomanNumberChordSpecification(kernParser.RomanNumberChordSpecificationContext ctx) {
            super.enterRomanNumberChordSpecification(ctx);
            lastChordSpecification = new RomanNumberChordSpecification();
            lastChordIntervals = null;
        }

        @Override
        public void exitRomanNumberChordSpecification(kernParser.RomanNumberChordSpecificationContext ctx) {
            super.exitRomanNumberChordSpecification(ctx);
            RomanNumberChordSpecification rnc = new RomanNumberChordSpecification();
            rnc.setRoot(lastDegree);
            rnc.setInversion(lastInversion);
            rnc.setExtensions(getLastChordIntervals());
            rnc.setAlteration(lastAlteration);
            lastChordSpecification = rnc;
        }

        @Override
        public void exitRootAlteration(kernParser.RootAlterationContext ctx) {
            super.exitRootAlteration(ctx);
            TerminalNode typeNode = (TerminalNode) ctx.getChild(0);

            switch (typeNode.getSymbol().getType()) {
                case kernLexer.OCTOTHORPE:
                    lastAlteration = ChordRootAlteration.raised;
                    break;
                case kernLexer.MINUS:
                    lastAlteration = ChordRootAlteration.lowered;
                    break;
                default:
                    throw new GrammarParseRuntimeException("Unknown alteration: " + ctx.getText());
            }
        }

        @Override
        public void exitEndOfFile(kernParser.EndOfFileContext ctx) {
            super.exitEndOfFile(ctx);
            this.eofReached = true;
        }
    }

    private void postProcess(ScoreSong scoreSong) throws IM3Exception, ImportException {
        TimeSignature ts = scoreSong.getUniqueMeterWithOnset(Time.TIME_ZERO);

        if (scoreSong.hasMeasures()) {
            Measure lastMeasure = scoreSong.getLastMeasure();
            if (!lastMeasure.hasEndTime()) {
                Time songDuration = scoreSong.getSongDuration();
                if (songDuration.equals(lastMeasure.getTime())) {
                    // empty last measure, remove it
                    scoreSong.removeMeasure(lastMeasure);
                } else {
                    lastMeasure.setEndTime(songDuration);
                }
            }
            // check anacrusis
            if (scoreSong.getNumMeasures() > 0) {
                Time expectedMeasureDuration = ts.getDuration();
                Measure fm = scoreSong.getFirstMeasure();
                Time diff = expectedMeasureDuration.substract(fm.getDuration());
                scoreSong.setAnacrusisOffset(diff);
            }
        }

        /*for (Staff staff : scoreSong.getStaves()) {
            TimeSignature ts = staff.getTimeSignatureWithOnset(Time.TIME_ZERO);

            // check anacrusis
            if (ts instanceof ITimeSignatureWithDuration) { // if not, it cannot be an anacrusis
                Time maxEndTime = Time.TIME_ZERO;
                List<Atom> atomsFirstMeasure = staff.getAtomsWithOnsetWithin(scoreSong.getFirstMeasure());
                for (Atom atom : atomsFirstMeasure) {
                    maxEndTime = Time.max(maxEndTime, atom.getOffset());
                }
                Time measureDuration = ((ITimeSignatureWithDuration) ts).getMeasureDuration();
                int diff = maxEndTime.compareTo(measureDuration);
                if (diff < 0) {
                    scoreSong.setAnacrusisOffset(measureDuration.substract(maxEndTime));
                } else if (diff > 0) {
                    throw new ImportException("Fist measure duration based on atom is " + maxEndTime + " and expected first measure duration based on time signature is " + measureDuration);
                } // else normal measure
            }
        }*/


    }


    // @Override
    public ScoreSong importSong(File file) throws ImportException {
        ErrorListener errorListener = new ErrorListener();
        try {
            Logger.getLogger(KernImporter.class.getName()).log(Level.INFO, "Parsing {0}", file.getAbsoluteFile());
            InputStream is = new FileInputStream(file);
            kernLexer lexer = new kernLexer(CharStreams.fromPath(file.toPath()));
            lexer.addErrorListener(errorListener);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            kernParser parser = new kernParser(tokens);
            parser.addErrorListener(errorListener);

            // parser.setErrorHandler(new BailErrorStrategy());
            // parser.setBuildParseTree(true); // tell ANTLR to build a parse
            // tree
            ParseTree tree = parser.song();
            ParseTreeWalker walker = new ParseTreeWalker();
            ScoreSong song = new ScoreSong();
            Loader loader = new Loader(song, durationEvaluator);
            walker.walk(loader, tree);
            if (errorListener.getNumberErrorsFound() != 0) {

                throw new ImportException(errorListener.getNumberErrorsFound() + " errors found in "
                        + file.getAbsolutePath() + "\n" + errorListener.toString());
            }

            if (!loader.eofReached) {
                throw new ImportException("The end of file has not been reached");
            }
            // return song;

            // loader.setRootNotesToHarmonies();
            ScoreSong ssong;
            ssong = loader.scoreSong;
            ssong.invertPartAndVoiceNumbering(); // the kern is written from
            // bass to treble
            postProcess(ssong);

            //ssong.moveAnalysisPartToBottom();
            //if (loader.rootPart != null) {
            //    ScoreLayer v = loader.spines.get(loader.rootSpine);
            //    if (v != null) {
            //        throw new UnsupportedOperationException("TODO refactorización");

					/*
					 * if (loader.rootPart.getStaves().isEmpty()) { throw new
					 * ImportException("The root part contains no staff"); } if
					 * (loader.rootPart.getStaves().size() > 1) { throw new
					 * ImportException("The root part contains > 1 staff"); }
					 * 
					 * replaceRootnotesForAnalysisHooks(loader.scoreSong);
					 */
//                } else {
            //                  Logger.getLogger(KernImporter.class.getName()).log(Level.WARNING,
            //"No root spine to be imported as voice");
            //  }
            //}
            return song;
        } catch (Throwable e) {
            e.printStackTrace();
            Logger.getLogger(KernImporter.class.getName()).log(Level.WARNING, "Import error {0}", e.getMessage());
            for (ParseError pe : errorListener.getErrors()) {
                Logger.getLogger(KernImporter.class.getName()).log(Level.WARNING, "Parse error: {0}", pe.toString());
            }

            throw new ImportException(e.getMessage());
        }
    }

    public Harm readHarmony(Key harmKey, String string) throws ImportException {
        try {
            CharStream input = CharStreams.fromString(string);
            kernLexer lex = new kernLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lex);
            kernParser parser = new kernParser(tokens);
            ErrorListener errorListener = new ErrorListener();
            parser.addErrorListener(errorListener);
            ParseTree tree = parser.harm();
            ParseTreeWalker walker = new ParseTreeWalker();
            Loader loader = new Loader(harmKey);
            walker.walk(loader, tree);
            if (errorListener.getNumberErrorsFound() != 0) {

                throw new ImportException(errorListener.getNumberErrorsFound() + " errors found in "
                        + string);
            }
            return loader.harm;
        } catch (Exception e) {
            System.err.println("Input: " + string);
            e.printStackTrace();
            throw new ImportException(e.toString());
        }
    }

    /**
     * Remove all notes in the root part and create an analysis hook for each
     * possible subdivision (the minimum if each bar)
     */
	/*2017 private void replaceRootnotesForAnalysisHooks(StaffAnalysisLayer staffLayer, ScoreSong song) throws IM3Exception {
		song.getAnalysisVoice().clearElementsWithRhythm();
		song.createAnalysisHooks(staffLayer);
	}*/
    public static void main(String[] args) {
		/*
		 * Logger.getLogger(KernImporter.class.getName()).info(
		 * "Changing level to FINEST to the stderr"); // LOG this level to the
		 * log Logger.getLogger(KernImporter.class.getName()).setLevel(Level.
		 * FINESTST);
		 * 
		 * ConsoleHandler handler = new ConsoleHandler(); // PUBLISH this level
		 * handler.setLevel(Level.FINESTST);
		 * Logger.getLogger(KernImporter.class.getName()).addHandler(handler);
		 */

        // File file = new File("testdata/kern/base_tuplet.krn");
        // File file = new File("testdata/kern/harm-rep.krn");
        // File file = new File("/tmp/guide02-example2-1.krn");
        // File file = new File("/tmp/guide06-example6-2.krn");
        KernImporter instance = new KernImporter();
        // ScoreSong song = instance.importSong(new
        // File("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/harmonizedchorals/KERN-SCORES/chor048.krn"));
        // System.out.println(song.getAllPitches());

        // ScoreSong expResult = new MusicXMLImporter().importSong(new
        // File("/Users/drizo/cmg/investigacion/training_sets/sources/tonalanalysis/harmonizedchorals/KERN-SCORES/chor048.xml"));
    }

    /**
     * Remove all root notes and set analysis hooks with the smallest note
     * figureAndDots of each bar
     */
}
