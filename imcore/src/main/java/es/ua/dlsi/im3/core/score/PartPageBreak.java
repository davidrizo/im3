package es.ua.dlsi.im3.core.score;


/**
 * An explicit page break. In MEI it is referred as a page beginning
 */
public class PartPageBreak implements ITimedElementInStaff, IFacsimile {
    private Time time;
    String ID;
    String facsimileElementID;

    /**
     * If it is explicit
     */
    private final boolean manual;
    private Staff staff;

    public PartPageBreak(Time time, boolean manual) {
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
