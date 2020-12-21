package es.ua.dlsi.im3.omr.encoding.semantic;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.MarkBarline;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import es.ua.dlsi.im3.omr.encoding.semantic.semanticsymbols.SemanticNote;

import java.util.LinkedList;
import java.util.List;

//TODO La generación de IDs sólo va para **mens / **kern monofonico!!!
/**
 * It converts the semantic encoding to a ScoreSong.
 * It assigns each core symbol an ID prepended by an L
 */
public class Semantic2IMCore {

    public List<Pair<SemanticSymbol, ITimedElementInStaff>> convert(NotationType notationType, SemanticEncoding semanticEncoding) throws IM3Exception {
    //public List<Pair<SemanticSymbol, ITimedElementInStaff>> convert(NotationType notationType, TimeSignature lastTimeSignature, KeySignature lastKeySignature, SemanticEncoding semanticEncoding) throws IM3Exception {
        /*SemanticConversionContext semanticConversionContext = new SemanticConversionContext(notationType);
        semanticConversionContext.setCurrentKeySignature(lastKeySignature);
        semanticConversionContext.setCurrentTimeSignature(lastTimeSignature);*/
        List<Pair<SemanticSymbol, ITimedElementInStaff>> conversion = new LinkedList<>();
        SimpleNote pendingTiePreviousSymbol = null; //TODO para acordes
        long nextSemanticSymbolID = 0L;
        for (SemanticSymbol semanticSymbol: semanticEncoding.getSymbols()) {
            semanticSymbol.setId(nextSemanticSymbolID);
            if (semanticSymbol.getSymbol() != null && semanticSymbol.getSymbol().getCoreSymbol() != null) {
                semanticSymbol.getSymbol().getCoreSymbol().__setID("L" + nextSemanticSymbolID);
            }
            nextSemanticSymbolID++;
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

    public ScoreSong convertToSingleVoicedSong(NotationType notationType, SemanticEncoding semanticEncoding) throws IM3Exception {
        //List<Pair<SemanticSymbol, ITimedElementInStaff>> conversion = convert(notationType, null, null, semanticEncoding);
        List<Pair<SemanticSymbol, ITimedElementInStaff>> conversion = convert(notationType, semanticEncoding);

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
                if (timedElementInStaff instanceof Atom) {
                    singleLayer.add((Atom) timedElementInStaff);
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
