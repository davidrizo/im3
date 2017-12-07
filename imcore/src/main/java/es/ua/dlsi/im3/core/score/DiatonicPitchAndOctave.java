package es.ua.dlsi.im3.core.score;

import java.util.Objects;

/**
 * Used just by staff
 */
class DiatonicPitchAndOctave implements Comparable<DiatonicPitchAndOctave> {
    DiatonicPitch dp;
    int octave;

    public DiatonicPitchAndOctave(DiatonicPitch dp, int octave) {
        this.dp = dp;
        this.octave = octave;
    }


    @Override
    public int compareTo(DiatonicPitchAndOctave o) {
        int diff = dp.compareTo(o.dp);
        if (diff == 0) {
            diff = octave - o.octave;
        }
        return diff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiatonicPitchAndOctave that = (DiatonicPitchAndOctave) o;
        return octave == that.octave &&
                dp == that.dp;
    }

    @Override
    public int hashCode() {

        return Objects.hash(dp, octave);
    }
}
