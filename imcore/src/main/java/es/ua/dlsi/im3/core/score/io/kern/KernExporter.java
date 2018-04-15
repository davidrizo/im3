package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.FigureAndDots;
import es.ua.dlsi.im3.core.conversions.RhythmUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.harmony.Harm;
import es.ua.dlsi.im3.core.score.mensural.meters.TempusImperfectumCumProlationeImperfecta;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.core.utils.SonoritySegmenter;
import es.ua.dlsi.im3.core.io.ExportException;
import org.apache.commons.lang3.math.Fraction;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by drizo on 8/6/17.
 */
public class KernExporter {
    private ScoreSong song;
    HarmExporter harmExporter;
    boolean exportingHarm;
    protected StringBuilder sb;
    List<Staff> stavesReversed;
    int nspines;

    public void exportSong(File file, ScoreSong song) throws ExportException {
        PrintStream ps = null;
        try {
            ps = new PrintStream(file, "UTF-8");
            ps.print(exportSong(song));
        } catch (Exception e) {
            throw new ExportException(e);
        }
        if (ps != null) {
            ps.close();
        }
    }

    public String exportSong(ScoreSong song) throws ExportException, IM3Exception {
        sb = new StringBuilder();
        this.song = song;
        stavesReversed = new ArrayList<>();
        for (Staff staff: song.getStaves()) {
            stavesReversed.add(0, staff);
        }
        printHeader();
        printContent();

        return sb.toString();
    }

    private void printHeader() throws ExportException {
        // print metadata
        String composer = song.getComposer();
        if (composer != null) {
            sb.append("!!!COM: ");
            sb.append(composer);
            sb.append('\n');
        }
        String title = song.getTitle();
        if (title != null) {
            sb.append("!!!OTL: ");
            sb.append(title);
            sb.append('\n');
        }


        //// spines
        nspines = 0;
        boolean first = true;
        if (song.hasHarms()) {
            harmExporter = new HarmExporter();
            sb.append("**harm");
            first = false;
            exportingHarm = true;
        } else {
            exportingHarm = false;
        }
        //TODO **root
        boolean staffTitle = false;
        for (Staff staff : stavesReversed) {
            if (staff.getName() != null) {
                staffTitle = true;
            }
            for (int i = staff.getLayers().size() - 1; i >= 0; i--) {
                nspines++;
                if (!first) {
                    sb.append('\t');
                } else {
                    first = false;
                }
                if (staff.getNotationType() == NotationType.eModern) {
                    sb.append("**kern");
                } else if (staff.getNotationType() == NotationType.eMensural) {
                    sb.append("**mens");
                } else {
                    throw new ExportException("Unsupported notation type: " + staff.getNotationType());
                }

            }
        }
        sb.append('\n');

        if (staffTitle) {
            first = true;
            for (Staff staff : stavesReversed) {
                for (int i = staff.getLayers().size() - 1; i >= 0; i--) {
                    if (!first) {
                        sb.append('\t');
                    } else {
                        first = false;
                    }
                    sb.append("! ");
                    if (staff.getName() != null) {
                        sb.append(staff.getName());
                    }
                }
            }
            sb.append('\n');
        }

        first = true;
        if (exportingHarm) {
            first = false;
            sb.append('!');
        }

        if (stavesReversed.size() > 1) {
            for (Staff staff : stavesReversed) {
                for (int i = staff.getLayers().size() - 1; i >= 0; i--) {
                    if (!first) {
                        sb.append('\t');
                    } else {
                        first = false;
                    }

                    sb.append("*staff");
                    sb.append(staff.getNumberIdentifier());
                }
            }
            sb.append('\n');
        }
    }

    private String generateTimeSignature(TimeSignature ts) throws ExportException {
        if (ts instanceof FractionalTimeSignature) {
            FractionalTimeSignature meter = (FractionalTimeSignature) ts;
            StringBuilder sb = new StringBuilder();
            sb.append('M');
            sb.append(meter.getNumerator());
            sb.append('/');
            sb.append(meter.getDenominator());
            return sb.toString();
        } else if (ts instanceof TimeSignatureCommonTime ||ts instanceof TempusImperfectumCumProlationeImperfecta) {
            return "M4/4";
        } else if (ts instanceof TimeSignatureCutTime) {
            return "M2/2";
        } else {
            // TODO: 15/4/18 Resto de compases - además añadir los símbolos
            throw new ExportException("Unsupported time signature type: " + ts.getClass());
        }
    }

    private String generateKeySignature(KeySignature ks) throws ExportException {
        StringBuilder sb = new StringBuilder();
        sb.append('k');
        sb.append('[');
        for (KeySignatureAccidentalElement acc : ks.getAccidentals()) {
            sb.append(acc.getNoteName().name().toLowerCase());
            sb.append(generateAccidental(acc.getAccidental()));
        }
        sb.append(']');
        return sb.toString();
    }

    private String generateClef(Clef clef) throws ExportException {
        if (clef instanceof ClefG1) {
            return "clefG1";
        } else if (clef instanceof ClefG2) {
            return "clefG2";
        } else if (clef instanceof ClefG2QuindicesimaBassa) {
            return "clefGv2";
        } else if (clef instanceof ClefF4) {
            return "clefF4";
        } else if (clef instanceof ClefF3) {
            return "clefF3";
        } else if (clef instanceof ClefF2) {
            return "clefF2";
        } else if (clef instanceof ClefC1) {
            return "clefC1";
        } else if (clef instanceof ClefC2) {
            return "clefC2";
        } else if (clef instanceof ClefC3) {
            return "clefC3";
        } else if (clef instanceof ClefC4) {
            return "clefC4";
        } else if (clef instanceof ClefC5) {
            return "clefC5";
        } else {
            throw new ExportException("Unimplemented export of clef " + clef.getClass());
        }
    }

    private void printContent() throws IM3Exception, ExportException {
        List<Segment> segments = new SonoritySegmenter().segmentSonorities(song);

        // Now, for each segment and each layer, fill the segments. Later, layers not always present will be trimmed
        // Add "" when nothing has to be added, it will be removed later
        String NOTHING = "!";
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        for (Segment segment: segments) {
            Time time = segment.getFrom();
            Measure measure = song.getMeasureWithOnset(time);
            String measureStr = null;
            if (measure != null) {
                String mn;
                if (measure.getNumber() == null) {
                    mn = "";
                } else {
                    mn = measure.getNumber().toString();
                }
                if (measure.getTime().isZero()) {
                    measureStr = "=" + mn + "-";
                } else {
                    measureStr = "=" + mn;
                }
            }

            ArrayList<String> [] clefKsTsRecords = new ArrayList [5];
            for (int i=0; i<clefKsTsRecords.length; i++) {
                clefKsTsRecords[i] = new ArrayList<>();
            }
            ArrayList<String> record = new ArrayList<>();

            for (Staff staff : stavesReversed) {
                for (int i = staff.getLayers().size() - 1; i >= 0; i--) {
                    ScoreLayer layer = staff.getLayers().get(i);

                    Clef clef = staff.getClefAtTime(time);
                    if (clef != null) {
                        clefKsTsRecords[0].add("*" + generateClef(clef));
                    } else {
                        clefKsTsRecords[0].add(NOTHING);
                    }

                    KeySignature keySig = staff.getKeySignatureWithOnset(time);
                    if (keySig != null) {
                        clefKsTsRecords[1].add("*" + generateKeySignature(keySig));
                    } else {
                        clefKsTsRecords[1].add(NOTHING);
                    }

                    TimeSignature timeSig = staff.getTimeSignatureWithOnset(time);
                    if (timeSig != null) {
                        clefKsTsRecords[2].add("*" + generateTimeSignature(timeSig));
                    } else {
                        clefKsTsRecords[2].add(NOTHING);
                    }

                    String meterSign = generateMeterSign(timeSig);
                    if (meterSign != null) {
                        clefKsTsRecords[3].add("*met(" + meterSign + ")");
                    } else {
                        clefKsTsRecords[3].add(NOTHING);
                    }

                    if (measureStr != null) {
                        clefKsTsRecords[4].add(measureStr);
                    } else {
                        if (staff.getMarkBarLineWithOnset(time) != null) {
                            clefKsTsRecords[4].add("=");
                        } else {
                            clefKsTsRecords[4].add(NOTHING);
                        }
                    }

                    Atom atom = layer.getAtomExpandedWithOnset(time);
                    if (atom == null) {
                        record.add("."); // continuation
                    } else {
                        if (atom instanceof SimpleMeasureRest) {
                            SimpleMeasureRest mrest = (SimpleMeasureRest) atom;
                            List<FigureAndDots> fds = RhythmUtils.findRhythmForDuration(NotationType.eModern, mrest.getDuration());
                            if (fds.size() != 1) {
                                throw new ExportException("Unsupported durations that have to be decomposed in a set of figures");
                            }
                            FigureAndDots fd = fds.get(0);
                            String duration = generateDuration(fd.getFigure(), fd.getDots(), Fraction.ONE);
                            record.add(duration + "rr");
                        } else {
                            encodeAtom(record, atom);
                        }
                    }
                }
            }

            if (exportingHarm) {
                // reuse clefKsTsRecords from the first spine
                for (int i=0; i<clefKsTsRecords.length; i++) {
                    if (!clefKsTsRecords[i].isEmpty()) {
                        if (i==0) {
                            clefKsTsRecords[i].add(0, "!"); // the clef is not copied
                        } else {
                            clefKsTsRecords[i].add(0, clefKsTsRecords[i].get(0));
                        }
                    }
                }
                Harm harm = song.getHarmWithOnsetOrNull(time);
                if (harm != null) {
                    record.add(0, harmExporter.exportHarm(harm));
                } else {
                    record.add(0, ".");
                }
            }

            for (int i=0; i<clefKsTsRecords.length; i++) {
                boolean empty = true;
                for (String s: clefKsTsRecords[i]) { // don't print anything when no spine adds any content
                    if (!s.equals(NOTHING)) { //TODO Comprobar que cuando hay cambio de clave en sólo un pentagrama al resto se le pone el carácter !
                        empty = false;
                    }
                }
                if (!empty) {
                    result.add(clefKsTsRecords[i]);
                }
            }

            result.add(record);
        }

        for (ArrayList<String> record: result) {
            boolean firstColumn = true;
            for (String col: record) {
                if (!firstColumn) {
                    sb.append('\t');
                } else {
                    firstColumn = false;
                }
                sb.append(col);
            }
            sb.append('\n');
        }
        for (int i=0; i<nspines; i++) {
            if (i>0) {
                sb.append('\t');
            }
            sb.append("*-");
        }
    }

    private String generateMeterSign(TimeSignature timeSig) {
        if (timeSig instanceof TimeSignatureCommonTime) {
            //return "C";
            return "c"; // TODO: 15/4/18 ¿Seguro? 
        } else if (timeSig instanceof TimeSignatureCutTime) {
            return "C|";
        } else if (timeSig instanceof TempusImperfectumCumProlationeImperfecta) {
            return "C";
        } else {
            // TODO: 15/4/18 Resto de prolaciones 
            return null;
        }
    }

    /**
     *
     * @param record
     * @param atom
     * @throws IM3Exception
     * @throws ExportException
     */
    private void encodeAtom(ArrayList<String> record, Atom atom) throws IM3Exception, ExportException {
        if (atom instanceof SingleFigureAtom) {
            Fraction multiplier;
            if (atom.getParentAtom() != null && atom.getParentAtom() instanceof SimpleTuplet) {
                SimpleTuplet parent = (SimpleTuplet) atom.getParentAtom();
                multiplier = Fraction.getFraction(parent.getInSpaceOfAtoms(), parent.getCardinality());
            } else {
                multiplier = Fraction.ONE;
            }
            String duration = generateDuration(((SingleFigureAtom) atom).getAtomFigure(), multiplier);

            if (atom instanceof SimpleNote) {
                SimpleNote sn = (SimpleNote) atom;
                String noteStr = generateNote(sn.getAtomPitch(), duration);
                if (sn.getAtomFigure().getFermata() != null) {
                    noteStr += ";";
                }
                record.add(noteStr);
            } else if (atom instanceof SimpleRest) {
                if (((SimpleRest) atom).getAtomFigure().getFermata() != null) {
                    record.add(duration + "r;");
                } else {
                    record.add(duration + "r");
                }

            } else if (atom instanceof SimpleChord) {
                StringBuilder cb = new StringBuilder();
                SimpleChord sc = (SimpleChord) atom;
                for (AtomPitch atomPitch : sc.getAtomPitches()) {
                    if (cb.length() > 0) {
                        cb.append(' ');
                    }
                    cb.append(generateNote(atomPitch, duration));
                }
                if (sc.getAtomFigure().getFermata() != null) {
                    cb.append(';');
                }
                record.add(cb.toString());
            } else {
                throw new ExportException("Unsupported atom type: " + atom.getClass().getName());
            }
        } /*else if (atom instanceof SimpleTuplet) {
            // TODO tuplets of tuplets and tuplets of chords
            SimpleTuplet tuplet = (SimpleTuplet) atom;
            for (Atom tupletAtom: tuplet.getAtoms()) {
                encodeAtom(record, tupletAtom, Fraction.getFraction(tuplet.getInSpaceOfAtoms(), tuplet.getCardinality()));
            }
        } */ else  {
            throw new ExportException("Unsupported exporting of class " + atom.getClass());
        }

    }

    private String generateNote(AtomPitch atomPitch, String duration) throws ExportException {
        //TODO Ver ligadas en acordes
        String prefix;

        if (atomPitch.isTiedToNext() && !atomPitch.isTiedFromPrevious()) {
            prefix = "[";
        } else {
            prefix = "";
        }
        String suffix;
        if (atomPitch.isTiedFromPrevious()) {
            if (atomPitch.isTiedToNext()) {
                suffix = "_"; // continuation tie //TODO Test unitario tie middle
            } else {
                suffix = "]";
            }
        } else {
            suffix = "";
        }
        return prefix + duration + generatePitch(atomPitch) + suffix;
    }

    // See http://www.humdrum.org/humextra/rhythm/#extensions-to-recip-and-kern-rhythms
    private String generateDuration(Figures figure, int dots, Fraction multipler) throws ExportException {
        StringBuilder sb = new StringBuilder();

        //TODO Test unitario - sobre todo de mensural
        if (figure.getNotationType() == NotationType.eModern) {
            Fraction divider = Fraction.getFraction(1, figure.getMeterUnit()).multiplyBy(multipler);
            Fraction durationValue = Fraction.ONE.divideBy(divider).reduce();
            if (durationValue.getDenominator() != 1) {
                throw new ExportException("Cannot export figure " + figure + " with multiplier " + multipler + " that produce a fraction: " + durationValue);
            }
            sb.append(durationValue.getNumerator());
        } else if (figure.getNotationType() == NotationType.eMensural) {
            char car;
            switch (figure) {
                case MAXIMA:
                    car = 'X';
                    break;
                case LONGA:
                    car = 'L';
                    break;
                case BREVE:
                    car = 'S';
                    break;
                case SEMIBREVE:
                    car = 's';
                    break;
                case MINIM:
                    car = 'M';
                    break;
                case SEMIMINIM:
                    car = 'm';
                    break;
                case FUSA:
                    car = 'U';
                    break;
                case SEMIFUSA:
                    car = 'u';
                    break;
                default:
                    throw new ExportException("Unsupported mensural figure " + figure);
            }
            sb.append(car);
        } else {
            throw new ExportException("Unsupported notation type " + figure.getNotationType());
        }
        for (int i=0; i<dots; i++) {
            sb.append('.');
        }
        return sb.toString();
    }

    /**
     *
     * @param figure
     * @param multiplier 1 when normal notes, e.j. 2/3 for triplets
     * @return
     * @throws ExportException
     */
    private String generateDuration(AtomFigure figure, Fraction multiplier) throws ExportException {
        return generateDuration(figure.getFigure(), figure.getDots(), multiplier);
    }

    private String generatePitch(AtomPitch pitch) throws ExportException {
        StringBuilder sb = new StringBuilder();

        String middleOctaveNote = pitch.getScientificPitch().getPitchClass().getNoteName().name().toLowerCase();

        if (pitch.getScientificPitch().getOctave() < 4) {
            int characters = 4 - pitch.getScientificPitch().getOctave();
            middleOctaveNote = middleOctaveNote.toUpperCase();
            for (int i=0; i<characters; i++) {
                sb.append(middleOctaveNote);
            }
        } else if (pitch.getScientificPitch().getOctave() == 4) {
            sb.append(middleOctaveNote);
        } else {
            int characters = 1 + pitch.getScientificPitch().getOctave() - 4;
            for (int i=0; i<characters; i++) {
                sb.append(middleOctaveNote);
            }
        }


        if (pitch.getScientificPitch().getPitchClass().isAltered()) {
            sb.append(generateAccidental(pitch.getScientificPitch().getPitchClass().getAccidental()));
        }
        return sb.toString();
    }

    private char generateAccidental(Accidentals accidental) throws ExportException {
        if (accidental.equals(Accidentals.SHARP)) {
            return '#';
        } else if (accidental.equals(Accidentals.FLAT)) {
            return '-';
        } else {
            throw new ExportException("Unsupported accidental in key signature: " + accidental);
        }

    }
}