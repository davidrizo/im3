package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.DisplacedDot;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

/**
 * Displaced dots (usually for manuscripts). Constructed from NotePitch
 */
public class LayoutCoreDisplacedDot extends LayoutAttachmentInStaff<DisplacedDot, NotePitch> {
    Pictogram pictogram;
    NotePitch notePitch;

    public LayoutCoreDisplacedDot(LayoutFont layoutFont, DisplacedDot coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
        //TODO ¿Este codepoint igual que un puntillo normal?
        pictogram = new Pictogram(this, InteractionElementType.dot, layoutFont, "augmentationDot", position);//TODO IDS
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }

    @Override
    public void rebuild() {
        throw new UnsupportedOperationException("TO-DO Rebuild " + this.getClass().getName());
    }
    @Override
    protected void doLayout() {
        position.setReferenceY(attachedToView.getDotsYCoordinate());
    }
}
