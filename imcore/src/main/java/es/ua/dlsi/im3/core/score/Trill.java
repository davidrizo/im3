package es.ua.dlsi.im3.core.score;

public class Trill extends StaffMark {
    SingleFigureAtom singleFigureAtom;

    public Trill(Staff staff, SingleFigureAtom singleFigureAtom) {
        super(staff, singleFigureAtom.getTime());
        this.singleFigureAtom = singleFigureAtom;
    }

}
