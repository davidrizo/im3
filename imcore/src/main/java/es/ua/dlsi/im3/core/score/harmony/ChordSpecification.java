package es.ua.dlsi.im3.core.score.harmony;

/**
 * Created by drizo on 20/6/17.
 */
public class ChordSpecification {
    boolean implicit;

    public boolean isImplicit() {
        return implicit;
    }

    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }


    @Override
    public String toString() {
        return "ChordSpecification{" +
                "implicit=" + implicit +
                '}';
    }


}
