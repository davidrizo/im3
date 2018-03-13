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
        pictogram = new Pictogram(InteractionElementType.dot, layoutFont, "augmentationDot", position);//TODO IDS
    }

    @Override
    public void setAttachedToView(NotePitch attachedToView) throws IM3Exception {
        super.setAttachedToView(attachedToView);
        position.setReferenceY(attachedToView.getDotsYCoordinate());
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }
}
