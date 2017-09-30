package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.HashMap;

public class LayoutCoreSimpleRest extends LayoutCoreSymbolWithDuration<SimpleRest> {
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
        UNICODES.put(Figures.LONGA, "mensuralRestLonga");
        UNICODES.put(Figures.MAXIMA, "mensuralRestMaxima");
        UNICODES.put(Figures.BREVE, "mensuralRestBrevis");
        UNICODES.put(Figures.SEMIBREVE, "mensuralRestSemibrevis");
        UNICODES.put(Figures.MINIM, "mensuralRestMinima");
        UNICODES.put(Figures.SEMIMINIM, "mensuralRestSemiminima");
        UNICODES.put(Figures.FUSA, "mensuralRestFusa");
    }

    

    public LayoutCoreSimpleRest(LayoutFont layoutFont, SimpleRest coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
        Coordinate restPosition = new Coordinate(
            position.getX(),
            null
        );
        pictogram = new Pictogram("REST-", layoutFont, getUnicode(), restPosition);//TODO IDS
    }

    @Override
    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        super.setLayoutStaff(layoutStaff);
        position.setReferenceY(layoutStaff.getYAtCenterLine());
        pictogram.getPosition().setReferenceY(position.getY());
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
