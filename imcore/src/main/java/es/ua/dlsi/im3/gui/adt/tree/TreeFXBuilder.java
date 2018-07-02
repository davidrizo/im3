package es.ua.dlsi.im3.gui.adt.tree;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.ILabelColorMapping;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;

import java.util.HashMap;

public class TreeFXBuilder {
    /**
     *
     * @param stree
     * @param yfactor
     * @param showOnlyLeaves
     * @param useHorizontalPosition If true, all labels must have their getPredefinedHorizontalPosition() not null
     * @param labelAngle
     * @param topDown It drawn from the top to down
     * @return
     * @throws TreeException
     * @throws IM3Exception
     */
	public TreeViewFX create(Tree stree, DoubleProperty xfactor, DoubleProperty yfactor, boolean showOnlyLeaves, boolean useHorizontalPosition, ILabelColorMapping labelColorMapping, boolean useStraightLines, double labelAngle, boolean topDown)
			throws IM3Exception {
		if (stree == null) {
			throw new IM3Exception("Cannot build an empty tree");
		}
		TreeLabelViewFX nodeFX;
		double angle;
		if (stree.isLeaf()) {
		    angle = labelAngle;
        } else {
		    angle = 0;
        }
		if (!showOnlyLeaves || stree.isLeaf()) {
            String stringLabel = stree.getLabel().getStringLabel();
		    Color color = Color.BLACK;
		    if (labelColorMapping != null) {
		        String colorName = labelColorMapping.getColorMapping(stringLabel);
		        if (colorName != null) {
		            color = Color.web(colorName);
                }
            }
			nodeFX = new TreeLabelViewFX(stringLabel, color, angle);
		} else {
			nodeFX = new TreeLabelViewFX(null, Color.BLACK, angle);
		}

		TreeViewFX treeViewFX = new TreeViewFX(stree, nodeFX, xfactor, yfactor, showOnlyLeaves, labelColorMapping, useStraightLines, labelAngle, topDown);
		//stree.setView(treeViewFX);

        if (!useHorizontalPosition) {
            throw new UnsupportedOperationException("TO-DO: calcular posición horizontal nodos árbol");
        } else {
            if (stree.getLabel().getPredefinedHorizontalPosition() != null) {
                nodeFX.nodexProperty().bind(xfactor.multiply(stree.getLabel().getPredefinedHorizontalPosition()));
            }
        }
		if (!stree.isLeaf()) {
			for (int i = 0; i < stree.getNumChildren(); i++) {
				TreeViewFX cg = create(stree.getChild(i), xfactor, yfactor, showOnlyLeaves, useHorizontalPosition, labelColorMapping, useStraightLines, labelAngle, topDown);
				treeViewFX.addChild(cg);
			}

			// set middle point x
			TreeViewFX first = treeViewFX.getChildren().get(0); // we know it is
																// not empty
			TreeViewFX last = treeViewFX.getChildren().get(treeViewFX.getChildren().size() - 1); // we
																									// know
																									// it
																									// is
																									// not
																									// empty
            if (!useHorizontalPosition)
            {
                NumberBinding sum = first.getLabelView().nodexProperty().add(last.getLabelView().nodexProperty());
                nodeFX.nodexProperty().bind(sum.divide(2.0));
            }
		}
		if (topDown) {
            nodeFX.nodeyProperty().bind(yfactor.multiply(stree.getLevel()));
        } else {
            nodeFX.nodeyProperty().bind(yfactor.multiply(stree.getLevel()).multiply(-1));
        }

		return treeViewFX;

	}

	public void update(TreeViewFX treeFX, Tree tree, boolean useHorizontalPosition) throws IM3Exception, TreeException {
		TreeViewFX updatedTreeFX = create(tree, treeFX.getXFactor(), treeFX.getYfactor(), treeFX.isShowOnlyLeaves(), useHorizontalPosition, treeFX.getLabelColorMapping(), treeFX.isUseStraightLines(), treeFX.getLabelAngle(), treeFX.isTopDown());
		treeFX.replaceWith(updatedTreeFX);
	}

}
