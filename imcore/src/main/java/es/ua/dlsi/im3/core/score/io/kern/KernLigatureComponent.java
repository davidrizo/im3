package es.ua.dlsi.im3.core.score.io.kern;

import es.ua.dlsi.im3.core.score.LigatureType;
import es.ua.dlsi.im3.core.score.SimpleNote;

import java.util.Objects;

/**
 * @autor drizo
 */
public class KernLigatureComponent {
    LigatureStartEnd startEnd;
    LigatureType type;
    SimpleNote simpleNote;

    public KernLigatureComponent(LigatureStartEnd startEnd, LigatureType type, SimpleNote simpleNote) {
        this.startEnd = startEnd;
        this.type = type;
        this.simpleNote = simpleNote;
    }

    public LigatureStartEnd getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(LigatureStartEnd startEnd) {
        this.startEnd = startEnd;
    }

    public LigatureType getType() {
        return type;
    }

    public void setType(LigatureType type) {
        this.type = type;
    }

    public SimpleNote getSimpleNote() {
        return simpleNote;
    }

    public void setSimpleNote(SimpleNote simpleNote) {
        this.simpleNote = simpleNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KernLigatureComponent)) return false;
        KernLigatureComponent that = (KernLigatureComponent) o;
        return startEnd == that.startEnd &&
                type == that.type &&
                Objects.equals(simpleNote, that.simpleNote);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startEnd, type, simpleNote);
    }

    @Override
    public String toString() {
        return "KernLigatureComponent{" +
                "startEnd=" + startEnd +
                ", type=" + type +
                ", simpleNote=" + simpleNote +
                '}';
    }
}
