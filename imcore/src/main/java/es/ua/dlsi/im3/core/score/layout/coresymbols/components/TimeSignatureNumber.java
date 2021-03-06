package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreTimeSignature;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

public class TimeSignatureNumber extends Component<LayoutCoreTimeSignature> {
    Pictogram pictogram;
    int number;

    public TimeSignatureNumber(LayoutFont layoutFont, LayoutCoreTimeSignature parent, int number, Coordinate position) throws IM3Exception {
        super(null, parent, position);
        this.number = number;
        pictogram = new Pictogram(this, InteractionElementType.timeSignatureNumber,layoutFont, "timeSig" + number, position);//TODO IDS
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }
    @Override
    protected void doLayout() throws IM3Exception {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
