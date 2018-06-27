package es.ua.dlsi.im3.gui.adt.tree;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.adt.tree.TreeException;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;

public class TreeFXBuilder {
    /**
     *
     * @param stree
     * @param yfactor
     * @param showOnlyLeaves
     * @param useHorizontalPosition If true, all labels must have their getPredefinedHorizontalPosition() not null
     * @return
     * @throws TreeException
     * @throws IM3Exception
     */
	public TreeViewFX create(Tree stree, DoubleProperty xfactor, DoubleProperty yfactor, boolean showOnlyLeaves, boolean useHorizontalPosition)
			throws TreeException, IM3Exception {
		if (stree == null) {
			throw new IM3Exception("Cannot build an empty tree");
		}
		// int depth = stree.getLevel();

		TreeLabelViewFX nodeFX;
		if (!showOnlyLeaves || stree.isLeaf()) {
			nodeFX = new TreeLabelViewFX(stree.getLabel().getStringLabel());
		} else {
			nodeFX = new TreeLabelViewFX(null);
		}
		TreeViewFX treeViewFX = new TreeViewFX(stree, nodeFX, xfactor, yfactor, showOnlyLeaves);
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
				TreeViewFX cg = create(stree.getChild(i), xfactor, yfactor, showOnlyLeaves, useHorizontalPosition);
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
			NumberBinding sum = first.getLabelView().nodexProperty().add(last.getLabelView().nodexProperty());
			nodeFX.nodexProperty().bind(sum.divide(2.0));
		}
		nodeFX.nodeyProperty().bind(yfactor.multiply(stree.getLevel()));

		return treeViewFX;

	}

	public void update(TreeViewFX treeFX, Tree tree, boolean useHorizontalPosition) throws IM3Exception, TreeException {
		TreeViewFX updatedTreeFX = create(tree, treeFX.getXFactor(), treeFX.getYfactor(), treeFX.isShowOnlyLeaves(), useHorizontalPosition);
		treeFX.replaceWith(updatedTreeFX);
	}
}
