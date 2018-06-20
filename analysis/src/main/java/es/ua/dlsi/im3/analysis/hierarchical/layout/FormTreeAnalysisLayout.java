package es.ua.dlsi.im3.analysis.hierarchical.layout;

import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.layout.treedraw.TreeGraphic;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.layout.Coordinate;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.coresymbols.InteractionElementType;
import es.ua.dlsi.im3.core.score.layout.graphics.Canvas;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * It draws the tree analysis in the score canvas
 * @autor drizo
 */
public class FormTreeAnalysisLayout {
    private final ScoreSong scoreSong;
    private final Tree<FormAnalysisTreeNodeLabel> tree;
    private LayoutFont layoutFont;

    public FormTreeAnalysisLayout(LayoutFont layoutFont, Tree<FormAnalysisTreeNodeLabel> tree, ScoreSong scoreSong) {
        this.tree = tree;
        this.scoreSong = scoreSong;
        this.layoutFont = layoutFont;
    }


    public void drawAnalysisInCanvas(HorizontalLayout horizontalLayout, double ySeparationBetweenLevels) throws IM3Exception {
        TreeGraphic treeGraphic = new TreeGraphic(horizontalLayout, tree, layoutFont, ySeparationBetweenLevels);
        Canvas canvas = horizontalLayout.getCanvas();
        canvas.add(treeGraphic);
    }
}
