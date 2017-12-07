package es.ua.dlsi.im3.mavr.model;

import es.ua.dlsi.im3.core.score.Atom;

import java.util.List;

public class Motive {
    List<Atom> atomList;
    String name;

    public Motive(String name, List<Atom> atomList) {
        this.name = name;
        this.atomList = atomList;
    }

    public List<Atom> getAtomList() {
        return atomList;
    }

    public String getName() {
        return name;
    }
}
