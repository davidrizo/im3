package es.ua.dlsi.im3.core.score;

/**
 * Line or space in the staff. Invariant
 * @author drizo
 */
public class PositionInStaff {
    public static final String LINE_STR = "L";
    public static final String SPACE_STR = "S";
    /**
     * 0 for bottom line, -1 for space under bottom line, 1 for space above bottom line
     */
    final int lineSpace;

    public PositionInStaff(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public int getLine() {
        return this.lineSpace / 2+1;
    }

    public int getSpace() {
        return (this.lineSpace-1) / 2+1;
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
        if (lineSpace % 2 == 0) {
            return LINE_STR + getLine();
        } else {
            return SPACE_STR + getSpace();
        }
    }

    public boolean laysOnLine() {
        return lineSpace % 2 == 0;
    }

    /**
     * @param line 1 is bottom line, 2 is the 1st line from bottom, 0 is the bottom ledger line
     * @return 0 for the bottom line, 2 for the 1st line from bottom, -1 for the bottom ledger line, etc.
     */
    public static PositionInStaff fromLine(int line) {
        return new PositionInStaff((line-1)*2);
    }

    /**
     * @param space 1 is bottom space, 2
     */
    public static PositionInStaff fromSpace(int space) {
        return new PositionInStaff((space)*2-1);
    }

    /**
     * Create a new PositionInStaff with the difference
     * @param lineSpaceDifference
     * @return
     */
    public PositionInStaff move(int lineSpaceDifference) {
        return new PositionInStaff(lineSpace + lineSpaceDifference);
    }
}
