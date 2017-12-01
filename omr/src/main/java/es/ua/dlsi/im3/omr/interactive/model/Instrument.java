package es.ua.dlsi.im3.omr.interactive.model;

/**
 * Instrument or voice
 */
public class Instrument implements Comparable<Instrument> {
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
}
