/*
 * Copyright (C) 2016 David Rizo Valero
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
package es.ua.dlsi.im3.omr.interactive.components;

import es.ua.dlsi.im3.omr.traced.Point;
import es.ua.dlsi.im3.omr.traced.Stroke;
import javafx.collections.ListChangeListener;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author drizo
 */
public class StrokeView extends Path {

    Stroke stroke;
    double lastX;
    double lastY;

    public StrokeView(Stroke stroke) {
	this.stroke = stroke;
	lastX = -1;
	lastY = -1;
	
	//System.out.println("New stroke");
	for (Point p : stroke.pointsProperty()) {
	    drawLine(p.getX(), p.getY()); // TODO Podríamos pintar la velocidad con colores
	}
	
	stroke.pointsProperty().addListener(new ListChangeListener<Point>() {
	    @Override
	    public void onChanged(Change<? extends Point> c) {
		//System.out.println(this.toString() + " " + c.toString());
		while (c.next()) { // use alwaus this scheme
		    if (c.wasPermutated()) {
			for (int i = c.getFrom(); i < c.getTo(); ++i) {
			    //permutate //TOO
			}
		    } else if (c.wasUpdated()) {
			//update item //TODO
		    } else {
			for (Point remitem : c.getRemoved()) {
			    //remitem.remove(Outer.this); //TODO
			}
			for (Point newPoint : c.getAddedSubList()) {
			    drawLine(newPoint.getX(), newPoint.getY());			    
			}
		    }
		}
	    }
	}
	);
    }

    public Stroke getStrokeObject() {
	return stroke;
    }

    public void addPoint(double x, double y) {
	stroke.addPoint(x, y);
	//drawLine(x, y); invoked by the listener (see constructor)
    }

    final public void drawLine(double x, double y) {
	if (lastX != -1) {
	    getElements().add(new MoveTo(lastX, lastY));
	    getElements().add(new LineTo(x, y));
	}
	lastX = x;
	lastY = y;
    }
}
