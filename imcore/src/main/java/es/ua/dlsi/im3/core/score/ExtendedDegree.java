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
 *
 * @author drizo
 */
public class ExtendedDegree {
	Degree degree;
	/**
	 * 7, 9, 13... null if not present
	 */
	Integer extension;

	public ExtendedDegree(Degree degree, Integer extension) {
		this.degree = degree;
		this.extension = extension;
	}

	// TODO Optimizar
	private static final Degree[] DEGREES = { Degree.VII, Degree.VI, Degree.V, Degree.IV, Degree.III, Degree.II,
			Degree.I };

	/**
	 * 
	 * @param string
	 *            e.g V, or V9
	 */
	public ExtendedDegree(String string) throws IM3Exception {
		String s = string.toUpperCase();
		for (Degree d : DEGREES) {
			if (s.startsWith(d.name())) {
				degree = d;
				String extStr = s.substring(d.name().length());
				if (extStr != null && extStr.length() > 0) {
					try {
						extension = Integer.parseInt(extStr);
					} catch (Throwable t) {
						throw new IM3Exception("This is not a valid extended degree: " + string);
					}
				}
				break;
			}
		}
		if (degree == null) {
			throw new IM3Exception("This is not a valid extended degree: " + string);
		}
		/*
		 * Pattern p = Pattern.compile("[IV]+[0,9]*"); Matcher m =
		 * p.matcher(string); if (!m.matches()) { throw new IM3Exception(
		 * "This is not a valid extended degree: " + string); } else { try {
		 * degree = Degree.valueOf(m.group()); if (m.find()) { extension =
		 * Integer.parseInt(m.group()); } if (m.find()) { // still something
		 * throw new IM3Exception("This is not a valid extended degree: " +
		 * string); } } catch (Throwable t) { throw new IM3Exception(
		 * "This is not a valid extended degree: " + string); } }
		 */

	}

	/**
	 * 
	 * @param string
	 * @return null if it is not an extended degree
	 */
	public static ExtendedDegree parseExtendedDegree(String string) {
		try {
			return new ExtendedDegree(string);
		} catch (IM3Exception ex) {
			return null;
		}
	}

	public ExtendedDegree(Degree degree) {
		this.degree = degree;
	}

	public Degree getDegree() {
		return degree;
	}

	public void setDegree(Degree degree) {
		this.degree = degree;
	}

	public Integer getExtension() {
		return extension;
	}

	public void setExtension(Integer extension) {
		this.extension = extension;
	}

	public boolean hasExtension() {
		return this.extension != null;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.degree);
		hash = 83 * hash + Objects.hashCode(this.extension);
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
		final ExtendedDegree other = (ExtendedDegree) obj;
		if (this.degree != other.degree) {
			return false;
		}
        return Objects.equals(this.extension, other.extension);
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(degree.toString());
		if (extension != null) {
			sb.append(extension);
		}
		return sb.toString();
	}

}
