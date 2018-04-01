package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.BeamGroup;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

import java.util.List;

public class LayoutBeamGroup extends NotationSymbol {
    private final LayoutFont layoutFont;
    BeamGroup beamGroup;
    List<LayoutCoreSingleFigureAtom> layoutCoreSingleFigureAtoms;
    Group group;

    public LayoutBeamGroup(BeamGroup beamedGroup, List<LayoutCoreSingleFigureAtom> layoutCoreSingleFigureAtoms, LayoutFont layoutFont) {
        this.beamGroup = beamedGroup;
        this.layoutCoreSingleFigureAtoms = layoutCoreSingleFigureAtoms;
        this.layoutFont = layoutFont;
    }

    /**
     * Create the beams between the notes
     */
    public void createBeams() throws IM3Exception {
        group = new Group(null, InteractionElementType.beams);
        // TODO: 1/10/17 Importante !!! No funciona con grupos de varias duraciones
        Integer flags = null;
        for (LayoutCoreSymbol coreSymbol: this.layoutCoreSingleFigureAtoms) {
            if (!(coreSymbol instanceof LayoutCoreSingleFigureAtom)) {
                throw new IM3Exception("Unsupported groups of non LayoutCoreSingleFigureAtom"); // FIXME: 1/10/17 Rests, different durations
            }
            LayoutCoreSingleFigureAtom lcoreSingleFigureAtom = ((LayoutCoreSingleFigureAtom)coreSymbol);
            int thisSymbolFlags = lcoreSingleFigureAtom.getCoreSymbol().getAtomFigure().getFigure().getNumFlags();
            if (flags != null) {
                if (flags != thisSymbolFlags) {
                    throw new IM3Exception("Unsupported mixed figure beams, there are figures with number of flags: " + flags + " and " + thisSymbolFlags);
                }
            } else {
                flags = thisSymbolFlags;
            }
            lcoreSingleFigureAtom.removeFlag();
        }

        if (this.layoutCoreSingleFigureAtoms.size() < 2) {
            throw new IM3Exception("Cannot create beams with less than 2 elements, there are: " + this.layoutCoreSingleFigureAtoms.size()); // TODO: 1/10/17 Broken beam of 1 note
        }
        LayoutCoreSingleFigureAtom from = layoutCoreSingleFigureAtoms.get(0);
        LayoutCoreSingleFigureAtom to = layoutCoreSingleFigureAtoms.get(this.layoutCoreSingleFigureAtoms.size()-1);

        // TODO: 1/10/17 En lugar de line un polígono
        double displacementY = 0;
        for (int i=0; i<flags; i++) {
            Coordinate fromPosition = new Coordinate(
                    new CoordinateComponent(from.getStemEnd().getX()),
                    new CoordinateComponent(from.getStemEnd().getY(), displacementY));

            Coordinate toPosition = new Coordinate(
                    new CoordinateComponent(to.getStemEnd().getX()),
                    new CoordinateComponent(to.getStemEnd().getY(), displacementY));

            GraphicsElement beam = layoutFont.getFontMap().createBeam(this, fromPosition, toPosition); // TODO ID
            group.add(beam);
            displacementY += LayoutConstants.BEAM_SEPARATION; // FIXME: 1/10/17 Dirección según plica - extender plica
        }

    }

    @Override
    public Group getGraphics() {
        if (group == null) {
            throw new IM3RuntimeException("createBeams not invoked");
        }
        return group;
    }

    @Override
    protected void doLayout() throws IM3Exception {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
