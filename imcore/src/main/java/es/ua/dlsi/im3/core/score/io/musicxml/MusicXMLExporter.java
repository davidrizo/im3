package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.ScorePart;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.ISongExporter;
import es.ua.dlsi.im3.core.score.io.XMLExporterHelper;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    // TODO: remove? from im2: private ScoreVoice lastVoice = null;

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
        XMLExporterHelper.start(sb, 0, "score-partwise", "version", MUSICXML_VERSION);
         XMLExporterHelper.start(sb, 1, "work");
          XMLExporterHelper.startEndTextContentSingleLine(sb, 2, "work-title", scoreSong.getTitle());
         XMLExporterHelper.end(sb, 1, "work");
         XMLExporterHelper.start(sb, 1, "identification");
          XMLExporterHelper.startEndTextContentSingleLine(sb, 2, "creator", scoreSong.getComposer(), "type", "composer");
          XMLExporterHelper.start(sb, 2, "encoding");
           XMLExporterHelper.startEndTextContentSingleLine(sb, 3, "software", "IM3");

           // TEST Con este formato: 2016-11-15
           DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
           XMLExporterHelper.startEndTextContentSingleLine(sb, 3, "encoding-date", df.format(new Date()));
           // Should we add these 'supports' tags? (see simple1.xml)
           XMLExporterHelper.startEnd(sb, 3, "supports", "element", "accidental", "type", "yes");
           // TODO add more supports tags (or do not)
          XMLExporterHelper.end(sb, 2, "encoding");
         XMLExporterHelper.end(sb, 1, "identification");

         XMLExporterHelper.start(sb, 1, "defaults");
          XMLExporterHelper.start(sb, 2, "scaling");
          XMLExporterHelper.end(sb, 2, "scaling");
          XMLExporterHelper.start(sb, 2, "page-layout");
          XMLExporterHelper.end(sb, 2, "page-layout");
          XMLExporterHelper.startEnd(sb, 2, "word-font", "font-family", "FreeSerif", "font-size", "10");
          XMLExporterHelper.startEnd(sb, 2, "lyric-font", "font-family", "FreeSerif", "font-size", "11");

          // separation between staves
          // <staff-layout><staff-distance>200</staff-distance></staff-layout>
          XMLExporterHelper.start(sb, 2, "staff-layout");
           XMLExporterHelper.startEndTextContentSingleLine(sb, 3, "staff-distance", "200");
          XMLExporterHelper.end(sb, 2, "staff-layout");

         XMLExporterHelper.end(sb, 1, "defaults");

         // credits
         // <credit page="1">
         //    <credit-words default-x="595.44" default-y="1626.67" justify="center" valign="top" font-size="24">Single test 1</credit-words>
         // </credit>
         // <credit page="1">
         //   <credit-words default-x="1134.19" default-y="1526.67" justify="right" valign="bottom" font-size="12">David Rizo</credit-words>
         // </credit>

         XMLExporterHelper.start(sb, 1, "credit", "page", "1");
          XMLExporterHelper.startEndTextContentSingleLine(sb, 3, "credit-words", scoreSong.getTitle(),"justify","center","valign","top","font-size","24");
         XMLExporterHelper.end(sb, 1, "credit");
         XMLExporterHelper.start(sb, 1, "credit", "page", "1");
        XMLExporterHelper.startEndTextContentSingleLine(sb, 3, "credit-words", scoreSong.getComposer(),"justify","right","valign","bottom","font-size","12");
         XMLExporterHelper.end(sb, 1, "credit");

        exportPartList();

        XMLExporterHelper.end(sb, 0, "score-partwise");

        return sb.toString();
    }

    /**
     * Exports the list of parts in the score
     * @throws IM3Exception
     */
    private void exportPartList() throws IM3Exception {
        XMLExporterHelper.start(sb,1, "<part-list>");
        ArrayList<ScorePart> parts = scoreSong.getParts();
        for (int i=0; i<parts.size(); i++) {
            //ScorePart part = movement.getScorePart(i);
            ScorePart voice = parts.get(i);
            // TODO: not sure: was voice.getAtomFigures() in im2
            if (voice.getAtomFigures().size() > 0) {
                XMLExporterHelper.start(sb, 2,"score-part", "id",computePartID(voice);
                 XMLExporterHelper.startEndTextContentSingleLine(sb,3,"part-name",(printVoiceNames ? (voice.getName()!=null ? voice.getName() :  "Voice " + (i+1)) : ""));
				 XMLExporterHelper.start(sb, 3, "score-instrument", "id",computePartID(voice)+"-"+voice.__getID());
				 // TODO: get the real instrument for the part
				  XMLExporterHelper.startEndTextContentSingleLine(sb,4,"instrument-name","Piano");
                 XMLExporterHelper.end(sb,3,"score-instrument");
                // TODO: MIDI stuff here, if available: channel, program, volume, pan (see simple1.xml)
                XMLExporterHelper.end(sb, 2, "score-part");
            }
        }
        XMLExporterHelper.end(sb,1,"part-list");
    }

    private void exportParts() throws Exception {
        TreeSet<Bar> bars = scoreSong.getMeasures();
        //ScorePart part = movement.getScorePart(ip);
        boolean harmoniesExported = false;
        for (ScorePart part: song.getPartsSortedByNumberAsc()) {
			/*if (!part.isMonophonic()) {
				throw new Exception("The voice is not monophonic");
			}*/
            if (!harmoniesExported) {
                // in order to export the harmonies, we create a PlayedNote decorator that contains the harmony. This object is
                // set only for the first note just after (or at) the onset of a given harmony
                for (Harmony harmony : song.getHarmonies()) {
                    ScoreSoundingElement snote = part.getFirstScoreSoundingElementAtTime(harmony.getTime());
                    snote.addDecoration(new _HarmomyNoteDecoration(harmony));
                    //System.out.println("Dec " + harmony.toString());
                }
                harmoniesExported = true;
            }

            if (!part.getScoreSoundingElements().isEmpty()) {
                out.print("<part id=\"");
                out.print(computePartID(part));
                out.println("\">");
                boolean F4Clef = part.needsF4Clef();
                for (Bar bar : bars) {
                    openBar(F4Clef, bar, part);
                    TreeSet<ScoreSoundingElement> snotes = part.getScoreSoundingElementsWithOnsetWithin(bar);
                    //System.err.println("Bar " + bar.toString() + " onset " + snotes.toString());
                    for (ScoreSoundingElement note: snotes) {
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

							/*out.println("\t<degree>");
							out.print("\t\t<degree-value>");
							out.print(hnd.harmony.getDegree());
							out.println("</degree-value>");
							out.println("\t</degree>");*/

			                /*<degree>
		                    <degree-value>7</degree-value>
		                    <degree-alter>0</degree-alter>
		                    <degree-type>add</degree-type>
		                    </degree>*/

                            out.println("</harmony>");
                        }
                        if (note instanceof ScoreChord) {
                            boolean firstInChord=true;
                            ScoreChord chord = (ScoreChord) note;
                            //System.out.println("Exporting " + chord.toString());
                            for (ScientificPitch pitch: chord.getPitches()) {
                                ScoreNote temp = ScoreNote.createTempScoreNote(note.getVoice(), song.getResolution(), chord.getTime(), pitch, chord.getRhythm());
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

}

}
