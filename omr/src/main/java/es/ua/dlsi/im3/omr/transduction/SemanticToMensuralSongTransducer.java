package es.ua.dlsi.im3.omr.transduction;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.conversions.FigureAndDots;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.ImportFactories;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import es.ua.dlsi.im3.omr.model.Constants;
import es.ua.dlsi.im3.omr.model.pojo.SemanticToken;

import java.util.List;

/**
 * TO-DO Esto se hace mejor con una gramática
 */
public class SemanticToMensuralSongTransducer implements ISemanticToScoreSongTransducer {
    private static final String METERSIGN_COMMON_TIME = "C";
    private static final String METERSIGN_CUT_TIME = "C/";
    private static final String DIV = "/";
    private ScoreLayer scoreLayer;
    private Staff staff;
    private Time currentTime;

    @Override
    public void transduceInto(List<SemanticToken> tokens, Staff scoreStaff, ScoreLayer scoreLayer) throws IM3Exception {
        this.scoreLayer = scoreLayer;
        this.staff = scoreStaff;
        currentTime = Time.TIME_ZERO;
        for (SemanticToken token: tokens) {
            switch (token.getSymbol()) {
                case barline:
                    addBarline();
                    break;
                case clef:
                    addClef(token.getValue());
                    break;
                case gracenote:
                    addGraceNote(token.getValue());
                    break;
                case keySignature:
                    addKeySignature(token.getValue());
                    break;
                case line:
                    addLine(token.getValue());
                    break;
                case multirest:
                    addMultirest(token.getValue());
                    break;
                case note:
                    addNote(token.getValue());
                    break;
                case rest:
                    addRest(token.getValue());
                    break;
                case slur:
                    addSlur(token.getValue());
                    break;
                case tie:
                    addTie(token.getValue());
                    break;
                case timeSignature:
                    addTimeSignature(token.getValue());
                    break;
                default:
                    throw new IM3Exception("Unsupported symbol " + token.getSymbol());
            }
        }
    }

    private void addTimeSignature(String value) throws IM3Exception {
        TimeSignature timeSignature = null;
        if (value.equals(METERSIGN_COMMON_TIME)) {
            timeSignature = new TimeSignatureCommonTime(NotationType.eMensural);
        } else if (value.equals(METERSIGN_CUT_TIME)) {
            timeSignature = new TimeSignatureCutTime(NotationType.eMensural);
        } else {
            //TODO Otros compases mensurales
            //TODO Amalgama
            String [] fraction = value.split(DIV);
            if (fraction.length != 2) {
                throw new ImportException("Expected C, C/, or two values separated by / in '" + value + "'");
            }
            timeSignature = new FractionalTimeSignature(Integer.parseInt(fraction[0]), Integer.parseInt(fraction[1]));
        }
        timeSignature.setTime(currentTime);
        staff.addTimeSignature(timeSignature);
    }

    private void addTie(String value) {
        throw new IM3RuntimeException("Unsupported operation");
    }

    private void addSlur(String value) {
        throw new IM3RuntimeException("Unsupported operation");
    }

    private void addRest(String value) throws IM3Exception {
        FigureAndDots figureAndDots = parseFigureAndDots(value);
        SimpleRest rest = new SimpleRest(figureAndDots.getFigure(), figureAndDots.getDots());
        staff.addCoreSymbol(rest);
        scoreLayer.add(rest);
        currentTime = scoreLayer.getDuration();
    }

    private FigureAndDots parseFigureAndDots(String value) throws IM3Exception {
        Figures figure = null;
        String valueUpper = value.toUpperCase();
        for (int i=0; figure == null && i < Figures.values().length; i++) {
            if (Figures.values()[i].getNotationType() == NotationType.eMensural && valueUpper.startsWith(Figures.values()[i].name())) {
                figure = Figures.values()[i];
            }
        }
        if (figure == null) {
            throw new IM3Exception("No figure starts with a prefix of '" + value + "'");
        }
        valueUpper = valueUpper.substring(figure.name().length());
        int dots = 0;
        while (!valueUpper.isEmpty() && valueUpper.charAt(0) == '.') {
            dots++;
            valueUpper = valueUpper.substring(1);
        }
        //TODO Fermata
        return new FigureAndDots(figure, dots);
    }

    private void addNote(String value) throws IM3Exception {
        String [] pitch_duration = value.split(Constants.SEMATIC_VALUE_SEPARATOR);
        if (pitch_duration.length != 2) {
            throw new IM3Exception("Expected pitch and duration separated by " + Constants.SEMATIC_VALUE_SEPARATOR + " and found '" + value + "'");
        }
        FigureAndDots figureAndDots = parseFigureAndDots(pitch_duration[1]);
        //TODO Gracenote, trills

        String pitchStr = pitch_duration[0];
        String noteName = pitchStr.substring(0,1);
        pitchStr = pitchStr.substring(1);
        DiatonicPitch diatonicPitch = DiatonicPitch.noteFromName(noteName);
        Accidentals accidental = null;
        try {
            accidental = Accidentals.accidentalFromName(pitchStr.substring(0,1));
            pitchStr = pitchStr.substring(1);
        } catch (IM3Exception e) {
            // no-op
        }

        int octave = Integer.valueOf(pitchStr);
        PitchClass pitchClass = new PitchClass(diatonicPitch, accidental);
        ScientificPitch scientificPitch = new ScientificPitch(pitchClass, octave);
        SimpleNote simpleNote = new SimpleNote(figureAndDots.getFigure(), figureAndDots.getDots(), scientificPitch);
        staff.addCoreSymbol(simpleNote);
        scoreLayer.add(simpleNote);
        currentTime = scoreLayer.getDuration();
    }

    private void addMultirest(String value) {
        throw new IM3RuntimeException("Unsupported operation");
    }

    private void addLine(String value) {
        throw new IM3RuntimeException("Unsupported operation");
    }

    private void addKeySignature(String value) throws IM3Exception {
        if (value.length() < 1) {
            throw new IM3Exception("Expected key signature value length of at least 1 in '" + value + "'");
        }
        String noteName = value.substring(0,1);
        String s1 = null;
        String s2 = null;
        if (value.length() >=2) {
            s1 = value.substring(1, 2);
            if (value.length() == 3) {
                s2 = value.substring(2, 3);
            } else {
                throw new IM3Exception("Expected key signature value length of at most 3 in '" + value + "'");
            }
        }
        DiatonicPitch diatonicPitch = DiatonicPitch.valueOf(noteName.toUpperCase());
        Mode mode = Mode.UNKNOWN;
        Accidentals accidental = null;

        if (s1 != null) {
            if (s2 != null) {
                accidental = Accidentals.accidentalFromName(s1);
                mode = Mode.stringToMode(s1);
            } else {
                try {
                    accidental = Accidentals.accidentalFromName(s1);
                } catch (IM3Exception e) {
                    mode = Mode.stringToMode(s1);
                }
            }
        }

        Key key = new Key(new PitchClass(diatonicPitch, accidental), mode);
        KeySignature keySignature = new KeySignature(NotationType.eMensural, key);
        keySignature.setTime(currentTime);
        staff.addKeySignature(keySignature);
    }

    private void addGraceNote(String value) {
        throw new IM3RuntimeException("Unsupported operation");
    }

    private void addClef(String value) throws IM3Exception {
        if (value.length() != 2) {
            throw new IM3Exception("Expected clef value length of 2 in '" + value + "'");
        }
        String noteName = value.substring(0,1);
        String line = value.substring(1,2);
        Clef clef = ImportFactories.createClef(NotationType.eMensural, noteName, Integer.parseInt(line), 0); //TODO Octave change
        clef.setTime(currentTime);
        staff.addClef(clef);
    }

    private void addBarline() throws IM3Exception {
        staff.addMarkBarline(new MarkBarline(currentTime));

    }
}
