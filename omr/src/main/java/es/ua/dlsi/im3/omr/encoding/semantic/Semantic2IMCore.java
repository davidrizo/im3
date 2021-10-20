package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.SemanticNote;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//TODO La generación de IDs sólo va para **mens / **kern monofonico!!!
/**
 * It converts the semantic encoding to a ScoreSong.
 * It assigns each core symbol an ID prepended by an L (used in notation.component.ts)
 */
public class Semantic2IMCore {

    /**
     *
     * @param notationType
     * @param semanticEncoding
     * @param addStaffSyncLine True just for semantic encoding in MuRET
     * @return
     * @throws IM3Exception
     */
    public List<Pair<SemanticSymbol, ITimedElementInStaff>> convert(NotationType notationType, SemanticEncoding semanticEncoding, boolean addStaffSyncLine) throws IM3Exception {
    //public List<Pair<SemanticSymbol, ITimedElementInStaff>> convert(NotationType notationType, TimeSignature lastTimeSignature, KeySignature lastKeySignature, SemanticEncoding semanticEncoding) throws IM3Exception {
        /*SemanticConversionContext semanticConversionContext = new SemanticConversionContext(notationType);
        semanticConversionContext.setCurrentKeySignature(lastKeySignature);
        semanticConversionContext.setCurrentTimeSignature(lastTimeSignature);*/
        List<Pair<SemanticSymbol, ITimedElementInStaff>> conversion = new LinkedList<>();
        SimpleNote pendingTiePreviousSymbol = null; //TODO para acordes
        long nextSemanticSymbolID = 0L;
        for (SemanticSymbol semanticSymbol: semanticEncoding.getSymbols()) {
            if (addStaffSyncLine) {
                semanticSymbol.setId(nextSemanticSymbolID);
                if (semanticSymbol.getSymbol() != null && semanticSymbol.getSymbol().getCoreSymbol() != null) {
                    if (semanticSymbol.getSymbol().getCoreSymbol() instanceof CompoundAtom) {
                        CompoundAtom compoundAtom = (CompoundAtom) semanticSymbol.getSymbol().getCoreSymbol();
                        for (Atom atom : compoundAtom.getAtoms()) {
                            atom.__setID("L" + nextSemanticSymbolID);
                            nextSemanticSymbolID++;
                        }
                    } else {
                        semanticSymbol.getSymbol().getCoreSymbol().__setID("L" + semanticSymbol.getId());
                        nextSemanticSymbolID++;
                    }
                }
            }


            conversion.add(new Pair<>(semanticSymbol, semanticSymbol.getSymbol().getCoreSymbol()));
            if (semanticSymbol.getSymbol() instanceof SemanticNote) {
                SemanticNote semanticNote = (SemanticNote) semanticSymbol.getSymbol();
                if (pendingTiePreviousSymbol != null) {
                    semanticNote.getCoreSymbol().tieFromPrevious(pendingTiePreviousSymbol);
                    pendingTiePreviousSymbol = null;
                }
                if (semanticNote.isTiedToNext()) {
                    pendingTiePreviousSymbol = semanticNote.getCoreSymbol();
                }
            }
        }
        return conversion;
    }

    public ScoreSong convertToSingleVoicedSong(NotationType notationType, SemanticEncoding semanticEncoding, boolean addStaffSyncLine) throws IM3Exception {
        //List<Pair<SemanticSymbol, ITimedElementInStaff>> conversion = convert(notationType, null, null, semanticEncoding);
        List<Pair<SemanticSymbol, ITimedElementInStaff>> conversion = convert(notationType, semanticEncoding, addStaffSyncLine);

        ScoreSong song = new ScoreSong();
        Staff staff = new Pentagram(song, "1", 1); //TODO
        staff.setName("Converted");
        staff.setNotationType(notationType);
        song.addStaff(staff);
        ScorePart scorePart = song.addPart();
        scorePart.setName("Converted");
        scorePart.addStaff(staff);
        ScoreLayer singleLayer = scorePart.addScoreLayer(staff);

        if (notationType == NotationType.eModern) {
            Measure measure = new Measure(song, 1);
            song.addMeasure(Time.TIME_ZERO, measure); // first measure
        }

        BeamGroup beamGroup = null;
        SemanticNote lastNote = null;
        ArrayList<Atom> tupletAtoms = new ArrayList<>();
        boolean skipAtom = false;
        for (Pair<SemanticSymbol, ITimedElementInStaff> pair: conversion) {
            ITimedElementInStaff timedElementInStaff = pair.getY();
            if (notationType == NotationType.eModern && timedElementInStaff instanceof MarkBarline ) {
                Measure lastMeasure = song.getLastMeasure();
                // There are cases where a barline is encoded in agnostic in the first position of the staff
                // In those cases it's ignored
                if (!lastMeasure.getTime().equals(singleLayer.getDuration())) {
                    lastMeasure.setEndTime(singleLayer.getDuration());

                    Measure measure = new Measure(song, lastMeasure.getNumber() + 1);
                    staff.getScoreSong().addMeasure(lastMeasure.getEndTime(), measure);
                }
            } else {
                // add beams
                if (timedElementInStaff instanceof Atom) {
                    if (pair.getX().getSymbol() instanceof SemanticNote) {
                        SemanticNote semanticNote = (SemanticNote) pair.getX().getSymbol();
                        if (semanticNote.getSemanticBeamType() != null) {
                            switch (semanticNote.getSemanticBeamType()) {
                                case start:
                                    beamGroup = new BeamGroup(false);
                                    beamGroup.add(semanticNote.getCoreSymbol());
                                    break;
                                case inner:
                                    if (beamGroup == null) {
                                        throw new IM3Exception("Missing start beam");
                                    }
                                    beamGroup.add(semanticNote.getCoreSymbol());
                                    break;
                                case end:
                                    if (beamGroup == null) {
                                        throw new IM3Exception("Missing start beam");
                                    }
                                    beamGroup.add(semanticNote.getCoreSymbol());
                                    beamGroup = null;
                            }
                        }

                        if (lastNote != null && lastNote.isTiedToNext()) {
                            lastNote.getCoreSymbol().tieToNext(semanticNote.getCoreSymbol());
                        }

                        if (semanticNote.getTupletNumber() != null) {
                            skipAtom = true;
                            if (tupletAtoms == null) {
                                tupletAtoms = new ArrayList<>();
                            }
                            
                            if (tupletAtoms.size() < semanticNote.getTupletNumber()) {
                                if (!tupletAtoms.isEmpty() && !tupletAtoms.get(0).getAtomFigures().get(0).getFigure().equals(semanticNote.getCoreSymbol().getAtomFigure().getFigure())) {
                                    throw new IM3Exception("Unsupported mixed duration tuplets");
                                }
                                tupletAtoms.add(semanticNote.getCoreSymbol());
                            } 
                            
                            if (tupletAtoms.size() == semanticNote.getTupletNumber()) {
                                //TODO Generalize
                                int inSpaceOfAtoms;
                                switch (semanticNote.getTupletNumber()) {
                                    case 2:
                                        inSpaceOfAtoms = 3;
                                        break;
                                    case 3:
                                        inSpaceOfAtoms = 2;
                                        break;
                                    case 5:
                                    case 6:
                                    case 7:
                                        inSpaceOfAtoms = 4;
                                        break;
                                    default:
                                        throw new IM3Exception("Unsupported tuplet number " + semanticNote.getTupletNumber());
                                }
                                SimpleTuplet simpleTuplet = new SimpleTuplet(semanticNote.getTupletNumber(), inSpaceOfAtoms, tupletAtoms.get(0).getAtomFigures().get(0).getFigure(), tupletAtoms);
                                singleLayer.add(simpleTuplet);
                                tupletAtoms = null;
                            }
                        } else {
                            skipAtom = false;
                        }

                        lastNote = semanticNote;
                    }
                    if (!skipAtom) {
                        singleLayer.add((Atom) timedElementInStaff);
                    }
                } else {
                    staff.addElementWithoutLayer((IStaffElementWithoutLayer) timedElementInStaff);
                }
            }
        }

        if (notationType == NotationType.eModern) {
            // if not closed by an ending line
            if (!song.getLastMeasure().hasEndTime()) {
                if (song.getSongDuration().equals(song.getLastMeasure().getTime())) {
                    // if last measure is empty delete it
                    song.removeLastMeasure();
                } else {
                    song.getLastMeasure().setEndTime(song.getSongDuration());
                }
            }
        }
        return song;
    }

}
