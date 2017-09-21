package es.ua.dlsi.im3.core.score.layout.coresymbols;

import com.sun.tools.corba.se.idl.constExpr.Not;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.AtomPitch;
import es.ua.dlsi.im3.core.score.SingleFigureAtom;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.LayoutConstants;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Dot;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.LayoutSymbolWithDuration;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NoteHead;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Stem;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;

public class LayoutSingleFigureAtom extends LayoutSymbolWithDuration<SingleFigureAtom> {
    Group group;
    ArrayList<NoteHead> heads;
    Stem stem;
    ArrayList<Dot> dots;

    public LayoutSingleFigureAtom(LayoutStaff layoutStaff, SingleFigureAtom coreSymbol) throws IM3Exception {
        super(layoutStaff, coreSymbol);
        group = new Group();

        heads = new ArrayList<>();

        if (coreSymbol.getAtomFigure().getFigure().usesStem()) {
            stem = new Stem(this, position, false); // TODO: 21/9/17 stem up or down
            group.add(stem.getGraphics());
        }

        for (AtomPitch atomPitch: coreSymbol.getAtomPitches()) {
            NoteHead noteHead = new NoteHead(layoutStaff.getScoreLayout().getLayoutFont(), this, position);
            group.add(noteHead.getGraphics());
        }

        // TODO: 21/9/17 Flags

        if (coreSymbol.getAtomFigure().getDots() > 0) {
            dots = new ArrayList<>();
            for (int d = 0; d<coreSymbol.getAtomFigure().getDots(); d++ ) {
                CoordinateComponent dotX = new CoordinateComponent(position.getX(), LayoutConstants.DOT_SEPARATION);
                // TODO: 21/9/17 Cuando el puntillo caiga en una línea se suba al espacio, ponerlo en yDisplacement
                double yDisplacement = 0;
                CoordinateComponent dotY = new CoordinateComponent(position.getY(), yDisplacement);

                Coordinate dotPosition = new Coordinate(dotX, dotY);
                Dot dot = new Dot(layoutStaff.getScoreLayout().getLayoutFont(), this, new Coordinate(dotX, dotY));
                dots.add(dot);
                group.add(dot.getGraphics());
            }
        }

    }
    // TODO: 21/9/17 FLAGS....

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    @Override
    public Time getDuration() {
        return coreSymbol.getDuration();
    }

}
