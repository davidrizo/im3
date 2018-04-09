package es.ua.dlsi.im3.cmme;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.metadata.PersonRoles;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.io.IScoreSongImporter;
import es.ua.dlsi.im3.core.score.mensural.meters.Perfection;
import es.ua.dlsi.im3.core.score.mensural.meters.TimeSignatureMensural;
import es.ua.dlsi.im3.core.score.mensural.meters.TimeSignatureMensuralFactory;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.apache.commons.lang3.math.Fraction;
import org.cmme.DataStruct.*;
import org.cmme.DataStruct.Clef;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CMMESongImporter implements IScoreSongImporter {
    private ScoreSong scoreSong;
    private HashMap<Integer, Staff> staves;
    private HashMap<Integer, ScoreLayer> layers;
    private SingleFigureAtom lastFigureAtom;

    @Override
    public ScoreSong importSong(File file) throws ImportException {
        XMLReader.initparser("data\\",false);
        try {
            CMMEParser p=new CMMEParser(file.toURL());
            return convert(p.piece);
        } catch (Exception e) {
            throw new ImportException(e);
        }
    }

    @Override
    public ScoreSong importSong(InputStream is) throws ImportException {
        XMLReader.initparser("data\\",false);
        try {
            CMMEParser p=new CMMEParser(is, null);
            return convert(p.piece);
        } catch (Exception e) {
            throw new ImportException(e);
        }
    }

    private ScoreSong convert(PieceData piece) throws ImportException, IM3Exception {
        scoreSong = new ScoreSong();

        scoreSong.addTitle(piece.getFullTitle()); // TODO Separar title de section

        if (piece.getComposer() != null) {
            scoreSong.addPerson(PersonRoles.COMPOSER, piece.getComposer());
        }
        if (piece.getEditor() != null) {
            scoreSong.addPerson(PersonRoles.EDITOR, piece.getEditor());
        }
        staves = new HashMap<>();
        layers  = new HashMap<>();
        for (Voice voice: piece.getVoiceData()) {
            createVoice(voice);
        }

        for (MusicSection section: piece.getSections()) {
            importSection(section);
        }
        return scoreSong;
    }

    private void createVoice(Voice voice) throws IM3Exception {
        ScorePart part = scoreSong.addPart();
        part.setName(voice.getName());
        Pentagram pentagram = new Pentagram(scoreSong, Integer.toString(voice.getNum()), voice.getNum());
        pentagram.setNotationType(NotationType.eMensural);
        pentagram.setName(voice.getStaffTitle());
        part.addStaff(pentagram);
        scoreSong.addStaff(pentagram);
        ScoreLayer layer = part.addScoreLayer(pentagram);
        staves.put(voice.getNum(), pentagram);
        layers.put(voice.getNum(), layer);
    }

    private void importSection(MusicSection section) throws ImportException, IM3Exception {
        //TODO Importante Dividir secciones - ahora las estoy poniendo todas juntas
        System.err.println("TODO Importante Dividir secciones - ahora las estoy poniendo todas juntas");

        if (!(section instanceof MusicMensuralSection)) {
            throw new ImportException("Unsupported non mensural sections: " + section.getClass().getName());
        }

        MusicMensuralSection mms = (MusicMensuralSection) section;
        for (int i=0; i<mms.getNumVoices(); i++) {
            VoiceEventListData voiceEventListData = mms.getVoice(i);
            Staff staff = staves.get(voiceEventListData.getVoiceNum());
            if (staff == null) {
                throw new IM3RuntimeException("Cannot find staff for voice with number " + voiceEventListData.getVoiceNum());
            }
            ScoreLayer layer = layers.get(voiceEventListData.getVoiceNum());
            if (layer == null) {
                throw new IM3RuntimeException("Cannot find score layer for voice with number " + voiceEventListData.getVoiceNum());
            }
            importVoice(voiceEventListData, staff, layer);
        }
    }

    private void importVoice(VoiceEventListData voice, Staff staff, ScoreLayer layer) throws ImportException, IM3Exception {
        lastFigureAtom = null;
        if (voice.getEvents() != null) {
            for (Event event : voice.getEvents()) {
                switch (event.geteventtype()) {
                    case Event.EVENT_CLEF:
                        ClefEvent clefEvent = (ClefEvent) event;
                        if (clefEvent.getClef().signature()) {
                            importSignatureClef(staff, layer, clefEvent);
                        } else {
                            importClef(staff, layer, (ClefEvent) event);
                        }
                        break;
                    case Event.EVENT_MENS:
                        importMensuration(staff, layer, (MensEvent) event);
                        break;
                    case Event.EVENT_NOTE:
                        importNote(staff, layer, (NoteEvent) event);
                        break;
                    case Event.EVENT_REST:
                        importRest(staff, layer, (RestEvent) event);
                        break;
                    case Event.EVENT_DOT:
                        importDot(staff, layer, (DotEvent) event);
                        break;
                    case Event.EVENT_ORIGINALTEXT:
                        importText(staff, layer, (OriginalTextEvent) event);
                        break;
                    case Event.EVENT_CUSTOS:
                        importCustos(staff, layer, (CustosEvent) event);
                        break;
                    case Event.EVENT_LINEEND:
                        importLineEnd(staff, layer, (LineEndEvent) event);
                        break;
                    case Event.EVENT_SECTIONEND:
                        importSectionEnd(staff, layer, event);
                        break;
                    case Event.EVENT_PROPORTION:
                        importProportion(staff, layer, (ProportionEvent) event);
                        break;
                    case Event.EVENT_COLORCHANGE:
                        importColorChange(staff, layer, (ColorChangeEvent) event);
                        break;
                    case Event.EVENT_BARLINE:
                        importBarLine(staff, layer, (BarlineEvent) event);
                        break;
                    case Event.EVENT_ANNOTATIONTEXT:
                        importAnnotationText(staff, layer, (AnnotationTextEvent) event);
                        break;
                    case Event.EVENT_LACUNA: // no notes played
                        importLacunaStart(staff, layer, (LacunaEvent) event);
                        break;
                    case Event.EVENT_LACUNA_END:
                        importLacunaEnd(staff, layer, event);
                        break;
                    case Event.EVENT_MODERNKEYSIGNATURE:
                        importModernKey(staff, layer, (ModernKeySignatureEvent) event);
                        break;
                    case Event.EVENT_MULTIEVENT:
                        importMultiEvent(staff, layer, (MultiEvent) event);
                        break;
                    case Event.EVENT_ELLIPSIS:
                        importEllipsis(staff, layer, event);
                        break;
                    case Event.EVENT_VARIANTDATA_START:
                        importVariantStart(staff, layer, event);
                        break;
                    case Event.EVENT_VARIANTDATA_END:
                        importVariantEnd(staff, layer, event);
                        break;
                    case Event.EVENT_BLANK:
                        importBlank(staff, layer, event);
                        break;
                    default:
                        throw new ImportException("Unsupported event type: " + event.geteventtype());
                }
            }
        }
    }


    private void importBlank(Staff staff, ScoreLayer layer, Event event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importVariantEnd(Staff staff, ScoreLayer layer, Event event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importVariantStart(Staff staff, ScoreLayer layer, Event event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importEllipsis(Staff staff, ScoreLayer layer, Event event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importMultiEvent(Staff staff, ScoreLayer layer, MultiEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importModernKey(Staff staff, ScoreLayer layer, ModernKeySignatureEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importLacunaEnd(Staff staff, ScoreLayer layer, Event event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importLacunaStart(Staff staff, ScoreLayer layer, LacunaEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importAnnotationText(Staff staff, ScoreLayer layer, AnnotationTextEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importBarLine(Staff staff, ScoreLayer layer, BarlineEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importColorChange(Staff staff, ScoreLayer layer, ColorChangeEvent event) {
        System.out.println("TODO Skipping color change");
        event.getcolorscheme().prettyprint();
    }

    private void importProportion(Staff staff, ScoreLayer layer, ProportionEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importSectionEnd(Staff staff, ScoreLayer layer, Event event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importLineEnd(Staff staff, ScoreLayer layer, LineEndEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importCustos(Staff staff, ScoreLayer layer, CustosEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importText(Staff staff, ScoreLayer layer, OriginalTextEvent event) {
        System.out.println("Pending: " + event.toString());
    }

    private void importDot(Staff staff, ScoreLayer layer, DotEvent event) throws ImportException {
        if (lastFigureAtom == null) {
            throw new ImportException("There is not a previous note or rest to add the dot");
        }
        // use set dots to avoid increasing the duration that is already specified in the note/length element
        lastFigureAtom.getAtomFigure().setDots(lastFigureAtom.getAtomFigure().getDots()+1);
    }

    private Figures convertFigure(int noteType) throws ImportException {
        Figures figure;
        switch (noteType) {
            case NoteEvent.NT_Maxima:
                figure = Figures.MAXIMA;
                break;
            case NoteEvent.NT_Longa:
                figure = Figures.LONGA;
                break;
            case NoteEvent.NT_Brevis:
                figure = Figures.BREVE;
                break;
            case NoteEvent.NT_Semibrevis:
                figure = Figures.SEMIBREVE;
                break;
            case NoteEvent.NT_Minima:
                figure = Figures.MINIM;
                break;
            case NoteEvent.NT_Semiminima:
                figure = Figures.SEMIMINIM;
                break;
            case NoteEvent.NT_Fusa:
                figure = Figures.FUSA;
                break;
            case NoteEvent.NT_Semifusa:
                figure = Figures.SEMIFUSA;
                break;
            default:
                throw new ImportException("Unsupported figure type: " + NoteEvent.NoteTypeNames[noteType]);
        }
        return figure;
    }
    private void importRest(Staff staff, ScoreLayer layer, RestEvent event) throws IM3Exception {
        Figures figure = convertFigure(event.getnotetype());
        SimpleRest rest = new SimpleRest(figure, 0);
        rest.setStaff(staff);
        layer.add(rest);
        staff.addCoreSymbol(rest);
        lastFigureAtom = rest;
    }

    private Time proportionToFraction(Proportion proportion) {
        return new Time(Fraction.getFraction(proportion.i1, proportion.i2));
    }

    /**
     * Copied from MusicXMLGenerator
     * @param p
     * @return
     */
    static int CMMEtoMusicXMLOctave(Pitch p)
    {
        return p.octave+(p.noteletter>'B' ? 1 : 0);
    }

    private void importNote(Staff staff, ScoreLayer layer, NoteEvent event) throws ImportException, IM3Exception {
        Figures figure = convertFigure(event.getnotetype());
        DiatonicPitch noteName = DiatonicPitch.noteFromName(Character.toUpperCase(event.getPitch().noteletter));
        int octave = CMMEtoMusicXMLOctave(event.getPitch());
        Accidentals cmmeAccidentals = Accidentals.alterToAccidentals(event.getPitchOffset().calcPitchOffset());
        Accidentals ksAccidental = staff.findCurrentKeySignatureAccidental(layer.getDuration(), noteName);

        Accidentals acc;
        if (cmmeAccidentals != Accidentals.NATURAL) {
            acc = cmmeAccidentals;
        } else if (ksAccidental != null) {
            acc = ksAccidental;
        } else {
            acc = null;
        }

        ScientificPitch pitch = new ScientificPitch(new PitchClass(noteName, acc), octave);


        // TODO Stem
        // TODO Accidentals editoriales
        int dots = 0;
        if (event.hasModernDot()) {
            dots = 1;
        }
        SimpleNote note = new SimpleNote(figure, dots, pitch);
        Proportion proportion = event.getLength();
        Time actualDuration = proportionToFraction(proportion);
        Time expectedDurationGivenFigure = figure.getDuration();

        if (!actualDuration.equals(expectedDurationGivenFigure)) {
            note.setDuration(actualDuration);
        }


        note.setStaff(staff);
        layer.add(note);
        staff.addCoreSymbol(note);
        lastFigureAtom = note;
    }

    private void importMensuration(Staff staff, ScoreLayer layer, MensEvent event) throws IM3Exception {
        //TODO event.small()
        TimeSignature ts = null;
        switch (event.getMainSign().signType) {
            case MensSignElement.MENS_SIGN_C:
                if (!event.getMainSign().dotted && event.getMainSign().number == null) {
                    if (event.getMainSign().stroke) {
                        ts = new TimeSignatureCutTime(NotationType.eMensural);
                    } else {
                        ts = new TimeSignatureCommonTime(NotationType.eMensural);
                    }
                }
                break;
        }

        if (ts == null) {
            System.out.println("TO-DO COMPAS:");
            event.prettyprint();
            ts = TimeSignatureMensuralFactory.getInstance().create(
                    Perfection.getPerfection(event.getMensInfo().modus_maior),
                    Perfection.getPerfection(event.getMensInfo().modus_minor),
                    Perfection.getPerfection(event.getMensInfo().tempus),
                    Perfection.getPerfection(event.getMensInfo().prolatio)
            );
        }

        ts.setTime(layer.getDuration());
        staff.addTimeSignature(ts);
    }

    private void importClef(Staff staff, ScoreLayer layer, ClefEvent event) throws ImportException, IM3Exception {
        es.ua.dlsi.im3.core.score.Clef clef = null;
        switch (event.getClef().getcleftype()) {
            case Clef.CLEF_C:
                switch (event.getClef().getloc()) {
                    case 1:
                        clef = new ClefC1();
                        break;
                    case 3:
                        clef = new ClefC2();
                        break;
                    case 5:
                        clef = new ClefC3();
                        break;
                    case 7:
                        clef = new ClefC4();
                        break;
                    case 9:
                        clef = new ClefC5();
                        break;
                }
                break;
            case Clef.CLEF_Frnd: // TODO: 27/3/18 ¿Qué clave es realmente? 
                switch (event.getClef().getloc()) {
                    case 5:
                        clef = new ClefF3();
                        break;
                    case 7:
                        clef = new ClefF4();
                        break;
                }
                break;
            case Clef.CLEF_F:
                switch (event.getClef().getloc()) {
                    case 5:
                        clef = new ClefF3();
                        break;
                    case 7:
                        clef = new ClefF4();
                        break;
                }
                break;
            case Clef.CLEF_G:
                switch (event.getClef().getloc()) {
                    case 1:
                        clef = new ClefG1();
                        break;
                    case 3:
                        clef = new ClefG2();
                        break;
                }
                break;
        }
        if (clef == null) {
            throw new ImportException("Unsupported clef: " + event);
        }
        clef.setTime(layer.getDuration());
        staff.addClef(clef);
    }
    /*private Accidentals convertAccDistance(int accDistance) throws ImportException {
        switch (accDistance) {
            case 1: return Accidentals.SHARP;
            case -1: return Accidentals.FLAT;
            case 2: return Accidentals.DOUBLE_SHARP;
            default: throw new ImportException("Unsupported accDistance: " + accDistance);
        }

    }*/

    private void importSignatureClef(Staff staff, ScoreLayer layer, ClefEvent clefEvent) throws IM3Exception {
        ModernKeySignature mks = clefEvent.getModernKeySig();
        // TODO: 28/3/18 Ver si hay otras claves no modernas que haya que gestionar distintas
        try {
            KeySignature ks = new KeySignature(NotationType.eMensural, new Key(mks.getAccDistance(), Mode.UNKNOWN));
            ks.setTime(layer.getDuration());
            staff.addKeySignature(ks);
        } catch (IM3Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot convert key signature", e);
        }
    }

}
