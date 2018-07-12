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
package es.ua.dlsi.im3.omr.model.entities;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


/**
 *
 * @author drizo
 */
public class Stroke {
    /**
     * List of coordinates
     */
    private List<Point> points;
    /**
     * Time stamp of the first point. Time is represented as the number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    long firstPointTime;

    public Stroke() {
	    points = new LinkedList<>();
    }

    public void addPoint(double x, double y) {
        if (points.isEmpty()) {
            firstPointTime = new Date().getTime();
            points.add(new Point(0, x, y));
        } else {
            points.add(new Point(new Date().getTime() - firstPointTime, x, y));
        }
    }

    public void setFirstPointTime(long firstPointTime) {
        this.firstPointTime = firstPointTime;
    }

    public long getFirstPointTime() {
        return firstPointTime;
    }

    public List<Point> pointsProperty() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stroke stroke = (Stroke) o;
        return firstPointTime == stroke.firstPointTime &&
                Objects.equals(points, stroke.points);
    }

    @Override
    public int hashCode() {

        return Objects.hash(points, firstPointTime);
    }
}
