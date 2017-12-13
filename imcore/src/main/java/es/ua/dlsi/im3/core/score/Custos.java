package es.ua.dlsi.im3.core.score;

public class Custos implements ITimedElementInStaff {
    DiatonicPitch diatonicPitch;
    Staff staff;
    Time time;
    int octave;

    public Custos(Staff staff, Time time, DiatonicPitch diatonicPitch, int octave) {
        this.staff = staff;
        this.time = time;
        this.diatonicPitch = diatonicPitch;
        this.octave = octave;
    }

    public Custos(Staff staff, Time time) {
        this.staff = staff;
        this.time = time;
    }

    @Override
    public Staff getStaff() {
        return staff;
    }

    @Override
    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Override
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
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