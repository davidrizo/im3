package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.TimeSignatureNumber;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

public class LayoutCoreFractionalTimeSignature extends LayoutCoreTimeSignature<FractionalTimeSignature> {
    TimeSignatureNumber numerator;
    TimeSignatureNumber denominator;
    Group group;

    public LayoutCoreFractionalTimeSignature(LayoutFont layoutFont, FractionalTimeSignature coreSymbol) throws IM3Exception {
        super(layoutFont, coreSymbol);
        group = new Group(InteractionElementType.fractionalTimeSignature);

        //TODO Igual que Barline - que valga para percusión
        Coordinate numeratorPosition = new Coordinate(position.getX(), null);
        numerator = new TimeSignatureNumber(layoutFont, this, coreSymbol.getNumerator(), numeratorPosition);
        addComponent(numerator);

        Coordinate denominatorPosition  = new Coordinate(position.getX(), null);
        denominator = new TimeSignatureNumber(layoutFont, this, coreSymbol.getDenominator(), denominatorPosition);
        addComponent(denominator);
    }

    private void addComponent(TimeSignatureNumber tsn) {
        group.add(tsn.getGraphics());
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    @Override
    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        super.setLayoutStaff(layoutStaff);
        if (layoutStaff.getLines().size() != 5) {
            throw new IM3Exception("TO-DO: unimplemented non pentagrams"); // TODO: 20/9/17 TODO Que se calcule sin ir a la línea
        }

        numerator.getPosition().setReferenceY(layoutStaff.getYAtLine(4));
        denominator.getPosition().setReferenceY(layoutStaff.getYAtLine(2));
    }

}
