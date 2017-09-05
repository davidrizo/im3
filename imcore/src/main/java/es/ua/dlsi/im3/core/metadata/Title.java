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
package es.ua.dlsi.im3.core.metadata;


/**
 *
 * @author drizo
 */
public class Title {
    String type;
    String value;
    
    public Title() {
    }
    
    public Title(String value) {
    		this.value = value;
    }
    public Title(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Override
    public String toString() {
	return value;
    }

    
    
    
}
