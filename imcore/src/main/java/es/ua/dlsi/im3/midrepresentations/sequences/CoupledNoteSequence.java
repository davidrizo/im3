package es.ua.dlsi.im3.midrepresentations.sequences;

import java.util.List;

public class CoupledNoteSequence<PitchType, RhythmType> extends Sequence<CoupledNoteRepresentation<PitchType, RhythmType>> {
    public CoupledNoteSequence(String name, CoupledNoteRepresentation<PitchType, RhythmType>[] seq) {
        super(name, seq);
    }

    public CoupledNoteSequence(String name, List<CoupledNoteRepresentation<PitchType, RhythmType>> seq) {
        super(name, seq);
    }

    @Override
    public String toString() {
        return sequence.toString();
    }
}
