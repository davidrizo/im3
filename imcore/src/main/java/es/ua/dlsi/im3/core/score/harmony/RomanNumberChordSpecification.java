package es.ua.dlsi.im3.core.score.harmony;

import java.util.Arrays;

/**
 * Created by drizo on 20/6/17.
 */
public class RomanNumberChordSpecification extends ChordSpecification{
    QualifiedDegree root;
    ChordInversion inversion;
    ChordInterval [] extensions;
    ChordRootAlteration alteration;


    public QualifiedDegree getRoot() {
        return root;
    }

    public void setRoot(QualifiedDegree root) {
        this.root = root;
    }

    public ChordInversion getInversion() {
        return inversion;
    }

    public void setInversion(ChordInversion inversion) {
        this.inversion = inversion;
    }

    public ChordInterval[] getExtensions() {
        return extensions;
    }

    public void setExtensions(ChordInterval[] extensions) {
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        //TODO
        /*return "RomanNumberChordSpecification{" +
                "implicit=" + implicit +
                ", root=" + root +
                ", inversion=" + inversion +
                ", extensions=" + Arrays.toString(extensions) +
                '}';*/
        StringBuilder sb = new StringBuilder(root.toString());
        //TODO Inversion
        //TODO Extensions
        return root.toString();
    }

    public ChordRootAlteration getAlteration() {
        return alteration;
    }

    public void setAlteration(ChordRootAlteration alteration) {
        this.alteration = alteration;
    }
}
