package es.ua.dlsi.im3.core.score;


/**
 * @deprecated Use system beginning
 * An explicit system break
 */
public class StaffBreak implements ITimedElementInStaff {
    Staff staff;
    String ID;
    String facsimileElementID;

    private Time time;
    /**
     * If it is explicit
     */
    private final boolean manual;

    public StaffBreak(Time time, boolean manual) {
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

    @Override
    public String getFacsimileElementID() {
        return facsimileElementID;
    }

    @Override
    public void setFacsimileElementID(String facsimileElementID) {
        this.facsimileElementID = facsimileElementID;
    }


    @Override
    public String __getID() {
        return ID;
    }

    @Override
    public void __setID(String id) {
        this.ID = id;
    }

    @Override
    public String __getIDPrefix() {
        return "systembreak";
    }

}
