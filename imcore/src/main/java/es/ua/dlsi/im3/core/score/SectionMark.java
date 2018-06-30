package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.Objects;

//TODO Not used yet, just imported from **mens
/**
 * A section marker
 * @autor drizo
 */
public class SectionMark implements ITimedElement {
    Time time;
    String label;

    public SectionMark(String label) {
        this.label = label;
    }

    @Override
    public Time getTime() {
        return time;
    }

    @Override
    public void move(Time offset) {
        if (time == null) {
            time = offset;
        } else {
            time = time.add(offset);
        }
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectionMark)) return false;
        SectionMark that = (SectionMark) o;
        return Objects.equals(time, that.time) &&
                Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {

        return Objects.hash(time, label);
    }

    @Override
    public String toString() {
        return label;
    }
}
