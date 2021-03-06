package es.ua.dlsi.im3.core.score;

/**
 * When dot is a symbol that must be treated independtly of the figure (e.g. when a barline is located between the note
 * and the dot
 */
public class DisplacedDot extends AttachmentInStaff<AtomPitch> implements ITimedElementInStaff {
    public DisplacedDot(Time time, AtomPitch coreSymbol) {
        super(coreSymbol.getStaff(), time, coreSymbol);
    }

    @Override
    public Staff getStaff() {
        return this.attachedTo.getStaff();
    }

    @Override
    public void setStaff(Staff staff) {
        // TODO: 28/10/17 We should change the staff of the atom pitch  
    }
}
