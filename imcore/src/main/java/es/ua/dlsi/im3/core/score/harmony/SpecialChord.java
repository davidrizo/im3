package es.ua.dlsi.im3.core.score.harmony;

/**
 * Created by drizo on 20/6/17.
 */
public class SpecialChord extends ChordSpecification {
    boolean enharmonicSpelling;
    ChordInversion inversion;


    public SpecialChord() {
        inversion = ChordInversion.root;
    }

    public boolean isEnharmonicSpelling() {
        return enharmonicSpelling;
    }

    public void setEnharmonicSpelling(boolean enharmonicSpelling) {
        this.enharmonicSpelling = enharmonicSpelling;
    }

    public ChordInversion getInversion() {
        return inversion;
    }

    public void setInversion(ChordInversion inversion) {
        this.inversion = inversion;
    }
}
