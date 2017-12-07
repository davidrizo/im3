package es.ua.dlsi.im3.core.score;

public class Trill extends StaffMark {
    private final PositionAboveBelow position;
    SingleFigureAtom singleFigureAtom;

    public Trill(Staff staff, PositionAboveBelow positionAboveBelow, SingleFigureAtom singleFigureAtom) {
        super(staff, singleFigureAtom.getTime());
        this.position = positionAboveBelow;
        this.singleFigureAtom = singleFigureAtom;
    }

    public PositionAboveBelow getPosition() {
        return position;
    }

    public SingleFigureAtom getSingleFigureAtom() {
        return singleFigureAtom;
    }
}
