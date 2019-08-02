package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefPercussion;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Component;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Pictogram;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LayoutCoreLigature extends LayoutCoreSymbolInStaff<Ligature> {
    Group group;
    List<LayoutCoreSingleFigureAtom> layoutElements;

    public LayoutCoreLigature(LayoutFont layoutFont, Ligature ligature) throws IM3Exception {
        super(layoutFont, ligature);

        build();
    }

    private void build() throws IM3Exception {
        group = new Group(this, InteractionElementType.ligature);
        layoutElements = new ArrayList<>();

        position.setDisplacementX(200);

        // TODO recta - obliqua
        for (Atom atom: coreSymbol.getAtoms()) {
            if (!(atom instanceof SimpleNote)) {
                throw new IM3Exception("Unsupported layout of ligatures made of other thing than simple notes");
            }

            SimpleNote note = (SimpleNote) atom;
            LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom = new LayoutCoreSingleFigureAtom(layoutFont, note);
            layoutCoreSingleFigureAtom.getPosition().setReferenceX(position.getX());

            layoutElements.add(layoutCoreSingleFigureAtom);
            group.add(layoutCoreSingleFigureAtom.getGraphics());

        }
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        double actualX = 0 ;
        for (LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom: layoutElements) {
            layoutCoreSingleFigureAtom.setX(actualX);
            try {
                actualX += layoutCoreSingleFigureAtom.getWidth();
            } catch (IM3Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot compute width of ligature element", e);
                throw new IM3RuntimeException(e);
            }
        }
    }

    @Override
    public void rebuild() throws IM3Exception {
        for (LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom: layoutElements) {
            layoutCoreSingleFigureAtom.rebuild();
        }
    }

    @Override
    public void setLayoutStaff(LayoutStaff layoutStaff) throws IM3Exception {
        super.setLayoutStaff(layoutStaff);
        for (LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom: layoutElements) {
            layoutCoreSingleFigureAtom.setLayoutStaff(layoutStaff);
        }
    }

    @Override
    protected void doLayout() throws IM3Exception {
        for (LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom: layoutElements) {
            layoutCoreSingleFigureAtom.doLayout();
        }
    }

    @Override
    public GraphicsElement getGraphics() {
       return group;
    }

}
