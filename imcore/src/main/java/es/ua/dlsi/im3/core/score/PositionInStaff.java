package es.ua.dlsi.im3.core.score;

/**
 * Line or space in the staff.
 * @author drizo
 */
public class PositionInStaff {
    /**
     * 0 for bottom line, -1 for space under bottom line, 1 for space above bottom line
     */
    int lineSpace;

    public PositionInStaff(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    // TODO: Test unitario
    public int getLine() {
        return this.lineSpace / 2;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionInStaff that = (PositionInStaff) o;

        return lineSpace == that.lineSpace;
    }

    @Override
    public int hashCode() {
        return lineSpace;
    }

    @Override
    public String toString() {
        return "PositionInStaff{" +
                "lineSpace=" + lineSpace +
                '}';
    }

    public boolean laysOnLine() {
        return lineSpace % 2 == 0;
    }
}
