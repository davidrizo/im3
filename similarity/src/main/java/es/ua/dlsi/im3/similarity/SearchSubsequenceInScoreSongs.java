package es.ua.dlsi.im3.similarity;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.DiatonicPitch;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.midrepresentations.sequences.DiatonicPitchSequence;
import es.ua.dlsi.im3.midrepresentations.sequences.DiatonicPitchSequenceFromScoreSongEncoder;
import es.ua.dlsi.im3.midrepresentations.sequences.Sequence;
import es.ua.dlsi.im3.similarity.strings.ISymbolComparer;
import es.ua.dlsi.im3.similarity.strings.StringEditDistance;

import java.util.*;

/**
 * Created by drizo on 18/7/17.
 */
public class SearchSubsequenceInScoreSongs {
    private final ArrayList<ScoreSong> songs;
    /**
     * sequence indexes are aligned with those of songs, sequences[0] are the sequences of songs[0]
     */
    private final ArrayList<DiatonicPitchSequence> sequences;


    public SearchSubsequenceInScoreSongs(Collection<ScoreSong> songs) throws IM3Exception {
        this.songs = new ArrayList<>();
        this.sequences = new ArrayList<>();
        DiatonicPitchSequenceFromScoreSongEncoder encoder = new DiatonicPitchSequenceFromScoreSongEncoder();
        for (ScoreSong song: songs) {
            this.songs.add(song);
            this.sequences.add(encoder.encode(song));
        }
    }

    class NoteNamesComparer implements ISymbolComparer<DiatonicPitch> {
        @Override
        public double computeInsertCost(DiatonicPitch a) {
            return 1;
        }

        @Override
        public double computeDeleteCost(DiatonicPitch a) {
            return 1;
        }

        @Override
        public double computeSymbolDistance(DiatonicPitch a, DiatonicPitch b) {
            if (a.equals(b)) {
                return 0;
            } else {
                return 2;
            }
        }
    }

    //TODO Usar local edit distance para buscar

    /**
     * Returns a sorted list of results by value
     * @param query
     * @return
     */
    public List<Match<ScoreSong>> searchSubsequence(DiatonicPitch[] query) throws IM3Exception {
        ArrayList<Match<ScoreSong>> result = new ArrayList<>();
        StringEditDistance<DiatonicPitch, NoteNamesComparer> editDistance = new StringEditDistance(new NoteNamesComparer());

        Sequence<DiatonicPitch> squery = new Sequence<>("Query", query);
        for (int i=0; i<sequences.size(); i++) {
            double distance = editDistance.distance(squery, sequences.get(i));
            result.add(new Match<>(songs.get(i), distance));
        }

        Comparator<Match<ScoreSong>> comparator = new Comparator<Match<ScoreSong>>() {

            @Override
            public int compare(Match<ScoreSong> o1, Match<ScoreSong> o2) {
                double diff = o1.getValue() - o2.getValue();
                if (diff < 0) {
                    return -1;
                } else if (diff > 0 ) {
                    return 1;
                } else if (o1.getElement() == o2.getElement()) { // same element
                    return 0;
                } else {
                    return o1.hashCode()-o2.hashCode();
                }
            }
        };
        result.sort(comparator);
        return result;
    }
}
