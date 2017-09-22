package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.mensural.meters.hispanic.TimeSignatureProporcionMenor;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;

import java.util.HashMap;

public class LayoutSimpleRest extends LayoutSymbolWithDuration<SimpleRest> {
    private final Pictogram pictogram;
    SimpleRest rest;

    private static final HashMap<Figures, String> UNICODES = new HashMap<>();
    {
        UNICODES.put(Figures.DOUBLE_WHOLE, "restDoubleWhole");
        UNICODES.put(Figures.WHOLE, "restWhole");
        UNICODES.put(Figures.HALF, "restHalf");
        UNICODES.put(Figures.QUARTER, "restQuarter");
        UNICODES.put(Figures.EIGHTH, "rest8th");
        UNICODES.put(Figures.SIXTEENTH, "rest16th");
        UNICODES.put(Figures.THIRTY_SECOND, "rest32nd");
        UNICODES.put(Figures.SIXTY_FOURTH, "rest64th");
        UNICODES.put(Figures.HUNDRED_TWENTY_EIGHTH, "rest128th");
        UNICODES.put(Figures.TWO_HUNDRED_FIFTY_SIX, "rest256th");
        // TODO Existen hasta la 1024th

        //TODO Mensural
        UNICODES.put(Figures.MINIM, "mensuralRestMinima");
    }

    

    public LayoutSimpleRest(LayoutStaff layoutStaff, SimpleRest coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);
        Coordinate restPosition = new Coordinate(
            position.getX(),
            layoutStaff.getYAtCenterLine()
        );
        pictogram = new Pictogram("REST-", layoutStaff.getScoreLayout().getLayoutFont(), getUnicode(), restPosition);//TODO IDS
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }

    @Override
    public Time getDuration() {
        return coreSymbol.getDuration();
    }

    private String getUnicode() throws IM3Exception {
        String unicode = UNICODES.get(coreSymbol.getAtomFigure().getFigure());
        if (unicode == null) {
            throw new IM3Exception("Cannot find a font unicode for " + coreSymbol.getAtomFigure().getFigure());
        }
        return unicode;
    }

}
