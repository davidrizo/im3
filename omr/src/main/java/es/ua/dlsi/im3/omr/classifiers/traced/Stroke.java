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
package es.ua.dlsi.im3.omr.classifiers.traced;

import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author drizo
 */
public class Stroke {
    ObservableList<Point> points;
    long firstPointTime;

    public Stroke() {
	points = FXCollections.observableArrayList();
    }

    public void addPoint(double x, double y) {
	if (points.isEmpty()) {
	    firstPointTime = new Date().getTime();
	    points.add(new Point(0, x, y));
	} else {
	    points.add(new Point(new Date().getTime() - firstPointTime, x, y));
	}
	
    }

    public ObservableList<Point> pointsProperty() {
	return points;
    }

    @Override
    public String toString() {
	if (points.isEmpty()) {
	    return "No points";
	} else {
	    return "First point = " + points.get(0).toString();
	}
    }

    public void addPoint(Point point) {
	this.points.add(point);
    }
    
    

    
    
}
