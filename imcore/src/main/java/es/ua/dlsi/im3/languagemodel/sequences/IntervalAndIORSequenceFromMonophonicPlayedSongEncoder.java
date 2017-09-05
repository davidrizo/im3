package es.ua.dlsi.im3.languagemodel.sequences;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.played.*;
import es.ua.dlsi.im3.languagemodel.IMidLevelRepresentationEncoder;

import java.util.ArrayList;
import java.util.List;

//TODO Test unitario

/**
 * Created by drizo on 11/8/17.
 */
public class IntervalAndIORSequenceFromMonophonicPlayedSongEncoder implements IMidLevelRepresentationEncoder<PlayedSong, CoupledNoteSequence<Integer, Double>> {
    @Override
    public CoupledNoteSequence<Integer, Double> encode(PlayedSong input) throws IM3Exception {
        SongTrack voice = input.getUniqueVoice();
        MultipleMidRepresentationsFromSongTrackExtractor extractor = new MultipleMidRepresentationsFromSongTrackExtractor(voice);

        ArrayList<Integer> pitchIntervals = extractor.getPitchIntervals();
        ArrayList<Double> IORs = extractor.getIORs();

        if (pitchIntervals.size() != IORs.size()) {
            throw new IM3RuntimeException("Pitch intervals size ( " + pitchIntervals.size() + ") != IOR size (" + IORs.size() + ")");
        }

        if (pitchIntervals.isEmpty()) {
            throw new IM3Exception("Empty track");
        }

        ArrayList<CoupledNoteRepresentation<Integer, Double>> items = new ArrayList<>();
        for (int i=0; i<pitchIntervals.size(); i++) {
            CoupledNoteRepresentation<Integer, Double> cnr = new CoupledNoteRepresentation<>(pitchIntervals.get(i), IORs.get(i));
            items.add(cnr);
        }
        return new CoupledNoteSequence<Integer, Double>("Interval and IOR sequence", items);
    }

    @Override
    public List<CoupledNoteSequence<Integer, Double>> encode(PlayedSong input, int windowSize, int windowStep) throws IM3Exception {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
