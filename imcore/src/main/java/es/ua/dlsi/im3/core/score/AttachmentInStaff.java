package es.ua.dlsi.im3.core.score;

/**
 * Attachments that must be positioned in the staff (e.g. displaced dots)
 */
public class AttachmentInStaff<CoreSymbolType extends ITimedElementInStaff> extends Attachment<CoreSymbolType> implements ITimedElementInStaff {
    Time time;
    Staff staff;

    public AttachmentInStaff(Staff staff, Time time, CoreSymbolType coreSymbol) {
        super(coreSymbol);
        this.time = time;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public Staff getStaff() {
        return staff;
    }

    @Override
    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
