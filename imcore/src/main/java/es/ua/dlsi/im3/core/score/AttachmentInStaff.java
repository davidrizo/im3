package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * Attachments that must be positioned in the staff (e.g. displaced dots)
 */
public class AttachmentInStaff<CoreSymbolType extends ITimedElementInStaff> extends Attachment<CoreSymbolType> implements ITimedElementInStaff {
    Time time;
    Staff staff;
    String facsimileElementID;
    String ID;

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
    public void move(Time offset) {
        this.time = time.add(offset);
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
        this.ID = ID;
    }

    @Override
    public String __getIDPrefix() {
        return "attachment";
    }
}
