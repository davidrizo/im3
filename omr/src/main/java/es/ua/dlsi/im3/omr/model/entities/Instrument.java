package es.ua.dlsi.im3.omr.model.entities;

import java.util.Objects;

/**
 * Instrument or voice
 */
public class Instrument implements Comparable<Instrument> {
    /**
     * Unique name
     */
    String name;

    public Instrument(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Instrument o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instrument that = (Instrument) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
