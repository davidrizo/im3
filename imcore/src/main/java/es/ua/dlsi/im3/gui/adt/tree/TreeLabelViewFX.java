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

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

//TODO Font size
/**
 *
 * @author drizo
 */
public class TreeLabelViewFX  {
    Text label; //TODO que se pueda poner otro tipo
    private final FloatProperty nodex = new SimpleFloatProperty();
    private final FloatProperty nodey = new SimpleFloatProperty();

    
    public TreeLabelViewFX(String text, Color color, double labelAngle) {
        label = new Text();
        if (text != null) {
            this.label.setText(text);
        }
        this.label.setFont(new Font(9));
        this.label.translateXProperty().bind(nodex);
        this.label.translateYProperty().bind(nodey);
        this.label.setFill(color);
        this.label.setRotate(labelAngle);

        // center to x
        //this.label.setTranslateX(-this.label.getWidth()/2);
        //this.label.setTranslateY(-this.label.getHeight()/2);
        //label.setPadding(new Insets(5));
        //Background b = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY));
        //this.label.setBackground(b);
    }

    public float getNodex() {
	return nodex.get();
    }

    public void setNodex(float value) {
	nodex.set(value);
    }

    public FloatProperty nodexProperty() {
	return nodex;
    }

    public float getNodey() {
	return nodey.get();
    }

    public void setNodey(float value) {
	nodey.set(value);
    }

    public FloatProperty nodeyProperty() {
	return nodey;
    }
    
    public Text getLabel() {
	return label;
    }

    public Node getRoot() {
	return label;
    }

    public FloatProperty xConnectionPointProperty() {
        return nodex; // .add(label.getLayoutBounds().getWidth()/2);
    }

    public DoubleBinding topConnectionPointProperty() {
        return nodey.subtract(label.getLayoutBounds().getHeight());
    }

    public FloatProperty bottomConnectionPointProperty() {
        return nodey;
    }

    public ObservableValue<? extends Paint> colorProperty() {
        return label.fillProperty();
    }
}
