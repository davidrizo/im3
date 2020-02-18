package es.ua.dlsi.im3.core.score.layout.coresymbols.components;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.HashMap;

public class Flag extends Component<LayoutCoreSingleFigureAtom> {
    Pictogram pictogram;
    private final Figures figure;
    private final boolean stemUp;
    private final String codepoint;

    public Flag(LayoutFont layoutFont, LayoutCoreSingleFigureAtom parent, Figures figure, Coordinate position, boolean stemUp) throws IM3Exception {
        super(null, parent, position);

        this.stemUp = stemUp;
        this.figure = figure;

        if (figure.usesCombinedStemAndFlag()) {
            codepoint = layoutFont.getFontMap().getUnicodeStemAndFlag(figure, stemUp);
        } else {
            codepoint = layoutFont.getFontMap().getUnicodeFlag(figure, stemUp);
        }
        if (codepoint == null) {
            throw new IM3Exception("Cannot find a codepoint for flag of figure " + figure + " and stem up?" + stemUp + " using combined stem and flag?" + figure.usesCombinedStemAndFlag());
        }
        pictogram = new Pictogram(this, InteractionElementType.flag, layoutFont, codepoint, position);//TODO IDS
    }

    @Override
    public GraphicsElement getGraphics() {
        return pictogram;
    }

    @Override
    protected void doLayout() {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }

    @Override
    public String toString() {
        return "Flag for figure " + figure + " and stemUp? " + stemUp + " with codepoint " + codepoint;
    }
}
