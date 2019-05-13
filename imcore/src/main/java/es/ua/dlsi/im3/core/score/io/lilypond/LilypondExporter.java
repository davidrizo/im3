package es.ua.dlsi.im3.core.score.io.lilypond;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.io.ISongExporter;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.mensural.meters.TempusImperfectumCumProlationeImperfecta;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

//TODO Distintas voces dentro del staff
public class LilypondExporter implements ISongExporter {
    private static final int CENTRAL_OCTAVE = 3;
    Logger logger = Logger.getLogger(LilypondExporter.class.getName());
    private static final String [] NOTES = {
            "c", "d", "e", "f", "g", "a", "b"};
    /**
     * Accidental names
     */
    private static final String [] ACCIDENTAL_NAMES = {"eses", "es", "", "is", "isis"};

    private static final String [] MAP = {
            "c", "c", "d", "d", "e", "f", "f", "g", "g", "a", "a", "b"};

    // TODO Esto no tiene en cuenta la tonalidad - los bemoles se introducen con es
    private static final String [] ACCIDENTALS = {"", "is", "", "is", "", "", "is", "", "is", "", "is", ""};
    /**
     * Sharp constant
     */
    public static final String SHARP = "is";
    /**
     * Flat constant
     */
    public static final String FLAT = "es";

    private static final String [] KEY_SIGNATURE_NOTES = {"c", "cis", "d", "ees", "e", "f", "fis", "g", "aes", "a", "bes", "b"};
    /**
     * Any paper format preamble
     */
    private String paperPreamble = null;
    private ArrayList<String> lastLyrics;
    private ScoreLyric lastLyric;
    private boolean diplomaticEdition;

    public LilypondExporter() {
        this(false);
    }
    public LilypondExporter(boolean diplomaticEdition) {
        this.diplomaticEdition = diplomaticEdition;
    }

    public boolean isDiplomaticEdition() {
        return diplomaticEdition;
    }

    public void setDiplomaticEdition(boolean diplomaticEdition) {
        this.diplomaticEdition = diplomaticEdition;
    }

    private void printScoreStart(ScoreSong song, PrintStream out) {
        if (paperPreamble != null) {
            out.println(paperPreamble);
        }
        out.println("\\score {");
    }

    /**
     * Encode/replace in latex the str source string
     * Change all non-word characters [a-zA-Z_0-9] and nonspace and not / for a -
     * @param str
     * @return
     */
    public static final String encodeString(final String str) {
        return str.replaceAll("[^a-zA-Z0-9\\/\\s]", "-");
    }

    private void printScoreEnd(ScoreSong song, PrintStream out) {
        StringBuilder sb = new StringBuilder();
        out.println("}");
        if (song.getTitle() != null) {
            //out.println("\\header { title = \"" + LatexUtils.encodeString(song.getTitle()) + "\"}");
            sb.append("title = \"");
            sb.append(encodeString(song.getTitle()));
            sb.append("\"");
        }
        //TODo Subtitle, author
        /*if (song.getSubtitle() != null) {
            sb.append(" subtitle = \"");
            sb.append(LatexUtils.encodeString(song.getSubtitle()));
            sb.append("\"");
        }
        if (song.getAuthor() != null) {
            sb.append(" composer = \"");
            sb.append(LatexUtils.encodeString(song.getAuthor()));
            sb.append("\"");
        }*/

        if (sb.length() > 0) {
            sb.insert(0, " \\header { ");
            sb.append("}");
        }

        if (diplomaticEdition) {
            sb.append("\n\\layout {\n" +
                    "    \\context {\n" +
                    "      \\Score\n" +
                    "      \\override NonMusicalPaperColumn.line-break-permission = ##f\n" +
                    "      \\override NonMusicalPaperColumn.page-break-permission = ##f\n" +
                    "    }\n" +
                    "  }\n");
        }

        out.print(sb);
    }
    /**
     * To visualize the startGroup and stopGroup
     * @param out
     */
    private void printScoreAnalisysBracketsEngraver(PrintStream out) {
		/*out.println("\\paper {");
		out.println("    \\context {");
		out.println("       \\StaffContext \\consists \"Horizontal_bracket_engraver\"");
		out.println("}}");*/
        //out.println("\\layout { \\context { \\Staff \\consists \"Horizontal_bracket_engraver\"}} ");
    }

    @Override
    public void exportSong(File file, ScoreSong song) throws ExportException {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(file));
            out.println("#(set-default-paper-size \"a4\" 'landscape')");
            out.println("\\version \"2.10.15\"");
            printScoreStart(song, out);
            out.println("<<" ); // if it is opened with {, when we have several staves it does not work well
            out.println(exportStaves(song.getStaves()));
            out.println(">>");
            printScoreAnalisysBracketsEngraver(out);
            printScoreEnd(song,out);

            out.close();

        } catch (FileNotFoundException e) {
            throw new ExportException(e);
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
    }

    public void exportPart(File file, ScorePart part) throws ExportException {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(file));
            out.println("#(set-default-paper-size \"a4\" 'landscape')");
            out.println("\\version \"2.10.15\"");
            printScoreStart(part.getScoreSong(), out);
            out.println("<<" ); // if it is opened with {, when we have several staves it does not work well
            out.println(exportStaves(part.getStaves()));
            out.println(">>");
            printScoreAnalisysBracketsEngraver(out);
            printScoreEnd(part.getScoreSong() ,out);

            out.close();

        } catch (FileNotFoundException e) {
            throw new ExportException(e);
        } catch (IM3Exception e) {
            throw new ExportException(e);
        }
    }

    private StringBuilder exportStaves(Collection<Staff> staves) throws ExportException {
        StringBuilder sb = new StringBuilder();

        for (Staff staff: staves) {
            if (staff.getNotationType() == NotationType.eMensural) {
                sb.append("\t\\new MensuralStaff {\n");
            } else {
                //sb.append("\t\\new Staff  \\with { \\consists \"Horizontal_bracket_engraver\" } {\n");
                sb.append("\t\\new Staff {\n");
            }
            exportStaff(staff, sb);

            sb.append("}\n"); // end staff
        }
        // lyrics
        if (lastLyrics != null && !lastLyrics.isEmpty()) {
            sb.append("\t\t\\new Lyrics \\lyricsto \"v1\"{\n"); //TODO v1 --> voz
            boolean first = true;
            for (String l: lastLyrics) {
                if (first) {
                    first = false;
                } else {
                    sb.append(' ');
                }
                sb.append(l);
            }
            sb.append("\t\t}\n");
        }
        //sb.append(">>\n");
        return sb;
    }

    /**
     * It includes the atoms in the layers
     * @return
     */
    public List<ITimedElementInStaff> getCoreSymbolsIncludingLayersOrdered(Staff staff) throws ExportException {
        List<ITimedElementInStaff> symbols = staff.getCoreSymbols();

        for (ScoreLayer scoreLayer: staff.getLayers()) {
            symbols.addAll(scoreLayer.getAtoms());
        }

        SymbolsOrderer.sortList(symbols);

        // now, in case of having several layers, distribute them by such layers
        if (staff.getLayers().size() > 1) {
            throw new ExportException("TO-DO VARIAS CAPAS EN LILYPOND");
        }

        return symbols;
    }


    private void exportStaff(Staff staff, StringBuilder sb) throws ExportException {
        if (staff.getNotationType() == NotationType.eMensural) {
            sb.append("\t\\new MensuralVoice  = \"v1\" {\n"); //TODO v1 --> voz
        } else {
            //sb.append("\t\\new Staff  \\with { \\consists \"Horizontal_bracket_engraver\" } {\n");
            sb.append("\t\\new Voice = \"v1\" {\n");
        }

        /*20181016 if (staff.getClefs().size() != 1) {
            throw new ExportException("Supported just 1 clef in the staff, there are: " + staff.getClefs().size());
        }
        if (staff.getKeySignatures().size() != 1) {
            throw new ExportException("Supported just 1 key signatures in the staff, there are: " + staff.getKeySignatures().size());
        }
        if (staff.getTimeSignatures().size() != 1) {
            throw new ExportException("Supported just 1 time signatures in the staff, there are: " + staff.getTimeSignatures().size());
        }

        if (staff.getName() != null) {
            sb.append("\t\\set Staff.instrument = \"" + staff.getName() + "\" \n");
        }

        exportClef(staff, staff.getClefAtTime(Time.TIME_ZERO), sb);
        exportKeySignature(staff, staff.getKeySignatureWithOnset(Time.TIME_ZERO), sb);
        exportTimeSignature(staff, staff.getTimeSignatureWithOnset(Time.TIME_ZERO), sb);*/

        lastLyrics = new ArrayList<>();

        sb.append("\\absolute {\n");

        List<ITimedElementInStaff> staffCoreSymbols = getCoreSymbolsIncludingLayersOrdered(staff);
        Key lastKey = null;

        for (ITimedElementInStaff timedElementInStaff: staffCoreSymbols) {
            if (timedElementInStaff instanceof Clef) {
                exportClef(staff, (Clef) timedElementInStaff, sb);
            } else if (timedElementInStaff instanceof TimeSignature) {
                exportTimeSignature(staff, (TimeSignature) timedElementInStaff, sb);
            } else if (timedElementInStaff instanceof KeySignature) {
                KeySignature keySignature = (KeySignature) timedElementInStaff;
                lastKey = keySignature.getInstrumentKey();
                exportKeySignature(staff,keySignature, sb);
            } else if (timedElementInStaff instanceof SimpleRest) {
                generateRest((SimpleRest) timedElementInStaff, sb);
            } else if (timedElementInStaff instanceof SimpleNote) {
                generateNote((SimpleNote) timedElementInStaff, lastKey, sb);
            } else if (timedElementInStaff instanceof MarkBarline) {
                generateMarkBarline((MarkBarline) timedElementInStaff, sb);
            } else if (timedElementInStaff instanceof SystemBeginning) {
                generatePartSystemBreak(sb);
            } else if (timedElementInStaff instanceof PageBeginning) {
                generatePartPageBreak(sb);
            } else if (timedElementInStaff instanceof Custos){
                System.err.println("TO-DO CUSTOS"); //TODO Custos
            } else {
                throw new ExportException("Unsupported export of type '" + timedElementInStaff.getClass() + "'");
            }
            sb.append(' ');
        }


        //generateStaff(staff, staff.getKeySignatureWithOnset(Time.TIME_ZERO).getInstrumentKey(), sb);
        sb.append("}\n"); // end of absolute
        sb.append("}\n"); // end of voice

    }

    private void generatePartPageBreak(StringBuilder sb) {
        sb.append("\\pagebreak");
    }

    private void generatePartSystemBreak(StringBuilder sb) {
        sb.append("\\break");
    }

    private void generateMarkBarline(MarkBarline markBarline, StringBuilder sb) {
        //TODO repeticiones...
        sb.append("|");
    }

    private void exportTimeSignature(Staff staff, TimeSignature timeSignature, StringBuilder sb) throws ExportException {
        if (timeSignature instanceof FractionalTimeSignature) {
            FractionalTimeSignature fts = (FractionalTimeSignature) timeSignature;
            sb.append("\t\t\\time " + fts.getNumerator() + "/"
                    + fts.getDenominator() + "\n");
        } else if (timeSignature instanceof TimeSignatureCommonTime || timeSignature instanceof TempusImperfectumCumProlationeImperfecta) {
            sb.append("\t\t\\time 4/4");
        } else if (timeSignature instanceof TimeSignatureCutTime) {
            sb.append("\t\t\\time 2/2");
        } else {
            throw new ExportException("Unsupported time signature: " + timeSignature.getClass());
        }
        sb.append('\n');
    }

    private void exportKeySignature(Staff staff, KeySignature keySignature, StringBuilder sb) {
        Key key = keySignature.getInstrumentKey();
        sb.append("\t\t\\key " + KEY_SIGNATURE_NOTES[key.getPitchClass().getSemitonesFromC()]
                + " \\" + (key.getMode()==Mode.MAJOR?"major":"minor"));
        sb.append('\n');
    }

    private void exportClef(Staff staff, Clef clef, StringBuilder sb) throws ExportException {
        sb.append("\t\t\\clef ");
        if (staff.getNotationType() == NotationType.eMensural) {
            if (clef instanceof ClefG2) {
                sb.append("\"mensural-g\"");
            } else if (clef instanceof ClefF4) {
                sb.append("\"mensural-f\"");
            } else if (clef instanceof ClefC1) {
                sb.append("\"mensural-c1\"");
            } else if (clef instanceof ClefC2) {
                sb.append("\"mensural-c2\"");
            } else if (clef instanceof ClefC3) {
                sb.append("\"mensural-c3\"");
            } else if (clef instanceof ClefC4) {
                sb.append("\"mensural-c4\"");
            } else if (clef instanceof ClefC5) {
                sb.append("\"mensural-c5\"");
            } else {
                throw new ExportException("Unsupported clef: " + clef.getClass().getName());
            }
        } else {
            if (clef instanceof ClefG2) {
                sb.append("treble");
            } else if (clef instanceof ClefF4) {
                sb.append("bass");
            } else if (clef instanceof ClefC1) {
                sb.append("soprano");
            } else {
                throw new ExportException("Unsupported clef: " + clef.getClass().getName());
            }
        }
        sb.append('\n');
    }

    /**
     * Generates the Lilypond code for an staff
     */
    /*private void generateStaff(Staff staff, Key lastKey, StringBuilder sb) throws ExportException {
        ScoreLayer voice = staff.getLayers().get(0);
        lastLyrics = new ArrayList<>();
        lastLyric = null;
        for (Atom atom: voice.getAtomsSortedByTime()) {
            if (atom instanceof SimpleRest) {
                generateRest((SimpleRest) atom, sb);
            } else if (atom instanceof SimpleNote) {
                generateNote((SimpleNote) atom, lastKey, sb);
            } else {
                throw new ExportException("Unsupported " + atom.getClass().getName());
            }
            sb.append(' ');
        }
    }*/

    private void generateNote(SimpleNote note, Key lastKey, StringBuilder sb) throws ExportException {
        generateNoteName(note.getPitch(), sb);
        generateDuration(note.getAtomFigure(), sb);
        TreeMap<Integer, ScoreLyric> scoreLyrics = note.getAtomPitch().getLyrics();

        String lyrics = null;
        ScoreLyric lyric = null;
        if (scoreLyrics != null && !scoreLyrics.isEmpty()) {
            if (scoreLyrics.size() > 2) {
                throw new ExportException("Unsupported more than one verse, there are:" + scoreLyrics.size());
            }
            lyric = scoreLyrics.firstEntry().getValue();
            lyrics = lyric.getText();
        }

        if (lyrics != null) {
            lastLyrics.add(lyrics);
        } else if (lastLyric != null && (lastLyric.getSyllabic() == Syllabic.begin || lastLyric.getSyllabic() == Syllabic.middle)) {
            lastLyrics.add("_");
        }
        
        if (lyric != null) {
            lastLyric = lyric;
        }

    }

    private void generateRest(SimpleRest rest, StringBuilder sb) throws ExportException {
        sb.append('r');
        generateDuration(rest.getAtomFigure(), sb);
    }

    private void generateDuration(AtomFigure atomFigure, StringBuilder sb) throws ExportException {
        if (atomFigure.getFigure().getNotationType() == NotationType.eMensural) {
            switch (atomFigure.getFigure()) {
                case MAXIMA:
                    sb.append("\\maxima");
                    break;
                case LONGA:
                    sb.append("\\longa");
                    break;
                case BREVE:
                    sb.append("\\breve");
                    break;
                case SEMIBREVE:
                    sb.append("1");
                    break;
                case MINIM:
                    sb.append("2");
                    break;
                case SEMIMINIM:
                    sb.append("4"); // TODO: 28/9/17 Ver duraciones
                    break;
                case FUSA:
                    sb.append("8"); // TODO: 28/9/17 Ver duraciones
                    break;
                case SEMIFUSA:
                    sb.append("16");
                    break;
                default:
                    throw new ExportException("Unsupported mensural figure: " + atomFigure.getFigure());
            }
        } else {
            sb.append(atomFigure.getFigure().getMeterUnit());
        }

        for (int i=0; i<atomFigure.getDots(); i++) {
            sb.append('.');
        }
    }



    private void generateNoteName(ScientificPitch pitch, StringBuilder sb) throws ExportException {
        sb.append(pitch.getPitchClass().getNoteName().name().toLowerCase());
        // TODO: 28/9/17 Alteraciones necesarias sólo?
        switch (pitch.getPitchClass().getAccidental()) {
            case SHARP:
                sb.append("is");
                break;
            case FLAT:
                sb.append("es");
                break;
            case DOUBLE_FLAT:
                sb.append("eses");
                break;
            case DOUBLE_SHARP:
                sb.append("isis");
                break;
            case NATURAL:
                // no-op
                break;
            default:
                throw new ExportException("Unsupported accidental: " + pitch.getPitchClass().getAccidental());
        }

        int relativeOctave = pitch.getOctave() - CENTRAL_OCTAVE;
        if (relativeOctave > 0) {
            while (relativeOctave > 0) { // upper octaves
                sb.append("'");
                relativeOctave --;
            }
        } else {
            while (relativeOctave < 0) { // lower octaves
                sb.append(",");
                relativeOctave ++;
            }
        }
    }

    /**
     * Export for a PNG format using measure
     * @param f
     * @param fromMeasure
     * @param toMeasure
     * @param song
     * @throws ExportException
     */
    /*public void exportSongForPNG(File outputFile, int fromMeasure, int toMeasure, Song song) throws ExportException {
        int [] voiceNumbers = new int[song.getNumVoices()];
        for (int i=0; i<voiceNumbers.length; i++) {
            voiceNumbers[i] = i+1;
        }

        paperPreamble = "\\paper{\n"
                +"indent=0\\mm\n"
                +"oddFooterMarkup=##f\n"
                +"oddHeaderMarkup=##f\n"
                +"bookTitleMarkup = ##f\n"
                +"scoreTitleMarkup = ##f\n"
                + "}\n";
        this.exportSong(outputFile, song, voiceNumbers, fromMeasure, toMeasure);

    }*/

    /**
     * Create a lilypond file ready to generate an EPS with the command: lilypond -b png -dno-gs-load-fonts -dinclude-eps-fonts <file>
     * @param outputFile
     * @param song
     * @throws ExportException
     */
    /*public void exportSongForPNG(File outputFile, Song song) throws ExportException {
        paperPreamble = "\\paper{\n"
                +"indent=0\\mm\n"
                +"oddFooterMarkup=##f\n"
                +"oddHeaderMarkup=##f\n"
                +"bookTitleMarkup = ##f\n"
                +"scoreTitleMarkup = ##f\n"
                + "}\n";
        this.exportSong(outputFile, song);
    }    */

    /**
     * Create a lilypond file ready to generate an EPS with the command: lilypond -b eps -dno-gs-load-fonts -dinclude-eps-fonts <file>
     * @param outputFile
     * @param song
     * @throws ExportException
     */
    /*public void exportSongForEPS(File outputFile, Song song) throws ExportException {
        paperPreamble = "\\paper{\n"
                +"indent=0\\mm\n"
                +"line-width=120\\mm\n"
                +"oddFooterMarkup=##f\n"
                +"oddHeaderMarkup=##f\n"
                +"bookTitleMarkup = ##f\n"
                +"scoreTitleMarkup = ##f\n"
                + "}\n";
        this.exportSong(outputFile, song);
    }
 */
}
