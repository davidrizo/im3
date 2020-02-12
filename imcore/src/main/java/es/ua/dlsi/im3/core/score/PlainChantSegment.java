package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.HashMap;

/**
 * Segment where only a plain chant in one voice and spaces for it in other ones is found
 */
public class PlainChantSegment implements ITimedElement {
    PlainChant plainChant;
    HashMap<Staff, PlainChantSpaces> plainChantSpaces;

    public PlainChantSegment(PlainChant plainChant) {
        this.plainChant = plainChant;
        this.plainChantSpaces = new HashMap<>();
    }

    @Override
    public Time getTime() {
        return plainChant.getTime();
    }

    @Override
    public void move(Time offset) throws IM3Exception {
        plainChant.move(offset);
        plainChantSpaces.values().forEach(plainChantSpaces1 -> plainChantSpaces1.move(offset));
    }

    public PlainChant getPlainChant() {
        return plainChant;
    }

    public HashMap<Staff, PlainChantSpaces> getPlainChantSpaces() {
        return plainChantSpaces;
    }

    public void addPlainChantSpaces(Staff staff, PlainChantSpaces plainChantSpaces) {
        this.plainChantSpaces.put(staff, plainChantSpaces);
    }
}
