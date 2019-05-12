package es.ua.dlsi.im3.core.score;

/**
 * An explicit system break applied just to a part.
 */
public class PartSystemBreak implements ITimedElementInStaff, IFacsimile {
    String ID;
    String facsimileElementID;
    Staff staff;

    private Time time;
    /**
     * If it is explicit
     */
    private final boolean manual;

    public PartSystemBreak(Time time, boolean manual) {
        this.time = time;
        this.manual = manual;
    }

    @Override
    public Time getTime() {
        return time;
    }

    @Override
    public void move(Time offset) {
        this.time = time.add(offset);
    }

    public boolean isManual() {
        return manual;
    }

    @Override
    public Staff getStaff() {
        return staff;
    }

    @Override
    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFacsimileElementID() {
        return facsimileElementID;
    }

    public void setFacsimileElementID(String facsimileElementID) {
        this.facsimileElementID = facsimileElementID;
    }
}
