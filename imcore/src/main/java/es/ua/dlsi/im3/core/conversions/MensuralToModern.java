package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;

public class MensuralToModern {
    private KeySignature activeKeySignature;
    private TimeSignature activeTimeSignature;
    private Time pendingMensureDuration;
    private Measure currentMeasure;


    /**
     * It creates a modern version of the mensural song
     * @param mensural
     * @return
     */
    public ScoreSong convertIntoNewSong(ScoreSong mensural) throws IM3Exception {
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

            convertIntoStaff(mensuralStaff, modernPentagram, modernLayer);
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
    public void convertIntoStaff(Staff mensuralStaff, Staff modernStaff, ScoreLayer modernLayer) throws IM3Exception {
        modernStaff.clear();
        modernLayer.clear();

        activeTimeSignature = null;
        activeKeySignature = null;
        pendingMensureDuration = null;
        currentMeasure = new Measure(modernStaff.getScoreSong());
        modernStaff.getScoreSong().addMeasure(Time.TIME_ZERO, currentMeasure);

        for (ITimedElementInStaff symbol: mensuralStaff.getCoreSymbolsOrdered()) {
            if (symbol instanceof Clef) {
                modernStaff.addClef(convert((Clef) symbol));
            } else if (symbol instanceof TimeSignature) {
                activeTimeSignature = convert((TimeSignature) symbol);
                pendingMensureDuration = activeTimeSignature.getDuration();
                modernStaff.addTimeSignature(activeTimeSignature);
            } else if (symbol instanceof KeySignature) {
                activeKeySignature = convert((KeySignature) symbol);
                modernStaff.addKeySignature(activeKeySignature);
            } else if (symbol instanceof MarkBarline) {
                convert(modernStaff, modernLayer, (MarkBarline) symbol);
            } else if (symbol instanceof Atom) {
                convert(modernStaff, modernLayer, (Atom) symbol);
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
    }

    private void convert(Staff modernStaff, ScoreLayer modernLayer, MarkBarline symbol) {
        // TODO: 6/10/17 No estoy tratándolo
    }

    private KeySignature convert(KeySignature symbol) throws IM3Exception {
        // TODO: 6/10/17 ¿Cómo convertimos? ¿Qué transposición?
        Key modernKey = new Key(symbol.getInstrumentKey().getPitchClass(), symbol.getInstrumentKey().getMode());
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
    private void convert(Staff modernStaff, ScoreLayer modernLayer, Atom atom) throws IM3Exception {
        if (!(atom instanceof SingleFigureAtom)) {
            throw new IM3Exception("Unsupported atom type " + atom.getClass());
        }

        SingleFigureAtom singleFigureAtom = (SingleFigureAtom) atom;

        //int dots = singleFigureAtom.getAtomFigure().getDots();
        //Figures figure = singleFigureAtom.getAtomFigure().getFigure();
        Time pendingDuration = singleFigureAtom.getAtomFigure().getDuration();

        // TODO: 6/10/17 ¿Puede cambiar el compás por enmedio?
        AtomPitch lastAtomPitch = null;
        while (!pendingDuration.isZero()) {
            if (pendingMensureDuration.isNegative()) {
                throw new IM3RuntimeException("Cannot have a negative pending measure duration: " + pendingMensureDuration);
            }
            Time outputFigureDuration = Time.min(pendingMensureDuration, pendingDuration);
            RhythmUtils.FigureAndDots outputFigureAndDots = RhythmUtils.findRhythmForDuration(NotationType.eModern, outputFigureDuration);

            SingleFigureAtom outputAtom = null;
            if (singleFigureAtom instanceof SimpleRest) {
                outputAtom = new SimpleRest(outputFigureAndDots.getFigure(), outputFigureAndDots.getDots());
            } else if (singleFigureAtom instanceof SimpleNote) {
                SimpleNote note = new SimpleNote(outputFigureAndDots.getFigure(), outputFigureAndDots.getDots(),
                        convertPitch(((SimpleNote)singleFigureAtom).getPitch()));
                outputAtom = note;

                if (lastAtomPitch != null) {
                    lastAtomPitch.setTiedToNext(note.getAtomPitch());
                }

                lastAtomPitch = note.getAtomPitch();
            } else {
                throw new IM3Exception("Unsupported single figure atom type: " + singleFigureAtom.getClass());
            }
            modernLayer.add(outputAtom);
            modernStaff.addCoreSymbol(outputAtom);
            pendingDuration = pendingDuration.substract(outputFigureDuration);
            pendingMensureDuration = pendingMensureDuration.substract(outputFigureDuration);
            if (pendingMensureDuration.isZero()) {
                Time time = modernLayer.getDuration();
                currentMeasure.setEndTime(time);
                currentMeasure = new Measure(modernStaff.getScoreSong());
                modernStaff.getScoreSong().addMeasure(time, currentMeasure);
                pendingMensureDuration = activeTimeSignature.getDuration();
            }
        }
    }

    private ScientificPitch convertPitch(ScientificPitch pitch) {
        // TODO: 6/10/17 Conversión de altura - ¿qué transposición?
        // Usamos activeKeySignature - quizás deberíamos guardar también la activa del origen
        ScientificPitch modern = pitch.clone();
        return modern;
    }


}
