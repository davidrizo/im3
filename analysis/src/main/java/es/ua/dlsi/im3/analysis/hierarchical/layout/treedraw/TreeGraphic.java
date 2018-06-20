package es.ua.dlsi.im3.analysis.hierarchical.layout.treedraw;

import es.ua.dlsi.im3.analysis.hierarchical.forms.DivisionLabel;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.ScoreAnalysisHook;
import es.ua.dlsi.im3.core.score.layout.*;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;
import es.ua.dlsi.im3.core.score.layout.graphics.Text;

/**
 * A whole tree, contains root and children connected by lines
 * @autor drizo
 */
public class TreeGraphic extends Group {
    private static final InteractionElementType INTERACTION_ELEMENT_TYPE = InteractionElementType.analysis; //TODO NotationType and interactionElemntType
    Text text;
    public TreeGraphic(HorizontalLayout horizontalLayout, Tree<FormAnalysisTreeNodeLabel> tree, LayoutFont layoutFont, double ySeparationBetweenLevels) throws IM3Exception {
        super(null, INTERACTION_ELEMENT_TYPE);

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


        text = new Text(null, INTERACTION_ELEMENT_TYPE, layoutFont, tree.getLabel().getStringLabel(), rootCoordinate);
        this.add(text);

        // draw children
        for (int i=0; i<tree.getNumChildren(); i++) {
            TreeGraphic child = new TreeGraphic(horizontalLayout, tree.getChild(i), layoutFont, ySeparationBetweenLevels);
            this.add(child);

            Line line = new Line(null, INTERACTION_ELEMENT_TYPE, rootCoordinate, child.getPosition());
            line.setThickness(4); //TODO
            this.add(line);
        }
    }
}
