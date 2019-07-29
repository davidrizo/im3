package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.Objects;

public class Custos implements ITimedElementInStaff, ITimedElementWithSetter, IStaffElementWithoutLayer {
    ScientificPitch scientificPitch;
    Staff staff;
    Time time;

    public Custos(Staff staff, Time time, ScientificPitch scientificPitch) {
        this.staff = staff;
        this.time = time;
    }

    public Custos(ScientificPitch scientificPitch) {
        this.scientificPitch = scientificPitch;
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

    @Override
    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public void move(Time offset) throws IM3Exception {
        Staff prevStaff = staff;
        staff.remove(this);
        if (time == null) {
            this.time = offset;
        } else {
            this.time = time.add(offset);
        }

        prevStaff.addTimedElementInStaff(this);
    }

    public ScientificPitch getScientificPitch() {
        return scientificPitch;
    }

    public void setScientificPitch(ScientificPitch scientificPitch) {
        this.scientificPitch = scientificPitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Custos custos = (Custos) o;
        return Objects.equals(scientificPitch, custos.scientificPitch) &&
                Objects.equals(staff, custos.staff) &&
                Objects.equals(time, custos.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scientificPitch, staff, time);
    }
}
