package es.ua.dlsi.im3.core.score;


//TODO Currently we only support all recta and all obliqua
public class Ligature extends CompoundAtom {
    LigatureType ligatureType;

    public Ligature(LigatureType ligatureType) {
        this.ligatureType = ligatureType;
    }

    public LigatureType getLigatureType() {
        return ligatureType;
    }
}
