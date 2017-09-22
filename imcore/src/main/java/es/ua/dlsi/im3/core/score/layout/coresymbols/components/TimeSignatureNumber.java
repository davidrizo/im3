package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutTimeSignature;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

public class TimeSignatureNumber extends Component<LayoutTimeSignature> {
    Pictogram pictogram;
    int number;

    public TimeSignatureNumber(LayoutFont layoutFont, LayoutTimeSignature parent, int number, Coordinate position) throws IM3Exception {
        super(parent, position);
        this.number = number;
        pictogram = new Pictogram("TS-NUM-",layoutFont, "timeSig" + number, position);//TODO IDS
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }

}
