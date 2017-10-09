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

package es.ua.dlsi.im3.gui.useractionlogger;

/**
 *
 * @author drizo
 */
public class UserActionCategory {
    private final String name;
    private final String[] fieldNames;

    /**
     * 
     * @param fieldNames
     * @param name Sequence of category, subcategory names
     */
    public UserActionCategory(String[] fieldNames, String ... name) {
	StringBuilder sb = null;
	for (String string : name) {
	    if (sb == null) {
		sb = new StringBuilder();
		sb.append(string);
	    } else {
		sb.append('.');
		sb.append(string);
	    }
	}
	if (sb == null) {
	    throw new RuntimeException("No name given for the user action category");
	} else {
	    this.name = sb.toString();
	}
	this.fieldNames = fieldNames;
    }

    public String getName() {
	return name;
    }

    public String[] getFieldNames() {
	return fieldNames;
    }
}
