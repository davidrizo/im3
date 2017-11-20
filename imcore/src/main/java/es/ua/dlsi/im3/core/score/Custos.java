package es.ua.dlsi.im3.core.score;

public class Custos extends StaffMark {
    DiatonicPitch diatonicPitch;
    int octave;

    public Custos(Staff staff, Time time, DiatonicPitch diatonicPitch, int octave) {
        super(staff, time);
        this.diatonicPitch = diatonicPitch;
        this.octave = octave;
    }

    public Custos(Staff staff, Time time) {
        super(staff, time);
    }

    public DiatonicPitch getDiatonicPitch() {
        return diatonicPitch;
    }

    public int getOctave() {
        return octave;
    }

    public void setDiatonicPitch(DiatonicPitch diatonicPitch) {
        this.diatonicPitch = diatonicPitch;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    @Override
    public String toString() {
        return "Custos{" +
                "diatonicPitch=" + diatonicPitch +
                ", octave=" + octave +
                '}';
    }
}
