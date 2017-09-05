package es.ua.dlsi.im3.languagemodel.sequences;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.NoteNames;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.languagemodel.IMidLevelRepresentationEncoder;

import java.util.ArrayList;
import java.util.List;

//TODO De momento concatenamos todas las voces - habría que pasarle en el constructor cómo queremos tratar la polifonía
//TODO Test unitario
/**
 * Created by drizo on 18/7/17.
 */
public class DiatonicPitchSequenceFromScoreSongEncoder implements IMidLevelRepresentationEncoder<ScoreSong, DiatonicPitchSequence> {
    @Override
    public DiatonicPitchSequence encode(ScoreSong input) throws IM3Exception {
        ArrayList<AtomPitch> aps = input.getAtomPitches();
        NoteNames [] v = new NoteNames[aps.size()];
        int i=0;
        for (AtomPitch ap: input.getAtomPitches()) {
            v[i++] = ap.getScientificPitch().getPitchClass().getNoteName();
        }

        return new DiatonicPitchSequence("Pitch sequence", v);

    }

    @Override
    public List<DiatonicPitchSequence> encode(ScoreSong input, int windowSize, int windowStep) throws IM3Exception {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
