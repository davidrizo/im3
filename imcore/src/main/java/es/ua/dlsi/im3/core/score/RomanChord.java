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
package es.ua.dlsi.im3.core.score;

import java.util.Objects;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * As it is used for chords like secondary dominants, the major and minor keys have to be taken into account
 * @author drizo
 */
public class RomanChord {
    Key noModeKey;
    ExtendedDegree degree;

    public RomanChord(Key noModeKey, 
    		ExtendedDegree degree) {
	this.noModeKey = noModeKey;
	this.degree = degree;
    }

    public Key getNoModeKey() {
    return noModeKey;
    }

    public ExtendedDegree getDegree() {
	return degree;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 61 * hash + Objects.hashCode(this.noModeKey);
	hash = 61 * hash + Objects.hashCode(this.degree);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final RomanChord other = (RomanChord) obj;
	if (!Objects.equals(this.noModeKey, other.noModeKey)) {
	    return false;
	}
        return Objects.equals(this.degree, other.degree);
    }
    
    public PitchClass getRoot() throws IM3Exception {
	return noModeKey.computeRoot(degree.getDegree());
    }

    @Override
    public String toString() {
	return degree.toString();
    }

    public String toLongString() {
	return degree.toString() + "@" + noModeKey.toString();
    }
    
    
    
}
