package es.ua.dlsi.im3.core.score.io.kern;

//TODO This class should be included in IM3

import es.ua.dlsi.im3.core.score.PositionInStaff;

import java.util.Objects;

/**
 * @autor drizo
 */
public class KernRestPosition {
    PositionInStaff positionInStaff;

    public KernRestPosition(PositionInStaff positionInStaff) {
        this.positionInStaff = positionInStaff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KernRestPosition)) return false;
        KernRestPosition that = (KernRestPosition) o;
        return Objects.equals(positionInStaff, that.positionInStaff);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionInStaff);
    }

    public PositionInStaff getPositionInStaff() {
        return positionInStaff;
    }

    public void setPositionInStaff(PositionInStaff positionInStaff) {
        this.positionInStaff = positionInStaff;
    }

    @Override
    public String toString() {
        return positionInStaff.toString();
    }
}
