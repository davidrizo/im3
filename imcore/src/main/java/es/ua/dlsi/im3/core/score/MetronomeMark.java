package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.Objects;

//TODO Not used yet, just imported from **mens

/**
 * A section marker
 * @autor drizo
 */
public class MetronomeMark implements ITimedElement {
    Time time;
    int tempo;

    public MetronomeMark(int tempo) {
        this.tempo = tempo;
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

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetronomeMark)) return false;
        MetronomeMark that = (MetronomeMark) o;
        return tempo == that.tempo &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {

        return Objects.hash(time, tempo);
    }

    @Override
    public String toString() {
        return "tempo=" + tempo;
    }
}
