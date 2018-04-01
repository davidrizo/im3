package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Measure;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

/**
 * If may be a system barline
 */
public class LayoutCoreBarline extends LayoutCoreSymbol<Measure>  {
    private final Coordinate from;
    private final Coordinate to;
    private final Staff staff;
    Line line;
    Time time;

    public LayoutCoreBarline(Staff staff, LayoutFont layoutFont, Measure measure) throws IM3Exception {
        super(layoutFont, measure);
        this.staff = staff;
        this.time = measure.getEndTime();
        //Coordinate from = new Coordinate(position.getX(), layoutStaff.getYAtLine(1));
        //Coordinate to = new Coordinate(position.getX(), layoutStaff.getYAtLine(5));
        from = new Coordinate(position.getX(), null);
        to = new Coordinate(position.getX(), null);

        line = new Line(this, InteractionElementType.barline, from, to);//TODO IDS

    }

    public Staff getStaff() {
        return staff;
    }

    /**
     * Both staves may be the same
     * @param bottomStaff Top staff in the system
     * @param topStaff Top staff in the system
     * @throws IM3Exception
     */
    public void setLayoutStaff(LayoutStaff bottomStaff, LayoutStaff topStaff) throws IM3Exception {
        // TODO: 21/9/17 Sólo vale para pentagramas - debe sobresalir igual con percusión
        from.setReferenceY(bottomStaff.getYAtLine(1));
        to.setReferenceY(topStaff.getYAtLine(5));
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }

    @Override
    protected void doLayout() throws IM3Exception {
    }

    @Override
    public Time getTime() {
        return time;
    }

    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }

    public Coordinate getBottomEnd() throws IM3Exception {
        if (from.getAbsoluteY() < to.getAbsoluteY()) {
            return from;
        } else {
            return to;
        }
    }

    public Coordinate getTopEnd() throws IM3Exception {
        if (from.getAbsoluteY() > to.getAbsoluteY()) {
            return from;
        } else {
            return to;
        }
    }

}
