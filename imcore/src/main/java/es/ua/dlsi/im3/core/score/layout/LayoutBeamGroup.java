package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.BeamGroup;
import es.ua.dlsi.im3.core.score.StemDirection;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSingleFigureAtom;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCoreSymbolWithDuration;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.NotePitch;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.LinkedList;
import java.util.List;

public class LayoutBeamGroup extends NotationSymbol {
    private final LayoutFont layoutFont;
    BeamGroup beamGroup;
    List<LayoutCoreSymbolWithDuration<?>> layoutCoreSymbols;
    Group group;
    private double angle;
    private double thickness;

    public LayoutBeamGroup(BeamGroup beamedGroup, List<LayoutCoreSymbolWithDuration<?>> layoutCoreSingleFigureAtoms, LayoutFont layoutFont) {
        this.beamGroup = beamedGroup;
        this.layoutCoreSymbols = layoutCoreSingleFigureAtoms;
        this.layoutFont = layoutFont;
    }

    /*class _StemDrawingInfo {
        double height;
        boolean stemUp;
        double referenceYHeadPossitionForStem;
        double minY;
        double maxY;
        LayoutCoreSymbolWithDuration<?> noteWithMinY;
        LayoutCoreSymbolWithDuration<?> noteWithMaxY;
    }*/

    /**
     *
     * @param majorityStemDirection Majority stem direction among elements in beam
     * @return
     * @throws IM3Exception
     */
    /*private _StemDrawingInfo computeStemDrawingInfo(StemDirection majorityStemDirection) throws IM3Exception {
        _StemDrawingInfo result = new _StemDrawingInfo();

        StemDirection stemDirection = null;
        // check of the stem direction is fixed
        for (LayoutCoreSymbolWithDuration<?> symbolWithDuration : layoutCoreSymbols) {
            if (symbolWithDuration.getCoreSymbol() instanceof SingleFigureAtom) {
                SingleFigureAtom singleFigureAtom = (SingleFigureAtom) symbolWithDuration.getCoreSymbol();
                if (singleFigureAtom.getExplicitStemDirection() != null) {
                    if (stemDirection != null && singleFigureAtom.getExplicitStemDirection() != stemDirection) {
                        throw new IM3Exception("Cannot build a group with two different explicit stem directions"); //TODO mejor mensaje
                    }
                    stemDirection = singleFigureAtom.getExplicitStemDirection();
                }
            }
        }

        if (stemDirection == null) {
            stemDirection = majorityStemDirection;
        }

        // get the lowest and highest pitch
        double min = Double.MAX_VALUE;
        double max = -1;
        LayoutCoreSymbolWithDuration<?> npMin = null;
        LayoutCoreSymbolWithDuration<?> npMax = null;
        for (LayoutCoreSymbolWithDuration<?> symbolWithDuration : layoutCoreSymbols) {
            CoordinateComponent ny = symbolWithDuration.getPosition().getY();
            double y = ny.getAbsoluteValue();
            if (y < min) {
                min = y;
                npMin = symbolWithDuration;
            }
            if (y > max) {
                max = y;
                npMax = symbolWithDuration;
            }
        }
        // see Broido, A., & Dorff, D. (1993). Standard music notation practice
        // (pp. 1–20). Music publisher's association.
        // (notes and stems)
        CoordinateComponent middleLineYCoordinateComponent = layoutCoreSymbols.get(0).getLayoutStaff().getYAtCenterLine();
        double middleLineY = middleLineYCoordinateComponent.getAbsoluteValue();
        double distanceMin = Math.abs(middleLineY - min);
        double distanceMax = Math.abs(middleLineY - max);
        result.maxY = max;
        result.minY = min;
        result.noteWithMaxY = npMax;
        result.noteWithMinY = npMin;
        if (distanceMax >= distanceMin) {
            result.referenceYHeadPossitionForStem = max;
        } else {
            result.referenceYHeadPossitionForStem = min;
        }
        result.height = HEIGHT + max - min;

        if (stemDirection != null && stemDirection != StemDirection.computed) {
            if (stemDirection == StemDirection.down) {
                result.stemUp = false;
            } else {
                result.stemUp = true;
                result.height = -result.height;
            }
        } else {
            if (result.referenceYHeadPossitionForStem <= middleLineY) { // recall y
                // is
                // highest
                // in bottom
                // line than
                // top line
                // stem down
                result.stemUp = false;
            } else {
                // stems up
                result.stemUp = true;
                result.height = -result.height;
            }
        }
        return result;
    }*/

    enum BeamDirection {plain, up, down};

    /**
     * Create the beams between the notes
     */
    public void createBeams() throws IM3Exception {
        group = new Group(this, InteractionElementType.beams);

        if (layoutCoreSymbols.isEmpty()) {
            throw new IM3Exception("Emtpy beam group");
        }

        LayoutCoreSingleFigureAtom from = null;
        LayoutCoreSingleFigureAtom to = null;

        // first compute how many flags each element requires
        int [] flags = new int[this.layoutCoreSymbols.size()];
        int minNumberBeams = Integer.MAX_VALUE;
        int maxNumberBeams = Integer.MIN_VALUE;
        int stemsUp = 0;
        int stemsDown = 0;
        StemDirection stemDirection = null;
        for (int i=0; i<flags.length; i++) {
            flags[i] = this.layoutCoreSymbols.get(i).getNumBeams();
            minNumberBeams = Math.min(minNumberBeams, flags[i]);
            maxNumberBeams = Math.max(maxNumberBeams, flags[i]);
            if (layoutCoreSymbols.get(i) instanceof LayoutCoreSingleFigureAtom) { //TODO Instance of...
                LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom = (LayoutCoreSingleFigureAtom) layoutCoreSymbols.get(i);
                if (layoutCoreSingleFigureAtom.isStemUp()) {
                    stemsUp++;
                } else {
                    stemsDown++;
                }

                if (layoutCoreSingleFigureAtom.getCoreSymbol().getExplicitStemDirection() != null) {
                    if (stemDirection != null && layoutCoreSingleFigureAtom.getCoreSymbol().getExplicitStemDirection() != stemDirection) {
                        throw new IM3Exception("Cannot build a group with two different explicit stem directions"); //TODO mejor mensaje
                    }
                    stemDirection = layoutCoreSingleFigureAtom.getCoreSymbol().getExplicitStemDirection();
                }
            }
        }

        if (stemDirection == null) {
            // if not forced to be up or down
            if (stemsUp >= stemsDown) {
                stemDirection = StemDirection.up;
            } else {
                stemDirection = StemDirection.down;
            }
        }

        if (minNumberBeams == 0) {
            throw new IM3Exception("Cannot build a beam group with 0 beams");
        }

        // change stem directions
        for (LayoutCoreSymbolWithDuration<?> symbolWithDuration : layoutCoreSymbols) {
            if (symbolWithDuration instanceof LayoutCoreSingleFigureAtom) {
                ((LayoutCoreSingleFigureAtom) symbolWithDuration).setStemUp(stemDirection == StemDirection.up); // this will set the correct y
            }
        }

        LinkedList<LayoutCoreSingleFigureAtom> singleFigureAtomLinkedList = new LinkedList<>();
        BeamDirection beamDirection = null;
        Boolean ascendingBeam = null; // positive = up, negative = down
        Double previousY = null;
        BeamDirection previousDirection = null;
        // TODO: 2/5/18 A very simple algorithm is used to compute beams
        for (LayoutCoreSymbolWithDuration<?> symbolWithDuration : layoutCoreSymbols) {
            if (symbolWithDuration instanceof LayoutCoreSingleFigureAtom) {
                singleFigureAtomLinkedList.add((LayoutCoreSingleFigureAtom) symbolWithDuration);
                double y;
                if (stemDirection == StemDirection.up) {
                    y = symbolWithDuration.getTopPitchAbsoluteY();
                } else {
                    y = symbolWithDuration.getBottomPitchAbsoluteY();
                }

                if (from == null) {
                    from = (LayoutCoreSingleFigureAtom) symbolWithDuration;
                }
                to = (LayoutCoreSingleFigureAtom) symbolWithDuration;

                if (previousY != null) {
                    if (beamDirection == null) {
                        if (previousY < y) {
                            beamDirection = BeamDirection.down;
                        } else if (previousY == y) {
                            beamDirection = BeamDirection.plain;
                        } else {
                            beamDirection = BeamDirection.up;
                        }
                    } else if (beamDirection != BeamDirection.up && previousY >= y) {
                            // change of direction
                            beamDirection = BeamDirection.plain;
                            break;
                    } else if (beamDirection != BeamDirection.down && previousY <= y) {
                        // change of direction
                        beamDirection = BeamDirection.plain;
                        break;
                    }
                }
                previousY = y;
            }
        }


        double stemDisplacementY;

       if (beamDirection == BeamDirection.plain) {
            // check the upper position (stem up) or bottom position (stem down)
            CoordinateComponent stemEndYReference = null;
            if (stemDirection == StemDirection.up) {
                double minValue = Double.MAX_VALUE;
                for (LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom : singleFigureAtomLinkedList) {
                    NotePitch referenceNote = layoutCoreSingleFigureAtom.getTopNote();
                    double y = referenceNote.getNoteHeadPosition().getAbsoluteY();
                    if (y < minValue) {
                        minValue = y;
                        stemEndYReference = referenceNote.getNoteHeadPosition().getY();
                    }
                }
                stemDisplacementY = -LayoutConstants.STEM_HEIGHT;
            } else {
                double maxValue = -Double.MAX_VALUE;
                for (LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom : singleFigureAtomLinkedList) {
                    NotePitch referenceNote = layoutCoreSingleFigureAtom.getBottomNote();
                    double y = referenceNote.getNoteHeadPosition().getAbsoluteY();
                    if (maxValue < y) {
                        maxValue = y;
                        stemEndYReference = referenceNote.getNoteHeadPosition().getY();
                    }
                }
                stemDisplacementY = LayoutConstants.STEM_HEIGHT;
            }

            CoordinateComponent toYComponent = new CoordinateComponent(stemEndYReference, stemDisplacementY);
            for (LayoutCoreSymbolWithDuration<?> symbolWithDuration : layoutCoreSymbols) {
                if (symbolWithDuration instanceof LayoutCoreSingleFigureAtom) {
                    LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom = (LayoutCoreSingleFigureAtom) symbolWithDuration;
                    layoutCoreSingleFigureAtom.setStemEndY(new CoordinateComponent (toYComponent), 0);
                }
            }
        } else {
            if (from == null || to == null) {
                throw new IM3Exception("Cannot find a non-rest element in the beam:"  + this);
            }

            //TODO Ángulo máximo
            // TODO: 3/5/18 We are now only connecting beginning with end without taking into account lengths
            double fromX = from.getPosition().getAbsoluteX();
            double toX = to.getPosition().getAbsoluteX();
            double fromY;
            if (stemDirection == StemDirection.up) { //TODO Estamos calculando esto muchas veces
                fromY = from.getBottomPitchAbsoluteY();
                stemDisplacementY = to.getBottomPitchAbsoluteY();
            } else {
                fromY = from.getTopPitchAbsoluteY();
                stemDisplacementY = to.getTopPitchAbsoluteY();
            }

            double xdiff = Math.abs(fromX - toX);
            // angle = Math.asin(h/xdiff);
            double h = Math.abs(fromY - stemDisplacementY);
            angle = Math.atan(h / xdiff);

            double stemFromYAbsolute = from.getStemEnd().getAbsoluteY();
            for (LayoutCoreSymbolWithDuration<?> symbolWithDuration : layoutCoreSymbols) {
                if (symbolWithDuration != from && symbolWithDuration != to && symbolWithDuration instanceof LayoutCoreSingleFigureAtom) {
                    LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom = (LayoutCoreSingleFigureAtom) symbolWithDuration;
                    double ydifferenceFromFirst = Math.abs(symbolWithDuration.getPosition().getAbsoluteX() - fromX) * Math.tan(angle);
                    if (stemDirection != StemDirection.up) {
                        ydifferenceFromFirst = -ydifferenceFromFirst;
                    }
                    //stemFromYAbsolute -= ydifferenceFromFirst;

                    //layoutCoreSingleFigureAtom.setStemEndY(stemFromYAbsolute); 
                }
                // TODO: 3/5/18 Ángulos!!!!! - poner lo que está comentado arriba
            }
        }


        // now draw lines, from outer line to inner line

        for (int nbeams=1; nbeams<=maxNumberBeams; nbeams++) {
            // check beginning and end - may be some gap (e.g. in a sequence 16th 16h eight 16th 16h
            LayoutCoreSymbolWithDuration<?> beamLineFrom = null;
            LayoutCoreSymbolWithDuration<?> beamLineTo = null;

            double ydisplacementOfBeamLine = (nbeams -1) * LayoutConstants.BEAM_SEPARATION;

            if (stemDirection == StemDirection.down) {
                ydisplacementOfBeamLine = -ydisplacementOfBeamLine;
            }

            Integer beamLineFromFlag = null;
            Integer beamLineToFlag = null;
            for (int j=0; j<flags.length; j++) {
                if (flags[j] >= nbeams) {
                    if (beamLineFromFlag == null) {
                        beamLineFromFlag = j;
                    }
                    beamLineToFlag = j;
                    if (layoutCoreSymbols.get(j) instanceof LayoutCoreSingleFigureAtom) {
                        ((LayoutCoreSingleFigureAtom) layoutCoreSymbols.get(j)).removeFlag();
                    }
                } else if (beamLineFromFlag != null || beamLineToFlag != null) {
                    addBeam(layoutCoreSymbols.get(beamLineFromFlag), layoutCoreSymbols.get(beamLineToFlag), ydisplacementOfBeamLine, angle, j==0, stemDirection); // TODO: 4/5/18 ¿Y si la primera nota a la izquierda es un silencio?
                    beamLineFromFlag = null;
                    beamLineToFlag = null;
                }
            }
            if (beamLineFromFlag != null || beamLineToFlag != null) {
                addBeam(layoutCoreSymbols.get(beamLineFromFlag), layoutCoreSymbols.get(beamLineToFlag), ydisplacementOfBeamLine, angle, false, stemDirection);
            }
        }


        /*

        for (BeamLine beamLine : beamLines) {
            beamLine.computeLocalLayout();
        }

        //TODO Thickness
        double th;
        if (stemInfo.stemUp) {
            th = thickness * maxNumberBeams;
        } else {
            th = -thickness * maxNumberBeams;
        }
        double xa = beamLines.get(0).getShape().getStartX();
        double xb = beamLines.get(0).getShape().getEndX();
        double ya = beamLines.get(0).getShape().getStartY() + th;
        double yb = beamLines.get(0).getShape().getEndY() + th;
        this.centerX = (xa + xb) / 2.0;
        this.centerY = (ya + yb) / 2.0;


        // TODO: 1/10/17 Importante !!! No funciona con grupos de varias duraciones
        Integer flags = null;
        for (LayoutCoreSymbol coreSymbol: this.layoutCoreSymbols) {
            LayoutCoreSingleFigureAtom lcoreSingleFigureAtom = ((LayoutCoreSingleFigureAtom)coreSymbol);
            int thisSymbolFlags = lcoreSingleFigureAtom.getCoreSymbol().getAtomFigure().getFigure().getNumFlags();
            if (flags != null) {
                if (flags != thisSymbolFlags) {
                    String message = "Unsupported mixed figure beams, there are figures with number of flags: " + flags + " and " + thisSymbolFlags;
                    if (lcoreSingleFigureAtom.getCoreSymbol().__getID() != null) {
                        message += ", in symbol " + lcoreSingleFigureAtom.getCoreSymbol().__getID();
                    }
                    throw new IM3Exception(message);
                }
            } else {
                flags = thisSymbolFlags;
            }
            lcoreSingleFigureAtom.removeFlag();
        }

        if (this.layoutCoreSymbols.size() < 2) {
            throw new IM3Exception("Cannot create beams with less than 2 elements, there are: " + this.layoutCoreSymbols.size()); // TODO: 1/10/17 Broken beam of 1 note
        }
        LayoutCoreSingleFigureAtom from = layoutCoreSymbols.get(0);
        LayoutCoreSingleFigureAtom to = layoutCoreSymbols.get(this.layoutCoreSymbols.size()-1);

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
        }*/

    }

    /**
     *
     * @param beamLineFrom
     * @param beamLineTo
     * @param displacement
     * @param angle Just used when a beam belongs to a stem (when beamLineFrom = beamLineTo)
     * @param firstNote Just used when a beam belongs to a stem (when beamLineFrom = beamLineTo)
     * @param stemDirection
     * @throws IM3Exception
     */
    private void addBeam(LayoutCoreSymbolWithDuration<?> beamLineFrom, LayoutCoreSymbolWithDuration<?> beamLineTo, double displacement, double angle, boolean firstNote, StemDirection stemDirection) throws IM3Exception {
        if (!(beamLineFrom instanceof LayoutCoreSingleFigureAtom)) { // TODO: 2/5/18 beam start rest
            throw new IM3Exception("TO-DO: " + beamLineFrom.getClass().getName());
        }

        if (!(beamLineTo instanceof LayoutCoreSingleFigureAtom)) { // TODO: 2/5/18 beam start rest
            throw new IM3Exception("TO-DO: " + beamLineTo.getClass().getName());
        }

        LayoutCoreSingleFigureAtom from = (LayoutCoreSingleFigureAtom) beamLineFrom;
        LayoutCoreSingleFigureAtom to = (LayoutCoreSingleFigureAtom) beamLineTo;

        Coordinate coordinateFrom;
        Coordinate coordinateTo;

        if (from == to) { // half beam (e.g. the sixteenth note in a 8th+dot sixteenth
            double ydiference = -LayoutConstants.HALF_STEM_WIDTH * Math.tan(angle);
            /*if (stemDirection == StemDirection.up) {
                ydiference = -ydiference;
            }*/
            if (firstNote) { // draw small stem to the right
                coordinateFrom = new Coordinate(from.getStemEnd().getX(),
                        new CoordinateComponent(from.getStemEnd().getY(), displacement));

                coordinateTo = new Coordinate(new CoordinateComponent(to.getStemEnd().getX(), LayoutConstants.HALF_STEM_WIDTH),
                        new CoordinateComponent(to.getStemEnd().getY(), displacement+ydiference));

            } else { // draw small stem to the left
                coordinateFrom = new Coordinate(new CoordinateComponent(from.getStemEnd().getX(), -LayoutConstants.HALF_STEM_WIDTH),
                        new CoordinateComponent(from.getStemEnd().getY(), displacement+ydiference));

                coordinateTo = new Coordinate(new CoordinateComponent(to.getStemEnd().getX()),
                        new CoordinateComponent(to.getStemEnd().getY(), displacement));

            }
        } else {
            coordinateFrom = new Coordinate(from.getStemEnd().getX(),
                    new CoordinateComponent(from.getStemEnd().getY(), displacement));

            coordinateTo = new Coordinate(to.getStemEnd().getX(),
                    new CoordinateComponent(to.getStemEnd().getY(), displacement));
        }

        GraphicsElement beam = layoutFont.getFontMap().createBeam(this, coordinateFrom, coordinateTo); // TODO ID
        group.add(beam);
    }

    private double computeStemXPosition(LayoutCoreSymbolWithDuration<?> symbolWithDuration) throws IM3Exception {
        if (symbolWithDuration instanceof LayoutCoreSingleFigureAtom) { //TODO Esto de hacer instanceof no está bien...
            LayoutCoreSingleFigureAtom layoutCoreSingleFigureAtom = (LayoutCoreSingleFigureAtom) symbolWithDuration;
            return layoutCoreSingleFigureAtom.getStemEnd().getAbsoluteX();
        } else {
            return symbolWithDuration.getPosition().getAbsoluteX();
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
    protected void doLayout() {
        throw new UnsupportedOperationException("doLayout at " + this.getClass().getName());
    }
}
