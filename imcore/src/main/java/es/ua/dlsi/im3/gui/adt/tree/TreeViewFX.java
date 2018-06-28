/*
 * Copyright (C) 2015 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.gui.adt.tree;

import java.util.ArrayList;

import es.ua.dlsi.im3.core.adt.tree.ILabelColorMapping;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *
 * @author drizo
 */
public class TreeViewFX {
    DoubleProperty xfactor;
	DoubleProperty yfactor;
	boolean showOnlyLeaves;
	Group group;
	TreeLabelViewFX labelView;
	ArrayList<TreeViewFX> children;
	Tree tree;
    ILabelColorMapping labelColorMapping;

	public TreeViewFX(Tree tree, TreeLabelViewFX labelView, DoubleProperty xfactor, DoubleProperty yfactor2, boolean showOnlyLeaves2, ILabelColorMapping labelColorMapping) {
		this.tree = tree;
		this.labelColorMapping = labelColorMapping;
		this.labelView = labelView;
		group = new Group(labelView.getRoot());
		this.children = new ArrayList<>();
		this.xfactor = xfactor;
		this.yfactor = yfactor2;
		this.showOnlyLeaves = showOnlyLeaves2;
	}

    public ILabelColorMapping getLabelColorMapping() {
        return labelColorMapping;
    }

    public DoubleProperty getYfactor() {
		return yfactor;
	}

	public boolean isShowOnlyLeaves() {
		return showOnlyLeaves;
	}

	public Group getRoot() {
		return group;
	}

	public TreeLabelViewFX getLabelView() {
		return labelView;
	}

	void addChild(TreeViewFX cg) {
		group.getChildren().add(cg.getRoot());
		children.add(cg);

		Line line = new Line();
		line.startXProperty().bind(labelView.xConnectionPointProperty());
		line.startYProperty().bind(labelView.bottomConnectionPointProperty());
		line.endXProperty().bind(cg.getLabelView().xConnectionPointProperty());
		line.endYProperty().bind(cg.getLabelView().topConnectionPointProperty());
		line.strokeProperty().bind(cg.getLabelView().colorProperty());


		if (cg.getTree() == tree.getPropagatedFrom()) {
			line.setStrokeWidth(3); // TODO ver
		} else {
			line.setStrokeWidth(1);
		}
		group.getChildren().add(line);
	}

	public ArrayList<TreeViewFX> getChildren() {
		return children;
	}

	public Tree getTree() {
		return tree;
	}

	public void updateTree(Tree tree2) {
		// TODO Auto-generated method stub

	}

	public void replaceWith(TreeViewFX updatedTreeFX) {
		this.tree = updatedTreeFX.getTree();
		this.labelView = updatedTreeFX.getLabelView();
		this.children = updatedTreeFX.getChildren();
		this.group.getChildren().clear();
		this.group.getChildren().addAll(updatedTreeFX.getRoot().getChildren());
	}

	public void clearTree() {
		this.tree = null;
		this.labelView = null;
		this.children.clear();
		this.group.getChildren().clear();
	}

    public DoubleProperty getXFactor() {
	    return xfactor;
    }
}
