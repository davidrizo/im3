package es.ua.dlsi.im3.core.score;


/**
 * An explicit page break. In MEI it is referred as a page beginning
 */
public class PageBeginning implements ITimedElement, IFacsimile {
    private Time time;
    String ID;
    String facsimileElementID;

    /**
     * If it is explicit
     */
    private final boolean manual;

    public PageBeginning(Time time, boolean manual) {
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
        return "PB";
    }
}
