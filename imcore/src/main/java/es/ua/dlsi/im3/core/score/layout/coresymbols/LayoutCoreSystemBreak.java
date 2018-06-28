package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.SystemBreak;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

/**
 * It draws a graphic to show where is the system break. By default it is hidden
 */
public class LayoutCoreSystemBreak extends LayoutCoreSymbol<SystemBreak> {
    private Time DEFAULT_TIME_FOR_SPACE_COMPUTING = new Time(1,1000); //TODO
    private final Coordinate from;
    private final Coordinate to;
    private Line line;

    public LayoutCoreSystemBreak(LayoutFont layoutFont, SystemBreak coreSymbol) {
        super(layoutFont, coreSymbol);
        this.line = line;
        from = new Coordinate(position.getX(), null);
        to = new Coordinate(position.getX(), null);

        line = new Line(this, InteractionElementType.systemBreak, from, to);
        line.setHidden(true);
    }

    /**
     * Both staves may be the same
     * @param topStaff Top staff in the system
     * @param bottomStaff Top staff in the system
     * @throws IM3Exception
     */
    public void setLayoutStaff(LayoutStaff topStaff, LayoutStaff bottomStaff) throws IM3Exception {
        // TODO: 21/9/17 Sólo vale para pentagramas - debe sobresalir igual con percusión
        from.setDisplacementY(-LayoutConstants.SPACE_HEIGHT*2);
        from.setReferenceY(bottomStaff.getYAtLine(0));
        to.setReferenceY(topStaff.getYAtLine(6));
        to.setDisplacementY(LayoutConstants.SPACE_HEIGHT*2); //TODO pongo esto para que se vea - hay que poner otro glifo como un intro
    }

    @Override
    public GraphicsElement getGraphics() {
        return line;
    }

    /**
     * Add a default duration to be able to fit the clef and key signature in the new line
     */
    public Time getDuration() {
        return DEFAULT_TIME_FOR_SPACE_COMPUTING; //TODO Quitar con esto consigo que no se ponga margen
    }

    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }
    @Override
    protected void doLayout() throws IM3Exception {
    }
}
