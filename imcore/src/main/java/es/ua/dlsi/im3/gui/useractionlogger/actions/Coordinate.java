/*
 * Copyright (C) 2014 David Rizo Valero
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

package es.ua.dlsi.im3.gui.useractionlogger.actions;

/**
 *
 * @author drizo
 */
public class Coordinate implements Comparable<Coordinate> {
    double x;
    double y;

    public Coordinate(double x, double y) {
	this.x = x;
	this.y = y;
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    @Override
    public String toString() {
	return new StringBuilder().append(x).append(',').append(y).toString();
    }

	@Override
	public int compareTo(Coordinate o) {
		if (x < o.x) {
			return -1;
		} else if (x > o.x) {
			return 1;
		} else if (y < o.y) {
			return -1;
		} else if (y > o.y) {
			return 1;
		} else {
			return 0;
		}
	}
    
    
    
    
}
