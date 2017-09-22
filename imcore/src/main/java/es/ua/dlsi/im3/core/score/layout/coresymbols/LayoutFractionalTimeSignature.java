package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.TimeSignatureNumber;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;
import es.ua.dlsi.im3.core.score.meters.FractionalTimeSignature;

public class LayoutFractionalTimeSignature extends LayoutTimeSignature<FractionalTimeSignature> {
    TimeSignatureNumber numerator;
    TimeSignatureNumber denominator;
    Group group;

    public LayoutFractionalTimeSignature(LayoutStaff layoutStaff, FractionalTimeSignature coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);
        if (layoutStaff.getLines().size() != 5) {
            throw new IM3Exception("TO-DO: unimplemented non pentagrams"); // TODO: 20/9/17 TODO Que se calcule sin ir a la línea
        }
        group = new Group();

        //TODO Igual que Barline - que valga para percusión
        Coordinate numeratorPosition = new Coordinate(position.getX(), layoutStaff.getYAtLine(4));
        numerator = new TimeSignatureNumber(layoutStaff.getScoreLayout().getLayoutFont(), this, coreSymbol.getNumerator(), numeratorPosition);
        addComponent(numerator);

        Coordinate denominatorPosition  = new Coordinate(position.getX(), layoutStaff.getYAtLine(2));
        denominator = new TimeSignatureNumber(layoutStaff.getScoreLayout().getLayoutFont(), this, coreSymbol.getDenominator(), denominatorPosition);
        addComponent(denominator);
    }

    private void addComponent(TimeSignatureNumber tsn) {
        group.add(tsn.getGraphics());
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }
}
