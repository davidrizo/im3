package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;

import java.util.ArrayList;
import java.util.List;

public class MensuralToModern {
    private KeySignature activeKeySignature;
    private TimeSignature activeTimeSignature;
    private Time pendingMensureDuration;
    private Measure currentMeasure;


    /**
     * It changes the duration of the atoms in the mensural song applying the durationMultiplier - important for correcly aligning layout spacing
     * @param mensural
     * @return
     */
    public ScoreSong convertIntoNewSong(ScoreSong mensural, Intervals transposition) throws IM3Exception {
        ScoreSong modernSong = new ScoreSong();
        for (ScorePart mensuralPart: mensural.getParts()) {
            ScorePart modernPart = new ScorePart(modernSong, mensuralPart.getNumber());
            modernSong.addPart(modernPart);

            if (mensuralPart.getLayers().size() != 1) {
                throw new IM3Exception("Just 1 layer is supported in a mensural input, and there are " +
                        mensuralPart.getLayers().size());
            }
            ScoreLayer mensuralLayer = mensuralPart.getLayers().iterator().next();
            
            if (mensuralPart.getStaves().size() != 1) {
                // TODO: 6/10/17 ¿Sólo 1 staff?
                throw new IM3Exception("Just 1 staff is supported in a mensural input, and there are " +
                        mensuralPart.getStaves().size());
            }

            Staff mensuralStaff = mensuralPart.getStaves().iterator().next();
            Pentagram modernPentagram = new Pentagram(modernSong, mensuralStaff.getHierarchicalOrder(), mensuralStaff.getNumberIdentifier());
            modernPentagram.setNotationType(NotationType.eModern);
            modernPart.addStaff(modernPentagram);
            modernSong.addStaff(modernPentagram);
            ScoreLayer modernLayer = modernPart.addScoreLayer(modernPentagram);

            // TODO: 16/10/17 Que se calcule en función de la teoría musical 
            convertIntoStaff(mensuralStaff, modernPentagram, modernLayer, transposition);
        }
        return modernSong;
    }

    // TODO: 6/10/17 Estoy convirtiendo sólo binario

    /**
     * It resets the modern output layer and staff contents and leaves the result inside
     * @param mensuralStaff
     * @param modernStaff
     * @param modernLayer
     * @throws IM3Exception
     */
    public void convertIntoStaff(Staff mensuralStaff, Staff modernStaff, ScoreLayer modernLayer, Intervals transposition) throws IM3Exception {
        modernStaff.clear();
        modernLayer.clear();

        Interval interval = transposition.createInterval();
        activeTimeSignature = null;
        activeKeySignature = null;
        pendingMensureDuration = null;
        currentMeasure = new Measure(modernStaff.getScoreSong());
        if (modernStaff.getScoreSong().getMeasureWithOnset(Time.TIME_ZERO) == null) {
            modernStaff.getScoreSong().addMeasure(Time.TIME_ZERO, currentMeasure);
        }

        for (ITimedElementInStaff symbol: mensuralStaff.getCoreSymbolsOrdered()) {
            if (symbol instanceof Clef) {
                modernStaff.addClef(convert((Clef) symbol)); // TODO: 16/10/17 Convertimos las claves?
            } else if (symbol instanceof TimeSignature) {
                activeTimeSignature = convert((TimeSignature) symbol);
                pendingMensureDuration = activeTimeSignature.getDuration();
                modernStaff.addTimeSignature(activeTimeSignature);
            } else if (symbol instanceof KeySignature) {
                activeKeySignature = convert((KeySignature) symbol, interval);
                modernStaff.addKeySignature(activeKeySignature);
            } else if (symbol instanceof MarkBarline) {
                convert(modernStaff, modernLayer, (MarkBarline) symbol);
            } else if (symbol instanceof Atom) {
                convert(modernStaff, modernLayer, (Atom) symbol, interval);
            } else if (symbol instanceof DisplacedDot) {
                // no-op It is never displaced, if always accompanies the pitch
            } else {
                throw new IM3Exception("Unsupported conversion of " + symbol.getClass());
            }
        }
        Time duration = modernLayer.getDuration();
        if (duration.equals(currentMeasure.getTime())) {
            // empty measure
            modernStaff.getScoreSong().removeMeasure(currentMeasure);
        } else {
            if (!currentMeasure.hasEndTime()) {
                currentMeasure.setEndTime(modernLayer.getDuration());
            }
            // TODO: 6/10/17 Puede que tengamos que rellenar con silencios
        }

        modernStaff.getScoreSong().numberMeasures();
        // now insert barlines between staves
        for (Measure measure: modernStaff.getScoreSong().getMeasures()) {
            DashedBarlineAcrossStaves dashedBarlineAcrossStaves = new DashedBarlineAcrossStaves(measure, modernStaff, mensuralStaff);
            modernStaff.addConnector(dashedBarlineAcrossStaves);
            mensuralStaff.addConnector(dashedBarlineAcrossStaves);
        }
    }

    private void convert(Staff modernStaff, ScoreLayer modernLayer, MarkBarline symbol) {
        // TODO: 6/10/17 No estoy tratándolo
    }

    private KeySignature convert(KeySignature symbol, Interval transposition) throws IM3Exception {
        // TODO: 6/10/17 ¿Cómo convertimos? ¿Qué transposición?
        PitchClass newPitch = transposition.computePitchClassFrom(symbol.getInstrumentKey().getPitchClass());
        Key modernKey = new Key(newPitch, symbol.getInstrumentKey().getMode());
        KeySignature keySignature = new KeySignature(NotationType.eModern, modernKey);
        return keySignature;
    }

    private TimeSignature convert(TimeSignature symbol) throws IM3Exception {
        // TODO: 6/10/17 ¿Cómo convertimos?
        TimeSignature modernTimeSignature;
        if (symbol instanceof TimeSignatureCommonTime) {
            modernTimeSignature = new TimeSignatureCommonTime(NotationType.eModern);
        } else {
            throw new IM3Exception("Unsupported time signature " + symbol);
        }
        return modernTimeSignature;
    }

    private Clef convert(Clef symbol) {
        // TODO: 6/10/17 ¿Cómo convertimos?
        Clef modernClef = symbol.clone();
        modernClef.setNotationType(NotationType.eModern);
        return modernClef;
    }

    // TODO: 6/10/17 Ligatures....
    private void convert(Staff modernStaff, ScoreLayer modernLayer, Atom atom, Interval transposition) throws IM3Exception {
        if (!(atom instanceof SingleFigureAtom)) {
            throw new IM3Exception("Unsupported atom type " + atom.getClass());
        }

        SingleFigureAtom singleFigureAtom = (SingleFigureAtom) atom;

        //int dots = singleFigureAtom.getAtomFigure().getDots();
        //Figures figure = singleFigureAtom.getAtomFigure().getFigure();
        Time pendingDuration = singleFigureAtom.getAtomFigure().getDuration();

        modernLayer.setDurationEvaluator(new DurationEvaluator()); // TODO - it does not use the evaluator of the MensuralSong

        // TODO: 6/10/17 ¿Puede cambiar el compás por enmedio?
        AtomPitch lastAtomPitch = null;
        while (!pendingDuration.isZero()) {
            if (pendingMensureDuration.isNegative()) {
                throw new IM3RuntimeException("Cannot have a negative pending measure duration: " + pendingMensureDuration);
            }
            if (pendingMensureDuration.isNegative()) {
                throw new IM3RuntimeException("Cannot have a negative pending duration: " + pendingDuration);
            }

            Time outputFigureDuration = Time.min(pendingMensureDuration, pendingDuration);
            List<RhythmUtils.FigureAndDots> outputFiguresAndDots;
            try {
                outputFiguresAndDots = RhythmUtils.findRhythmForDuration(NotationType.eModern, outputFigureDuration);
            } catch (IM3Exception e) {
                throw new IM3Exception("Error translating pending duration " + pendingDuration + " for figure " + singleFigureAtom, e);
            }

            SimpleNote prevNote = null;
            for (RhythmUtils.FigureAndDots outputFigureAndDots: outputFiguresAndDots) {
                SingleFigureAtom outputAtom = null;
                if (singleFigureAtom instanceof SimpleRest) {
                    outputAtom = new SimpleRest(outputFigureAndDots.getFigure(), outputFigureAndDots.getDots());
                } else if (singleFigureAtom instanceof SimpleNote) {
                    SimpleNote note = new SimpleNote(outputFigureAndDots.getFigure(), outputFigureAndDots.getDots(),
                            convertPitch(((SimpleNote)singleFigureAtom).getPitch(), transposition));
                    outputAtom = note;

                    if (lastAtomPitch != null) {
                        lastAtomPitch.setTiedToNext(note.getAtomPitch());
                    }

                    lastAtomPitch = note.getAtomPitch();

                    if (prevNote != null) {
                        note.getAtomPitch().setTiedFromPrevious(prevNote.getAtomPitch());
                    }
                    prevNote = note;
                } else {
                    throw new IM3Exception("Unsupported single figure atom type: " + singleFigureAtom.getClass());
                }
                modernLayer.add(outputAtom);
                modernStaff.addCoreSymbol(outputAtom);
            }
            pendingDuration = pendingDuration.substract(outputFigureDuration);
            pendingMensureDuration = pendingMensureDuration.substract(outputFigureDuration);

            if (pendingMensureDuration.isZero()) {
                Time time = modernLayer.getDuration();
                if (modernStaff.getScoreSong().getMeasureWithOnset(time) == null) {
                    currentMeasure.setEndTime(time);
                    currentMeasure = new Measure(modernStaff.getScoreSong());
                    modernStaff.getScoreSong().addMeasure(time, currentMeasure);
                }
                pendingMensureDuration = activeTimeSignature.getDuration();
            }
        }
    }

    private ScientificPitch convertPitch(ScientificPitch pitch, Interval transposition) throws IM3Exception {
        // TODO: 6/10/17 Conversión de altura - ¿qué transposición?
        // Usamos activeKeySignature - quizás deberíamos guardar también la activa del origen
        ScientificPitch modern = transposition.computeScientificPitchFrom(pitch);
        return modern;
    }

    /**
     * It adds the staves of song2 into song1. If alternate is true it alternates song1 staves with song 2 staves. If
     * it is false it appends the song2 at the end of song1. The parts are in both cases appended at the end
     * It removes the parts and staves from song2. It copies the measures from song2 to song1
     */
    public void merge(ScoreSong song1, ScoreSong song2, boolean alternate) throws IM3Exception {
        int expectedStaves = song1.getStaves().size() + song2.getStaves().size();

        ArrayList<ScorePart> song1Parts = song1.getPartsSortedByNumberAsc();
        for (ScorePart part: song2.getParts()) {
            part.setSong(song1);
            part.setNumber(1000 + part.getNumber()); //TODO
            song1.addPart(part);
        }
        song2.clearParts();
        song1.clearMeasures();

        for (Measure fromMeasure: song2.getMeasures()) {
            Measure newMeasure = new Measure(song1, fromMeasure.getNumber());
            newMeasure.setTime(fromMeasure.getTime());
            newMeasure.setEndTime(fromMeasure.getEndTime());
            song1.addMeasure(fromMeasure.getTime(), newMeasure);
        }

        if (!alternate) {
            // TODO: 16/10/17 Hierarchical order, number...
            for (Staff staff: song2.getStaves()) {
                staff.setSong(song1);
                staff.setHierarchicalOrder("Z-" + staff.getHierarchicalOrder()); //TODO
                staff.setNumberIdentifier(1000+staff.getNumberIdentifier()); //TODO
                song1.addStaff(staff);
            }
            song2.clearStaves();
        } else {
            if (song1.getStaves().size() != song2.getStaves().size()) {
                throw new IM3Exception("song1 has " + song1.getStaves().size() + " and song2 has " + song2.getStaves().size());
            }
            for (int i=0; i<song2.getStaves().size(); i++) {
                Staff staff = song2.getStaves().get(i);
                staff.setSong(song1);
                staff.setHierarchicalOrder("Z-" + staff.getHierarchicalOrder()); //TODO
                staff.setNumberIdentifier(1000+staff.getNumberIdentifier()); //TODO
                song1.addStaffAt((2*i)+1, staff);
            }
            song2.clearStaves();

            if (song1.getStaves().size() != expectedStaves) {
                throw new IM3Exception("Expected a result of " + expectedStaves + " and obtained " + song1.getStaves().size());
            }
        }

    }

}
