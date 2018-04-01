package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.ISongExporter;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static es.ua.dlsi.im3.core.score.io.XMLExporterHelper.*;

public class MusicXMLExporter implements ISongExporter {
    protected ScoreSong scoreSong;
    protected StringBuilder sb;
    private static final String MUSICXML_VERSION = "3.1";
    private static final String PARTWISE_DOCTYPE = "<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML "
            + MUSICXML_VERSION + " Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">\n";

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";

    /**
     * Easier to set it fixed than compute it
     */
    private static final Time divisions = new Time(96, 1);

    // some flags
    private boolean printVoiceNames;
    private boolean printRomanNumberAnalysis;

    private ScoreLayer lastVoice = null;

    /* TODO: calcado e invertido de MusicXMLSAXScoreSongImporter. ¿refactorizar esto en una clase utilidad? */
    static final HashMap<Figures, String> FIGURES = new HashMap<>();
    private static final String MIDDLE_TIE = "__middle__";

    // TODO MusicXML figures, ¿normalizar en MusicXMLImporter?
    static {
        FIGURES.put(Figures.BREVE, "breve");
        FIGURES.put(Figures.WHOLE, "whole");
        FIGURES.put(Figures.HALF, "half");
        FIGURES.put(Figures.QUARTER, "quarter");
        FIGURES.put(Figures.EIGHTH, "eighth");
        FIGURES.put(Figures.SIXTEENTH, "16th");
        FIGURES.put(Figures.THIRTY_SECOND, "32th");
        FIGURES.put(Figures.SIXTY_FOURTH, "64th");
        FIGURES.put(Figures.HUNDRED_TWENTY_EIGHTH, "128th");
        FIGURES.put(Figures.TWO_HUNDRED_FIFTY_SIX, "256th");
    }

    private int backup;

    private int multiMeasureRestRemaining =0;
    private boolean lastMeasure=false;

    /**
     * @return the printRomanNumberAnalysis
     */
    public final boolean isPrintRomanNumberAnalysis() {
        return printRomanNumberAnalysis;
    }

    /**
     * Not printed in Finale !!!
     *
     * @param printRomanNumberAnalysis the printRomanNumberAnalysis to set
     */
    public final void setPrintRomanNumberAnalysis(boolean printRomanNumberAnalysis) {
        this.printRomanNumberAnalysis = printRomanNumberAnalysis;
    }

    /**
     * Returns the beam index of a given note.
     *
     * @param note note to compute the beam for
     * @return the beam index, e. g., 0 for quarter notes or larger, 1 for 8ths, 2 for 16ths, etc.
     */
    private final int computeBeamIndex(SimpleNote note) {
        Time dur = note.getDuration();
        if (dur.getExactTime().getNumerator() != 1)
            return 0; // no beam
        else {
            return note.getAtomFigure().getFigure().getNumFlags();
        }
    }

    /**
     * Default constructor
     */
    public MusicXMLExporter() {
        this.scoreSong = null;
        this.sb = null;
        this.printVoiceNames = true;
        this.printRomanNumberAnalysis = false;
        this.backup = 0;
        this.multiMeasureRestRemaining =0;
        this.lastMeasure=false;
    }

    /**
     * Constructor
     *
     * @param printVoiceNames whether to print voice names
     */
    public MusicXMLExporter(boolean printVoiceNames) {
        this.scoreSong = null;
        this.sb = null;
        this.printVoiceNames = printVoiceNames;
        this.printRomanNumberAnalysis = false;
        this.backup = 0;
        this.multiMeasureRestRemaining =0;
        this.lastMeasure=false;
    }

    /**
     * Returns a name for a part given a score part
     *
     * @param part the score part
     * @return a score part name
     */
    private String computePartID(ScorePart part) {
        return "P" + part.getNumber();
    }

    /* from im2. PIERRE: I don't know what is this for, yet.
    // used to export harmomies
    class _HarmomyNoteDecoration implements IScoreSoundingElementDecoration {
        Harmony harmony;
        public _HarmomyNoteDecoration(Harmony harmony) {
            this.harmony = harmony;
        }
    };
    */

    @Override
    public void exportSong(File file, ScoreSong song) throws ExportException {
        this.scoreSong = song;
        sb = new StringBuilder();

        PrintStream ps = null;
        try {
            ps = new PrintStream(file, "UTF-8");
            ps.print(exportSong());
        } catch (Exception e) {
            throw new ExportException(e);
        }
        if (ps != null) {
            ps.close();
        }
    }

    /**
     * TODO PIERRE
     *
     * @return
     * @throws IM3Exception
     * @throws ExportException
     */
    public String exportSong() throws IM3Exception {
        sb = new StringBuilder();

        sb.append(XML);
        sb.append(PARTWISE_DOCTYPE);
        start(sb, 0, "score-partwise", "version", MUSICXML_VERSION);
        start(sb, 1, "work");
        startEndTextContentSingleLine(sb, 2, "work-title", scoreSong.getTitle());
        end(sb, 1, "work");
        start(sb, 1, "identification");
        startEndTextContentSingleLine(sb, 2, "creator", scoreSong.getComposer(), "type", "composer");
        start(sb, 2, "encoding");
        startEndTextContentSingleLine(sb, 3, "software", "IM3");

        // TEST Con este formato: 2016-11-15
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        startEndTextContentSingleLine(sb, 3, "encoding-date", df.format(new Date()));
        // Should we add these 'supports' tags? (see simple1.xml)
        startEnd(sb, 3, "supports", "element", "accidental", "type", "yes");
        // TODO add more supports tags (or do not)
        end(sb, 2, "encoding");
        end(sb, 1, "identification");

         /* Currently not needed

         start(sb, 1, "defaults");
          start(sb, 2, "scaling");
          end(sb, 2, "scaling");
          start(sb, 2, "page-layout");
          end(sb, 2, "page-layout");
          startEnd(sb, 2, "word-font", "font-family", "FreeSerif", "font-size", "10");
          startEnd(sb, 2, "lyric-font", "font-family", "FreeSerif", "font-size", "11");

          // separation between staves
          // <staff-layout><staff-distance>200</staff-distance></staff-layout>
          start(sb, 2, "staff-layout");
           startEndTextContentSingleLine(sb, 3, "staff-distance", "200");
          end(sb, 2, "staff-layout");

         end(sb, 1, "defaults");
    */

         /* TODO: Interfers with <work> and <identification> in MuseScore
        start(sb, 1, "credit", "page", "1");
        startEndTextContentSingleLine(sb, 3, "credit-words", scoreSong.getTitle(), "justify", "center", "valign", "top", "font-size", "24");
        end(sb, 1, "credit");
        start(sb, 1, "credit", "page", "1");
        startEndTextContentSingleLine(sb, 3, "credit-words", scoreSong.getComposer(), "justify", "right", "valign", "bottom", "font-size", "12");
        end(sb, 1, "credit");
        */

        exportPartList();
        exportParts();

        end(sb, 0, "score-partwise");

        return sb.toString();
    }

    /**
     * Exports the list of parts in the score
     */
    private void exportPartList() {
        start(sb, 1, "part-list");
        ArrayList<ScorePart> parts = scoreSong.getParts();
        for (int i = 0; i < parts.size(); i++) {
            //ScorePart part = movement.getScorePart(i);
            ScorePart voice = parts.get(i);
            // TODO: not sure: was voice.getAtomFigures() in im2
            if (voice.getAtomFigures().size() > 0) {
                start(sb, 2, "score-part", "id", computePartID(voice));
                startEndTextContentSingleLine(sb, 3, "part-name", (printVoiceNames ? (voice.getName() != null ? voice.getName() : "Voice " + (i + 1)) : ""));
                start(sb, 3, "score-instrument", "id", computePartID(voice) + "-" + voice.__getID());
                // TODO: get the real instrument for the part
                startEndTextContentSingleLine(sb, 4, "instrument-name", "Piano");
                end(sb, 3, "score-instrument");
                // No MIDI stuff : channel, program, volume, pan (see simple1.xml)
                end(sb, 2, "score-part");
            }
        }
        end(sb, 1, "part-list");
    }

    private void exportParts() throws IM3Exception {
        Collection<Measure> measures = scoreSong.getMeasures();
        //ScorePart part = movement.getScorePart(ip); // PIERRE: what is this??
        boolean harmoniesExported = false;
        boolean tempoExported = false; // TODO: exports only the first tempo indication , for now.
        for (ScorePart part : scoreSong.getPartsSortedByNumberAsc()) {
            lastVoice = null;
            lastMeasure=false;
            /* TODO
            if (!harmoniesExported) {
                // in order to export the harmonies, we create a PlayedNote decorator that contains the harmony. This object is
                // set only for the first note just after (or at) the onset of a given harmony
                for (Harmony harmony : scoreSong.getHarmonies()) {
                    // TODO: not very clear this will work... was
                    // ScoreSoundingElement snote = part.getFirstScoreSoundingElementAtTime(harmony.getTime());
                    AtomFigure snote = part.getAtomFiguresWithOnsetWithin(harmony.getTime(),harmony.getTime()).get(0);
                    // TODO im2 : snote.addDecoration(new _HarmomyNoteDecoration(harmony));
                }
                harmoniesExported = true;
            } */

            if (!part.isEmpty()) {
                start(sb, 1, "part", "id", computePartID(part));
                // TODO im2 : boolean F4Clef = part.needsF4Clef();
                Iterator<Measure> itm = measures.iterator();

                // I don't know which staff contains the manual system breaks, so I iterate over all staffs
                HashMap<Time, SystemBreak> system_breaks = new HashMap<>();
                for (Staff st : part.getStaves())
                    system_breaks.putAll(st.getSystemBreaks());

                while (itm.hasNext()) {
                    Measure measure = itm.next();

                    if (!itm.hasNext()) lastMeasure=true;

                    Tempo tempo = null;
                    if (!tempoExported) {
                        tempo = scoreSong.getTempoAtTimeOrNull(measure.getTime()); // initial tempo
                        if (tempo != null) tempoExported = true;
                    }

                    openBar(false, measure, part, tempo, system_breaks);

                    if (multiMeasureRestRemaining==0) {
                        Collection<ScoreLayer> layers = part.getLayers();
                        Iterator<ScoreLayer> iterator = layers.iterator();
                        while (iterator.hasNext()) {
                            ScoreLayer layer = iterator.next();
                            if (!iterator.hasNext())
                                lastVoice = layer;
                            SortedSet<AtomFigure> snotes = layer.getAtomFiguresSortedByTimeWithin(measure);
                            for (AtomFigure note : snotes) {
                        /* TODO: handle functional analysis
                        _HarmomyNoteDecoration hnd = (_HarmomyNoteDecoration) note.getDecoration(_HarmomyNoteDecoration.class);
                        if (hnd != null) {
                            //System.out.println("Scando " + hnd.harmony.toString());
                            out.println("<harmony>");
                            if (printRomanNumberAnalysis) {
                                out.print("\t<function>");
                                out.print(hnd.harmony.getDegreeString());
                                out.println("\t</root>");
                            } else {
                                out.println("\t<root>");
                                out.print("\t\t<root-step>");
                                out.print(hnd.harmony.getRoot().getNoteName().toString());
                                out.println("</root-step>");
                                out.print("\t\t<root-alter>");
                                out.print(hnd.harmony.getRoot().getAccidental().getAlteration());
                                out.println("</root-alter>");
                                out.println("\t</root>");
                            }
                            out.print("\t<kind>");
                            if (hnd.harmony != null && hnd.harmony.getChordType() != null) {
                                out.print(hnd.harmony.getChordType().getEquivalentHarmonyKind().toString().toLowerCase().replace('_', '-'));
                                //System.out.println(hnd.harmony.getDegreeString() + " " + hnd.harmony.getChordType().getEquivalentHarmonyKind().toString().toLowerCase().replace('_', '-'));
                            }
                            out.println("</kind>");

//							out.println("\t<degree>");
//							out.print("\t\t<degree-value>");
//							out.print(hnd.harmony.getDegree());
//							out.println("</degree-value>");
//							out.println("\t</degree>");
//
//			                <degree>
//		                    <degree-value>7</degree-value>
//		                    <degree-alter>0</degree-alter>
//		                    <degree-type>add</degree-type>
//		                    </degree>

                            out.println("</harmony>");
                        } */

                                // I guess im2 ScoreChord is im3 SimpleChord, im2 ScoreNote is im3 AtomPitch
                                // We start from a note, see whether it is in a chord, then get the notes in the  chord and export them??
                                if (note.getAtom() instanceof SimpleChord) {
                                    boolean firstInChord = true;
                                    SimpleChord chord = (SimpleChord) note.getAtom();
                                    List<SimpleNote> notes = chord.getNotes();
                                    for (SimpleNote simpleNote : notes) {

                                        //was im2: ScoreNote.createTempScoreNote(note.getVoice(), song.getResolution(), chord.getTime(), pitch, chord.getRhythm());
                                        exportNoteOrRest(simpleNote, !firstInChord, chord.getParentAtom());
                                        firstInChord = false;
                                    }
                                } else {
                                    exportNoteOrRest(note.getAtom(), false, note.getAtom().getParentAtom());
                                }
                            }
                            if (backup > 0) {
                                start(sb, 3, "backup");
                                startEndTextContentSingleLine(sb, 4, "duration", String.valueOf(backup));
                                end(sb, 3, "backup");
                                backup = 0;
                            }
                        }

                    }
                    closeBar();
                }
                end(sb, 1, "part");
            }
        }
    }

    private void exportNoteOrRest(Atom atom, boolean inChord, CompoundAtom tuplet) {
        boolean needsBackup = false;
        start(sb, 3, "note");
        if (inChord) {
            startEnd(sb, 4, "chord");
        }
        if (atom instanceof SimpleRest) {
            startEnd(sb, 4, "rest");
        } else if (atom instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) atom;
            start(sb, 5, "pitch");
            PitchClass pc = note.getPitch().getPitchClass();
            startEndTextContentSingleLine(sb, 6, "step", pc.getNoteName().toString());
            if (pc.getAccidental() != Accidentals.NATURAL)
                startEndTextContentSingleLine(sb, 6, "alter", String.valueOf(pc.getAccidental().getAlteration()));
            startEndTextContentSingleLine(sb, 6, "octave", String.valueOf(note.getPitch().getOctave()));
            end(sb, 5, "pitch");
        } /* TODO else { what else? } */

        startEndTextContentSingleLine(sb, 5, "duration", String.valueOf(atom.getDuration().multiplyBy(divisions).intValue()));
        /* In im2 it was computed like this:
        double duration=64.0*atom.getFigure().getRatio();
        double durwithdots = duration;
        for (int id=0; id<atom.getDots(); id++) {
            durwithdots += duration / Math.pow(2, id+1);
        }
        out.print((int)durwithdots);*/

        // Ties
        if (atom instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) atom;
            if (note.getAtomPitch().getTiedFromPrevious() != null) {
                startEnd(sb, 5, "tie", "type", "stop");
            }

            if (note.getAtomPitch().getTiedToNext() != null) {
                startEnd(sb, 5, "tie", "type", "start");
            }

        }

        startEndTextContentSingleLine(sb, 5, "voice", String.valueOf(atom.getLayer().getNumber()));
        if (atom.getLayer() != lastVoice) {
            backup += atom.getDuration().multiplyBy(divisions).intValue();
        }

        // Type, tuplets, stems
        if (atom instanceof SingleFigureAtom) {
            SingleFigureAtom sfa = (SingleFigureAtom) atom;
            startEndTextContentSingleLine(sb, 5, "type", FIGURES.get(sfa.getAtomFigure().getFigure()));
            for (int idts = 0; idts < sfa.getAtomFigure().getDots(); idts++) {
                startEnd(sb, 5, "dot");
            }

            if (tuplet instanceof SimpleTuplet) {
                SimpleTuplet stuplet = (SimpleTuplet) tuplet;
                start(sb, 5, "time-modification");
                startEndTextContentSingleLine(sb, 6, "actual-notes", String.valueOf(stuplet.getCardinality()));
                startEndTextContentSingleLine(sb, 6, "normal-notes", String.valueOf(stuplet.getInSpaceOfAtoms()));
                end(sb, 5, "time-modification");


            }
            // Stems
            StemDirection stemdir = sfa.getExplicitStemDirection();
            if (stemdir != null)
                startEndTextContentSingleLine(sb, 5, "stem", stemdir.name());
        }


        if (atom.getStaff() != null) {
            startEndTextContentSingleLine(sb, 5, "staff", String.valueOf(atom.getStaff().getNumberIdentifier()));
        }

        // Beams and lyrics
        // TODO: Beams of tuplets
        if (atom instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) atom;
            AtomPitch pitch = note.getAtomPitch();

            // Beams must go after <staff>, I believe
            BeamGroup bg = note.getBelongsToBeam();
            if (bg != null) {

                String beamValue;
                if (bg.getFirstFigure() == note)
                    beamValue = "begin";
                else if (bg.getLastFigure() == note)
                    beamValue = "end";
                else beamValue = "continue";

                // beam number is based on note duration
                int num_beams = computeBeamIndex(note);
                for (int beam = 1; beam <= num_beams; beam++)
                    startEndTextContentSingleLine(sb, 5, "beam", beamValue, "number", String.valueOf(beam));
            }

            start(sb, 5, "notations");
            // Tie notations
            if (note.getAtomPitch().getTiedFromPrevious() != null) {
                startEnd(sb, 6, "tied", "type", "stop");
            }

            if (note.getAtomPitch().getTiedToNext() != null) {
                startEnd(sb, 6, "tied", "type", "start");
            }


            // Tuplet notations
            if (tuplet != null && tuplet instanceof SimpleTuplet) {
                if (atom == tuplet.getFirstAtom() || atom == tuplet.getLastAtom()) {
                    // tuplet <notations>
                    // placement
                    String placement = note.getExplicitStemDirection().name();

                    if (placement.equals("up") || placement.equals("computed"))
                        placement = "above";
                    else placement = "below";
                    String tuplet_type = (atom == tuplet.getFirstAtom() ? "start" : "stop");

                    // TODO: check if the tuplet 'number' is really __getID(). Tuplets need an integer identifier
                    startEnd(sb, 6, "tuplet", "number", tuplet.__getID(), "placement", placement, "type", tuplet_type);
                }
            }
            end(sb, 5, "notations");

            if (pitch.getLyrics() != null) {
                for (Map.Entry<Integer, ScoreLyric> entry : pitch.getLyrics().entrySet()) {
                    start(sb, 5, "lyric", "number", String.valueOf(entry.getKey()));
                    startEndTextContentSingleLine(sb, 6, "syllabic", "single");
                    startEndTextContentSingleLine(sb, 6, "text", entry.getValue().getText());
                    end(sb, 5, "lyric");
                }
            }
        }


        end(sb, 3, "note");

        /*
        if (needsBackup) {
            start(sb,3,"backup");
            startEndTextContentSingleLine(sb,4,"duration",String.valueOf(atom.getDuration().multiplyBy(divisions).intValue()));
            end(sb,3,"backup");
        }*/
    }

    /**
     * @param useFClef
     * @param measure
     * @param part
     * @param tempo
     * @param systemBreaks
     * @throws IM3Exception
     */
    private void openBar(boolean useFClef, Measure measure, ScorePart part, Tempo tempo, HashMap<Time, SystemBreak> systemBreaks) throws IM3Exception {
        // reset backup
        backup = 0;
        if (measure.getTime().equals(Time.TIME_ZERO) && measure.getSong().isAnacrusis())
            start(sb, 2, "measure", "implicit","yes","number", measure.getNumber().toString());
        else
            start(sb, 2, "measure", "number", measure.getNumber().toString());
        if (multiMeasureRestRemaining==0) { // not in a multimeasure rest

            // manual system breaks
            if (systemBreaks != null && !systemBreaks.isEmpty()) {
                if (measure.getTime().equals(Time.TIME_ZERO)) // 1st measure of part
                {
                    start(sb, 3, "print");
                    startEndTextContentSingleLine(sb, 4, "measure-numbering", "system");
                    end(sb, 3, "print");

                } else {
                    SystemBreak systemBreak = systemBreaks.get(measure.getTime());
                    if (systemBreak != null && systemBreak.isManual()) {
                        start(sb, 3, "print", "new-system", "yes");
                        start(sb, 4, "system-layout");
                        // TODO: System distance is fixed, should be a property of SystemBreak or Staff or whatever in the future
                        startEndTextContentSingleLine(sb, 5, "system-distance", "114");
                        end(sb, 4, "system-layout");
                        end(sb, 3, "print");
                    }
                }
            }
            start(sb, 3, "attributes");
            //int divisions = (int) ((double)ts.getNumerator() / (double)song.findMinimumFigure(bar).getRatio()); //TODO Ver qu� pasa con compases compuestos
            startEndTextContentSingleLine(sb, 4, "divisions", String.valueOf(divisions.intValue()));

            // ks was KeySignature in im2, I think it is Key in im3 (not KeySignature)
            Key ks = scoreSong.getUniqueKeyWithOnset(measure.getTime());
            if (ks != null) {
                start(sb, 4, "key");
                startEndTextContentSingleLine(sb, 5, "fifths", String.valueOf(ks.getFifths()));
                startEndTextContentSingleLine(sb, 5, "mode", ks.getMode() == Mode.MAJOR ? "major" : "minor");
                end(sb, 4, "key");


            }

            TimeSignature ts = scoreSong.getUniqueMeterWithOnset(measure.getTime());
            // TODO: Only modern notation time signatures, for now
            if (ts instanceof FractionalTimeSignature) {
                FractionalTimeSignature fts = (FractionalTimeSignature) ts;
                start(sb, 4, "time");
                startEndTextContentSingleLine(sb, 5, "beats", String.valueOf(fts.getNumerator()));
                startEndTextContentSingleLine(sb, 5, "beat-type", String.valueOf(fts.getDenominator()));
                end(sb, 4, "time");

                // staves
                List<Staff> staves = part.getStaves();
                startEndTextContentSingleLine(sb, 4, "staves", String.valueOf(staves.size()));
                for (Staff s : part.getStaves()) {
                    start(sb, 4, "clef", "number", String.valueOf(s.getNumberIdentifier()));
                    startEndTextContentSingleLine(sb, 5, "sign", s.getRunningClefAt(measure.getTime()).getNote().toString());
                    startEndTextContentSingleLine(sb, 5, "line", String.valueOf(s.getRunningClefAt(measure.getTime()).getLine()));
                    end(sb, 4, "clef");

                    // We need this, I don't know exactly why
                    startEnd(sb,4,"staff-details","number",String.valueOf(s.getNumberIdentifier()), "print-object","yes");
                }


			/*out.println("<clef>");
			if (useFClef) {
				out.println("<sign>F</sign>");
				out.println("<line>4</line>");
			} else {
				out.println("<sign>G</sign>");
				out.println("<line>2</line>");
			}
			out.println("</clef>");		*/ //TODO Revisar
            }

            // multimeasure rests
            List<AtomFigure> atomFigures = part.getAtomFiguresWithOnsetWithin(measure.getTime(), measure.getEndTime());
            if (!atomFigures.isEmpty()) {
                Atom atom = atomFigures.get(0).getAtom();

                if (atom instanceof SimpleMultiMeasureRest) {
                /*<measure-style>
     <multiple-rest>6</multiple-rest>
    </measure-style>*/
                    SimpleMultiMeasureRest smmr = (SimpleMultiMeasureRest) atom;
                    multiMeasureRestRemaining = smmr.getNumMeasures();
                    start(sb, 4, "measure-style");
                    startEndTextContentSingleLine(sb, 5, "multiple-rest", String.valueOf(multiMeasureRestRemaining));
                    end(sb, 4, "measure-style");
                }
            }
            end(sb, 3, "attributes");

            // Tempo
            if (tempo != null) {
                startEnd(sb, 3, "sound", "tempo", String.valueOf(tempo.getTempo()));
            }

        }
    }

    private void closeBar() {
        if (lastMeasure) {
            // <barline location="right">
            // <bar-style>light-heavy</bar-style>
            // </barline>

            // Currently, every last measure has a double final bar.
            // TODO: output a double final bar only if the measure has one.
            start(sb, 3, "barline", "location", "right");
            startEndTextContentSingleLine(sb, 4, "bar-style", "light-heavy");
            end(sb, 3, "barline");
        }
        if (multiMeasureRestRemaining>0)
            multiMeasureRestRemaining--;
        end(sb, 2, "measure");
    }


}

