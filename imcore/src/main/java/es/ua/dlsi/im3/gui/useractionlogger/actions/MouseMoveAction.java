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

import es.ua.dlsi.im3.gui.useractionlogger.UserActionCategory;

import java.time.Instant;
import java.util.ArrayList;

/**
 *
 * @author drizo
 */
public class MouseMoveAction extends UserAction {
    private static final UserActionCategory CATEGORY = new CategoryMouseMove();
    
    private Instant startInstant;
    private Instant endInstant;
    private final ArrayList<Coordinate> coordinates;
    private String context;
    public MouseMoveAction(String context) {
	super(CATEGORY);
	coordinates = new ArrayList<>();
	this.context = context;
    }
    
    public void startCapturing() {
	startInstant = Instant.now();
    }
    
    public void endCapturing() {
	endInstant = Instant.now();
	
	StringBuilder sb = new StringBuilder();
	boolean first = true;
	for (Coordinate coordinate : coordinates) {
	    if (!first) {
		sb.append(' ');
	    } else {
		first = false;
	    }
	    sb.append(coordinate.getX());
	    sb.append(',');
	    sb.append(coordinate.getY());
	}
	setFields(new Object[] {context, startInstant.toEpochMilli(), endInstant.toEpochMilli(), sb.toString()});
    }
    
    public void addCoordinate(double x, double y) {
	coordinates.add(new Coordinate(x, y));
    }    
    
}
