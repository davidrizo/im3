package es.ua.dlsi.im3.analysis.hierarchical.layout.treedraw;

import es.ua.dlsi.im3.analysis.hierarchical.forms.DivisionLabel;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.*;

/**
 * A whole tree, contains root and children connected by lines
 * @autor drizo
 */
public class TreeGraphic extends Group {
    private static final InteractionElementType INTERACTION_ELEMENT_TYPE = InteractionElementType.analysis; //TODO NotationType and interactionElemntType
    Text text;
    public TreeGraphic(HorizontalLayout horizontalLayout, Tree<FormAnalysisTreeNodeLabel> tree, LayoutFont layoutFont, double ySeparationBetweenLevels, RGBA rgba) throws IM3Exception {
        super(new NotationSymbol() {
            @Override
            public GraphicsElement getGraphics() {
                return null;
            }

            @Override
            protected void doLayout() throws IM3Exception {

            }
        }, INTERACTION_ELEMENT_TYPE);

        Coordinate rootCoordinate;
        if (tree.getLabel() instanceof DivisionLabel) {
            DivisionLabel divisionLabel = (DivisionLabel) tree.getLabel();
            ScoreAnalysisHook hook = divisionLabel.getScoreAnalysisHookStart();

            NotationSymbol notationSymbol = horizontalLayout.getCoreSymbolView(hook);
            if (notationSymbol == null) {
                throw new IM3Exception("Cannot find a view for analysis hook " + hook);
            }

            rootCoordinate = new Coordinate(notationSymbol.getPosition().getX(), new CoordinateComponent(ySeparationBetweenLevels * tree.getLevel()));
        } else {
            rootCoordinate = new Coordinate(); //TODO
        }


        text = new Text(this.getNotationSymbol(), INTERACTION_ELEMENT_TYPE, layoutFont, tree.getLabel().getStringLabel(), rootCoordinate);
        this.add(text);
        text.setRGBColor(rgba);

        // draw children
        for (int i=0; i<tree.getNumChildren(); i++) {
            TreeGraphic child = new TreeGraphic(horizontalLayout, tree.getChild(i), layoutFont, ySeparationBetweenLevels, rgba);
            this.add(child);

            Line line = new Line(this.getNotationSymbol(), INTERACTION_ELEMENT_TYPE, rootCoordinate, child.getPosition());
            line.setRGBColor(rgba);
            line.setThickness(4); //TODO
            this.add(line);
        }
    }
}
