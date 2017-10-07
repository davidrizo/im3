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
package es.ua.dlsi.im3.omr.traced;


/**
 *
 * @author drizo
 */
public class Point {
    long relativeTime;
    double x;
    double y;

    public Point(long relativeTime, double x, double y) {
	this.relativeTime = relativeTime;
	this.x = x;
	this.y = y;
    }

    public long getRelativeTime() {
	return relativeTime;
    }
    
    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    @Override
    public String toString() {
	return "(" + x + ","+ y + ")@" + relativeTime;
    }
    
}
