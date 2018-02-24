package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.ISongExporter;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
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
    private static final int divisions = 64;

    // some flags
    private boolean printVoiceNames;
    private boolean printRomanNumberAnalysis;

    private ScoreLayer lastVoice = null;

    /* TODO: calcado e invertido de MusicXMLSAXScoreSongImporter. ¿refactorizar esto en una clase utilidad? */
    static final HashMap<Figures, String> FIGURES = new HashMap<>();
    private static final String MIDDLE_TIE = "__middle__";
    // TODO MusicXML figures, ¿normalizar en MusicXMLImporter?
    static {
        FIGURES.put(Figures.BREVE,"breve");
        FIGURES.put(Figures.WHOLE,"whole");
        FIGURES.put(Figures.HALF,"half");
        FIGURES.put(Figures.QUARTER,"quarter");
        FIGURES.put(Figures.EIGHTH,"eighth");
        FIGURES.put(Figures.SIXTEENTH,"16th");
        FIGURES.put(Figures.THIRTY_SECOND,"32th");
        FIGURES.put(Figures.SIXTY_FOURTH,"64th");
        FIGURES.put(Figures.HUNDRED_TWENTY_EIGHTH,"128th");
        FIGURES.put(Figures.TWO_HUNDRED_FIFTY_SIX,"256th");
    }

    /**
     * @return the printRomanNumberAnalysis
     */
    public final boolean isPrintRomanNumberAnalysis() {
        return printRomanNumberAnalysis;
    }

    /**
     * Not printed in Finale !!!
     * @param printRomanNumberAnalysis the printRomanNumberAnalysis to set
     */
    public final void setPrintRomanNumberAnalysis(boolean printRomanNumberAnalysis) {
        this.printRomanNumberAnalysis = printRomanNumberAnalysis;
    }

    /**
     * Default constructor
     */
    public MusicXMLExporter() {
        this.scoreSong = null;
        this.sb = null;
        this.printVoiceNames = true;
        this.printRomanNumberAnalysis = false;
    }

    /**
     * Constructor
     * @param printVoiceNames whether to print voice names
     */
    public MusicXMLExporter(boolean printVoiceNames) {
        this.scoreSong = null;
        this.sb = null;
        this.printVoiceNames = printVoiceNames;
        this.printRomanNumberAnalysis = false;
    }

    /**
     * Returns a name for a part given a score part
     * @param part the score part
     * @return a score part name
     */
    String computePartID(ScorePart part) {
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
        this.scoreSong = scoreSong;
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
     * @return
     * @throws IM3Exception
     * @throws ExportException
     */
    public String exportSong() throws IM3Exception, ExportException {
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

         // credits
         // <credit page="1">
         //    <credit-words default-x="595.44" default-y="1626.67" justify="center" valign="top" font-size="24">Single test 1</credit-words>
         // </credit>
         // <credit page="1">
         //   <credit-words default-x="1134.19" default-y="1526.67" justify="right" valign="bottom" font-size="12">David Rizo</credit-words>
         // </credit>

         start(sb, 1, "credit", "page", "1");
          startEndTextContentSingleLine(sb, 3, "credit-words", scoreSong.getTitle(),"justify","center","valign","top","font-size","24");
         end(sb, 1, "credit");
         start(sb, 1, "credit", "page", "1");
        startEndTextContentSingleLine(sb, 3, "credit-words", scoreSong.getComposer(),"justify","right","valign","bottom","font-size","12");
         end(sb, 1, "credit");

        exportPartList();

        end(sb, 0, "score-partwise");

        return sb.toString();
    }

    /**
     * Exports the list of parts in the score
     * @throws IM3Exception
     */
    private void exportPartList() throws IM3Exception {
        start(sb,1, "<part-list>");
        ArrayList<ScorePart> parts = scoreSong.getParts();
        for (int i=0; i<parts.size(); i++) {
            //ScorePart part = movement.getScorePart(i);
            ScorePart voice = parts.get(i);
            // TODO: not sure: was voice.getAtomFigures() in im2
            if (voice.getAtomFigures().size() > 0) {
                start(sb, 2,"score-part", "id",computePartID(voice);
                 startEndTextContentSingleLine(sb,3,"part-name",(printVoiceNames ? (voice.getName()!=null ? voice.getName() :  "Voice " + (i+1)) : ""));
				 start(sb, 3, "score-instrument", "id",computePartID(voice)+"-"+voice.__getID());
				 // TODO: get the real instrument for the part
				  startEndTextContentSingleLine(sb,4,"instrument-name","Piano");
                 end(sb,3,"score-instrument");
                // No MIDI stuff : channel, program, volume, pan (see simple1.xml)
                end(sb, 2, "score-part");
            }
        }
        end(sb,1,"part-list");
    }

    private void exportParts() throws Exception {
        Collection<Measure> measures = scoreSong.getMeasures();
        //ScorePart part = movement.getScorePart(ip); // PIERRE: what is this??
        boolean harmoniesExported = false;
        for (ScorePart part: scoreSong.getPartsSortedByNumberAsc()) {
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

            // part.getAtomFigures()? part.getAtomPitches()? part.getAtomsSortedByTime()?
            if (!part. getAtomsSortedByTime().isEmpty()) {
                start(sb, 1,"part","id",computePartID(part));
                // TODO im2 : boolean F4Clef = part.needsF4Clef();
                for (Measure measure : measures) {
                    openBar(false, measure, part);
                    List<AtomFigure> snotes = part.getAtomFiguresWithOnsetWithin(measure.getTime(),measure.getEndTime());
                    for (AtomFigure note: snotes) {
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
                            boolean firstInChord=true;
                            SimpleChord chord = (SimpleChord) note.getAtom();
                            for (ScientificPitch pitch: chord.getPitches()) {
                                AtomPitch temp = ScoreNote.createTempScoreNote(note.getVoice(), song.getResolution(), chord.getTime(), pitch, chord.getRhythm());
                                //for (ScoreNote cnote : ((ScoreChord)note).getScoreNotes()) {
                                //System.out.println("\tExporting " + temp);
                                exportNoteOrRest(temp, !firstInChord, chord.getBelongsToTuplet());
                                firstInChord=false;
                            }
                        } else {
                            exportNoteOrRest(note, false, note.getBelongsToTuplet());
                        }
                    }
                    closeBar();
                }
                out.println("</part>");
            }
        }
    }

    private void exportNoteOrRest(Atom atom, boolean inChord, SimpleTuplet tuplet) throws IM3Exception {
        boolean needsBackup = false;
        start(sb,3,"note");
        if (inChord) {
            startEnd(sb,4,"chord");
        }
        if (atom instanceof SimpleRest) {
            startEnd(sb,4,"rest");
        } else if (atom instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) atom;
            start(sb,5,"pitch");
            PitchClass pc = note.getPitch().getPitchClass();
            startEndTextContentSingleLine(sb,6,"step",pc.getNoteName().toString());
            startEndTextContentSingleLine(sb,6,"alter",String.valueOf(pc.getAccidental().getAlteration()));
            startEndTextContentSingleLine(sb,6,"octave",String.valueOf(note.getPitch().getOctave()));
            end(sb,5,"pitch");
        } /* TODO else { what else? } */

        startEndTextContentSingleLine(sb,5,"duration",String.valueOf(atom.getDuration().intValue()));
        /* In im2 it was computed like this:
        double duration=64.0*atom.getFigure().getRatio();
        double durwithdots = duration;
        for (int id=0; id<atom.getDots(); id++) {
            durwithdots += duration / Math.pow(2, id+1);
        }
        out.print((int)durwithdots);*/

        if (atom instanceof SimpleNote) {
            SimpleNote note = (SimpleNote) atom;
            if (note.getAtomPitch().getTiedFromPrevious() != null) {
                startEnd(sb,5,"tie","type","stop");
            }

            if (note.getAtomPitch().getTiedToNext() != null) {
                startEnd(sb,5,"tie","type","start");
            }
        }

        startEndTextContentSingleLine(sb,5,"voice",String.valueOf(atom.getLayer().getNumber()));
        if (atom.getLayer() != lastVoice) {
            needsBackup = true;
        }
        lastVoice = atom.getLayer();

        if (atom instanceof SingleFigureAtom) {
            SingleFigureAtom sfa = (SingleFigureAtom) atom;
            startEndTextContentSingleLine(sb,5,"type",FIGURES.get(sfa.getAtomFigure().getFigure()));
            for (int idts=0; idts<sfa.getAtomFigure().getDots(); idts++) {
                startEnd(sb,5,"dot");
            }
        }


        if (atom.getStaff() != null) {
            startEnd(sb,5,"staff",String.valueOf(atom.getStaff().getNumberIdentifier()));
        }

        if (tuplet != null) {
            start(sb,5,"time-modification");
            startEnd(sb,6,"actual-notes",String.valueOf(tuplet.getCardinality()));
            startEnd(sb,6,"normal-notes",String.valueOf(tuplet.getInSpaceOfAtoms()));
            end(sb,5,"time-modification");
        }

        if (atom instanceof SimpleNote) {
            AtomPitch note = ((SimpleNote) atom).getAtomPitch();

            if (note.getLyrics() != null) {
                for (Map.Entry<Integer, ScoreLyric> entry: note.getLyrics().entrySet()) {
                    start(sb,5,"lyric","number",String.valueOf(entry.getKey()));
                    startEndTextContentSingleLine(sb,6,"syllabic","single");
                    startEndTextContentSingleLine(sb,6,"text",entry.getValue().getText());
                    end(sb,5,"lyric");
                }
            }
        }

        end(sb,3,"note");

        if (needsBackup) {
            start(sb,3,"backup");
            startEndTextContentSingleLine(sb,4,"duration",String.valueOf(atom.getDuration().intValue()));
            end(sb,3,"backup");
        }
    }

    /**
         * @param useFClef
         * @param measure
         * @param part
         * @throws IM3Exception
         */
        private void openBar(boolean useFClef, Measure measure, ScorePart part) throws IM3Exception {
            start(sb, 2,"measure","number",measure.getNumber().toString());
             start(sb,3,"attributes");
                //int divisions = (int) ((double)ts.getNumerator() / (double)song.findMinimumFigure(bar).getRatio()); //TODO Ver qu� pasa con compases compuestos
                startEndTextContentSingleLine(sb,4,"divisions", String.valueOf(divisions));

            // ks was KeySignature in im2, I think it is Key in im3 (not KeySignature)
            Key ks = scoreSong.getUniqueKeyActiveAtTime(measure.getTime());
            if (ks != null) {
                start(sb, 4, "key");
                startEndTextContentSingleLine(sb,5,"fifths",String.valueOf(ks.getFifths()));
                startEndTextContentSingleLine(sb,5,"mode",ks.getMode() == Mode.MAJOR ? "major" : "minor");
                end(sb,4,"key");

                // staves
                List<Staff> staves = part.getStaves();
                startEndTextContentSingleLine(sb,4,"staves",String.valueOf(staves.size()));
                for (Staff s: part.getStaves()) {
                    start(sb,4,"clef","number",String.valueOf(s.getNumberIdentifier()));
                    startEndTextContentSingleLine(sb,5,"sign", s.getClefAtTime(measure.getTime()).getNote().toString());
                    startEndTextContentSingleLine(sb,5,"line",String.valueOf(s.getClefAtTime(measure.getTime()).getLine()));
                    end(sb,4,"clef");
                }

            }
            TimeSignature ts = scoreSong.getUniqueMeterWithOnset(measure.getTime());
            // TODO: Only modern notation time signatures, for now
            if (ts instanceof FractionalTimeSignature) {
                FractionalTimeSignature fts = (FractionalTimeSignature) ts;
                start(sb,4,"time");
                  startEndTextContentSingleLine(sb,5,"beats", String.valueOf(fts.getNumerator()));
                  startEndTextContentSingleLine(sb,5,"beat-type",fts.getDenominator());
                end(sb,4,"time");

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
            end(sb,3,"attributes");

        }

        private void closeBar() {
            end(sb,2,"measure");
        }


}

}
