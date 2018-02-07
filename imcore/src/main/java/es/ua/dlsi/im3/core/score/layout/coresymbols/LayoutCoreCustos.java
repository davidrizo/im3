package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Custos;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

public class LayoutCoreCustos extends LayoutCoreSymbolInStaff<Custos> {
    private final Pictogram pictogram;

    public LayoutCoreCustos(LayoutFont layoutFont, Custos custos) throws IM3Exception {
        super(layoutFont, custos);

        //position.setY(layoutStaff.getYAtLine(coreSymbol.getLine()));
        pictogram = new Pictogram("CUSTOS-", layoutFont, layoutFont.getFontMap().getCustosCodePoint(), position);//TODO IDS
    }

    @Override
    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        super.setLayoutStaff(layoutStaff);
        position.setReferenceY(
                layoutStaff.computeYPositionForPitchWithoutClefOctaveChange(coreSymbol.getTime(), coreSymbol.getDiatonicPitch(), coreSymbol.getOctave()));
    }


    @Override
    public GraphicsElement getGraphics() {
       return pictogram;
    }
}
