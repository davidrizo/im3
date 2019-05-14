package es.ua.dlsi.im3.core.score;

/**
 * An explicit system beginning
 */
public class SystemBeginning implements ITimedElement, IFacsimile {
    String ID;
    String facsimileElementID;

    private Time time;
    /**
     * If it is explicit
     */
    private final boolean manual;

    public SystemBeginning(Time time, boolean manual) {
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

    public String getFacsimileElementID() {
        return facsimileElementID;
    }

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
        return "SB";
    }
}
