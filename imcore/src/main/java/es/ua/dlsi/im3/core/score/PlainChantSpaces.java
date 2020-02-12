package es.ua.dlsi.im3.core.score;


/**
 * Spaces created for filling the plain chant from other voice
 */
public class PlainChantSpaces extends CompoundAtom {
    PlainChant plainChant;

    //TODO ¿Qué pasa cuando cambia el plainChant?
    public PlainChantSpaces(PlainChant plainChant) {
        this.plainChant = plainChant;
        for (Atom atom: plainChant.getAtoms()) {
            for (AtomFigure atomFigure: atom.getAtomFigures()) {
                Space space = new Space(atomFigure.getTime(), atomFigure.getFigure(), atomFigure.getDots());
                this.addSubatom(space);
            }
        }
    }
}
