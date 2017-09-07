package es.ua.dlsi.im3.midrepresentations.sequences;

public class CoupledNoteRepresentation<PitchType, RhythmType> {
    private final PitchType pitch;
    private final RhythmType rhythmType;

    public CoupledNoteRepresentation(PitchType pitch, RhythmType rhythmType) {
        this.pitch = pitch;
        this.rhythmType = rhythmType;
    }

    public PitchType getPitch() {
        return pitch;
    }

    public RhythmType getRhythmType() {
        return rhythmType;
    }

    @Override
    public String toString() {
        return "(" + pitch + ", " + rhythmType + ")";
    }
}
